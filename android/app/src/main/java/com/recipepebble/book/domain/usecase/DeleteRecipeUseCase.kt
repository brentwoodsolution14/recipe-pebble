package com.recipepebble.book.domain.usecase

import com.recipepebble.book.domain.model.Recipe
import com.recipepebble.book.domain.repository.RecipeRepository

class DeleteRecipeUseCase(private val repository: RecipeRepository) {

    operator fun invoke(id: String): List<Recipe> = repository.remove(id)
}
