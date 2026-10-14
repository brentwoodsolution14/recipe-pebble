package com.recipepebble.book.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipepebble.book.core.config.GameConfig
import com.recipepebble.book.data.sample.SampleData
import com.recipepebble.book.domain.model.Ingredient
import com.recipepebble.book.domain.model.Recipe
import com.recipepebble.book.domain.model.RecipeStep
import com.recipepebble.book.domain.repository.SettingsRepository
import com.recipepebble.book.domain.usecase.SaveRecipeUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val saveRecipe: SaveRecipeUseCase,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(seed())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private fun seed(): GameUiState = GameUiState(
        stage = GameConfig.STAGE_TITLE,
        name = SampleData.draftName,
        minutes = settings.load().defaultMinutes,
        tags = SampleData.draftTags,
        ingredients = SampleData.draftIngredients,
        steps = SampleData.draftSteps
    )

    fun onNameChanged(value: String) {
        val trimmed = value.take(GameConfig.MAX_NAME_LENGTH)
        _state.value = _state.value.copy(name = trimmed, nameError = false)
    }

    fun onMinutesDelta(delta: Int) {
        val next = _state.value.minutes + delta * GameConfig.MINUTE_STEP
        val clamped = when {
            next < GameConfig.MIN_MINUTES -> GameConfig.MIN_MINUTES
            next > GameConfig.MAX_MINUTES -> GameConfig.MAX_MINUTES
            else -> next
        }
        _state.value = _state.value.copy(minutes = clamped)
    }

    fun onTagToggled(label: String) {
        val current = _state.value.tags
        val next = when {
            current.contains(label) -> current - label
            current.size >= GameConfig.MAX_TAGS_PER_RECIPE -> current
            else -> current + label
        }
        _state.value = _state.value.copy(tags = next)
    }

    fun onAddIngredient(name: String, amount: String) {
        if (name.isBlank()) return
        val current = _state.value.ingredients
        if (current.size >= GameConfig.MAX_INGREDIENTS) return
        val item = Ingredient(
            name = name.trim().take(GameConfig.MAX_INGREDIENT_LENGTH),
            amount = amount.trim().take(GameConfig.MAX_INGREDIENT_LENGTH)
        )
        _state.value = _state.value.copy(
            ingredients = current + item,
            warning = GameUiState.Warning.NONE
        )
    }

    fun onRemoveIngredient(index: Int) {
        val current = _state.value.ingredients
        if (index !in current.indices) return
        val next = current.toMutableList()
        next.removeAt(index)
        _state.value = _state.value.copy(ingredients = next)
    }

    fun onToggleIngredient(index: Int) {
        val current = _state.value.ingredients
        if (index !in current.indices) return
        val next = current.toMutableList()
        next[index] = next[index].toggled()
        _state.value = _state.value.copy(ingredients = next)
    }

    fun onAddStep(text: String) {
        if (text.isBlank()) return
        val current = _state.value.steps
        if (current.size >= GameConfig.MAX_STEPS) return
        val step = RecipeStep(text.trim().take(GameConfig.MAX_STEP_LENGTH))
        _state.value = _state.value.copy(
            steps = current + step,
            warning = GameUiState.Warning.NONE
        )
    }

    fun onMoveStep(index: Int, delta: Int) {
        val current = _state.value.steps
        val target = index + delta
        if (index !in current.indices || target !in current.indices) return
        val next = current.toMutableList()
        val moved = next.removeAt(index)
        next.add(target, moved)
        _state.value = _state.value.copy(steps = next)
    }

    fun onPrimary() {
        val current = _state.value
        if (current.saving) return
        when (current.stage) {
            GameConfig.STAGE_TITLE -> {
                if (current.name.isBlank()) {
                    _state.value = current.copy(nameError = true)
                } else {
                    _state.value = current.copy(
                        stage = GameConfig.STAGE_INGREDIENTS,
                        nameError = false,
                        warning = GameUiState.Warning.NONE
                    )
                }
            }

            GameConfig.STAGE_INGREDIENTS -> {
                if (current.ingredients.isEmpty()) {
                    _state.value = current.copy(warning = GameUiState.Warning.INGREDIENTS)
                } else {
                    _state.value = current.copy(
                        stage = GameConfig.STAGE_METHOD,
                        warning = GameUiState.Warning.NONE
                    )
                }
            }

            else -> {
                if (current.steps.isEmpty()) {
                    _state.value = current.copy(warning = GameUiState.Warning.STEPS)
                } else {
                    persist(current)
                }
            }
        }
    }

    fun onSecondary() {
        val current = _state.value
        if (current.saving) return
        if (current.stage == GameConfig.STAGE_TITLE) {
            _state.value = current.copy(leaving = true)
        } else {
            _state.value = current.copy(
                stage = current.stage - 1,
                warning = GameUiState.Warning.NONE
            )
        }
    }

    fun onSaveDraft() {
        val current = _state.value
        if (current.saving || current.name.isBlank()) return
        persist(current)
    }

    fun consumeSaved() {
        _state.value = _state.value.copy(savedId = "")
    }

    fun consumeLeaving() {
        _state.value = _state.value.copy(leaving = false)
    }

    fun clearWarning() {
        if (_state.value.warning == GameUiState.Warning.NONE) return
        _state.value = _state.value.copy(warning = GameUiState.Warning.NONE)
    }

    private fun persist(source: GameUiState) {
        _state.value = source.copy(saving = true, warning = GameUiState.Warning.NONE)
        viewModelScope.launch {
            delay(GameConfig.SAVE_DELAY_MS)
            val id = System.currentTimeMillis().toString()
            val recipe = Recipe(
                id = id,
                name = source.name.trim(),
                minutes = source.minutes,
                favourite = false,
                spriteKey = SampleData.spriteFor(id),
                tags = source.tags,
                ingredients = source.ingredients,
                steps = source.steps
            )
            saveRecipe(recipe)
            _state.value = _state.value.copy(saving = false, savedId = id)
        }
    }
}
