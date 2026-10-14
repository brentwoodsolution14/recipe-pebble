package com.recipepebble.book.domain.usecase

import com.recipepebble.book.domain.model.Recipe
import com.recipepebble.book.domain.repository.RecipeRepository

class ToggleIngredientUseCase(private val repository: RecipeRepository) {

    operator fun invoke(id: String, index: Int): Recipe? {
        val current = repository.find(id) ?: return null
        val updated = current.withIngredientToggled(index)
        repository.replace(updated)
        return updated
    }

    fun clearAll(id: String): Recipe? {
        val current = repository.find(id) ?: return null
        val updated = current.withAllUnchecked()
        repository.replace(updated)
        return updated
    }
}
