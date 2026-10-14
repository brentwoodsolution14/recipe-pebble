package com.recipepebble.book.domain.model

enum class RecipeTag(val label: String) {
    BREAKFAST("BREAKFAST"),
    SOUP("SOUP"),
    PASTA("PASTA"),
    BAKING("BAKING"),
    SALAD("SALAD"),
    DINNER("DINNER"),
    SWEET("SWEET"),
    QUICK("QUICK");

    companion object {
        fun fromLabel(label: String): RecipeTag? = values().firstOrNull { it.label == label }

        fun labels(): List<String> = values().map { it.label }
    }
}
