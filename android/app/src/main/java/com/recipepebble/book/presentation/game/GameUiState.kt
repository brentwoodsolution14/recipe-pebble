package com.recipepebble.book.presentation.game

import com.recipepebble.book.core.config.GameConfig
import com.recipepebble.book.domain.model.Ingredient
import com.recipepebble.book.domain.model.RecipeStep

data class GameUiState(
    val stage: Int = GameConfig.STAGE_TITLE,
    val name: String = "",
    val minutes: Int = GameConfig.DEFAULT_MINUTES,
    val tags: List<String> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<RecipeStep> = emptyList(),
    val nameError: Boolean = false,
    val warning: Warning = Warning.NONE,
    val saving: Boolean = false,
    val savedId: String = "",
    val leaving: Boolean = false
) {
    enum class Warning { NONE, INGREDIENTS, STEPS }

    val isLastStage: Boolean
        get() = stage == GameConfig.STAGE_METHOD

    val primaryEnabled: Boolean
        get() = when (stage) {
            GameConfig.STAGE_TITLE -> name.isNotBlank()
            GameConfig.STAGE_INGREDIENTS -> ingredients.isNotEmpty()
            else -> steps.isNotEmpty()
        }
}
