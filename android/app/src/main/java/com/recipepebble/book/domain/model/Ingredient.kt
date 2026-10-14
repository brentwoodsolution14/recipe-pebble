package com.recipepebble.book.domain.model

data class Ingredient(
    val name: String,
    val amount: String,
    val checked: Boolean = false
) {
    fun toggled(): Ingredient = copy(checked = !checked)

    fun cleared(): Ingredient = copy(checked = false)
}
