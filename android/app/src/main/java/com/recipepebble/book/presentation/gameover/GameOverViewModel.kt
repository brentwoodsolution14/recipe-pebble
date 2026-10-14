package com.recipepebble.book.presentation.gameover

import androidx.lifecycle.ViewModel
import com.recipepebble.book.domain.usecase.DeleteRecipeUseCase
import com.recipepebble.book.domain.usecase.GetRecipesUseCase
import com.recipepebble.book.domain.usecase.ToggleFavouriteUseCase
import com.recipepebble.book.domain.usecase.ToggleIngredientUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel(
    private val getRecipes: GetRecipesUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase,
    private val toggleIngredient: ToggleIngredientUseCase,
    private val deleteRecipe: DeleteRecipeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    private var recipeId: String = ""

    fun load(id: String, savedNow: Boolean) {
        if (recipeId == id && _state.value.recipe != null) return
        recipeId = id
        val recipe = getRecipes.byId(id) ?: getRecipes.all().firstOrNull()
        _state.value = GameOverUiState(recipe = recipe, savedNow = savedNow)
    }

    fun onFavourite() {
        val current = _state.value.recipe ?: return
        toggleFavourite(current.id)
        _state.value = _state.value.copy(recipe = getRecipes.byId(current.id))
    }

    fun onIngredientToggled(index: Int) {
        val current = _state.value.recipe ?: return
        val updated = toggleIngredient(current.id, index) ?: return
        _state.value = _state.value.copy(recipe = updated)
    }

    fun onCookAgain() {
        val current = _state.value.recipe ?: return
        val updated = toggleIngredient.clearAll(current.id) ?: return
        _state.value = _state.value.copy(recipe = updated, savedNow = false)
    }

    fun onDelete() {
        val current = _state.value.recipe ?: return
        deleteRecipe(current.id)
        _state.value = _state.value.copy(deleted = true)
    }

    fun consumeDeleted() {
        _state.value = _state.value.copy(deleted = false)
    }
}
