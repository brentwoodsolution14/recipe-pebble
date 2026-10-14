package com.recipepebble.book.presentation.gameover

import com.recipepebble.book.domain.model.Recipe

data class GameOverUiState(
    val recipe: Recipe? = null,
    val savedNow: Boolean = false,
    val deleted: Boolean = false
) {
    val minutes: Int
        get() = recipe?.minutes ?: 0

    val ingredientCount: Int
        get() = recipe?.ingredients?.size ?: 0

    val stepCount: Int
        get() = recipe?.steps?.size ?: 0
}
