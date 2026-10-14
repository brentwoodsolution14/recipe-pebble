package com.recipepebble.book.domain.usecase

import com.recipepebble.book.domain.model.Recipe
import com.recipepebble.book.domain.repository.RecipeRepository

class ToggleFavouriteUseCase(private val repository: RecipeRepository) {

    operator fun invoke(id: String): List<Recipe> {
        val current = repository.find(id) ?: return repository.load()
        return repository.replace(current.withFavourite(!current.favourite))
    }
}
