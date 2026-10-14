package com.recipepebble.book.domain.usecase

import com.recipepebble.book.domain.model.Recipe
import com.recipepebble.book.domain.repository.RecipeRepository

class GetRecipesUseCase(private val repository: RecipeRepository) {

    operator fun invoke(favouritesOnly: Boolean): List<Recipe> {
        val all = repository.load()
        return if (favouritesOnly) all.filter { it.favourite } else all
    }

    fun all(): List<Recipe> = repository.load()

    fun byId(id: String): Recipe? = repository.find(id)
}
