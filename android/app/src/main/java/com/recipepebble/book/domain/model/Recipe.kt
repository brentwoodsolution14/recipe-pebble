package com.recipepebble.book.domain.model

data class Recipe(
    val id: String,
    val name: String,
    val minutes: Int,
    val favourite: Boolean,
    val spriteKey: String,
    val tags: List<String>,
    val ingredients: List<Ingredient>,
    val steps: List<RecipeStep>
) {
    val checkedCount: Int
        get() = ingredients.count { it.checked }

    fun withFavourite(value: Boolean): Recipe = copy(favourite = value)

    fun withIngredientToggled(index: Int): Recipe {
        if (index !in ingredients.indices) return this
        val updated = ingredients.toMutableList()
        updated[index] = updated[index].toggled()
        return copy(ingredients = updated)
    }

    fun withAllUnchecked(): Recipe = copy(ingredients = ingredients.map { it.cleared() })
}
