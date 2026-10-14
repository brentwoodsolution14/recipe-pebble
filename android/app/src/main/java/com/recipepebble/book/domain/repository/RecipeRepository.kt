package com.recipepebble.book.domain.repository

import com.recipepebble.book.domain.model.Recipe

interface RecipeRepository {
    fun load(): List<Recipe>

    fun save(recipes: List<Recipe>)

    fun add(recipe: Recipe): List<Recipe>

    fun replace(recipe: Recipe): List<Recipe>

    fun remove(id: String): List<Recipe>

    fun find(id: String): Recipe?

    fun resetToSample(): List<Recipe>
}
