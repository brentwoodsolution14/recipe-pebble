package com.recipepebble.book.presentation.menu

import com.recipepebble.book.domain.model.Recipe

data class MenuUiState(
    val loading: Boolean = true,
    val favouritesOnly: Boolean = false,
    val visible: List<Recipe> = emptyList(),
    val totalRecipes: Int = 0,
    val favouriteCount: Int = 0,
    val tagCount: Int = 0
) {
    val isEmpty: Boolean
        get() = !loading && visible.isEmpty()

    val statsVisible: Int
        get() = listOf(totalRecipes, favouriteCount, tagCount).count { it > 0 }
}
