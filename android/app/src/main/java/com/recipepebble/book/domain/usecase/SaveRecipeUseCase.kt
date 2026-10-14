package com.recipepebble.book.domain.usecase

import com.recipepebble.book.domain.model.Recipe
import com.recipepebble.book.domain.repository.RecipeRepository

class SaveRecipeUseCase(private val repository: RecipeRepository) {

    operator fun invoke(recipe: Recipe): List<Recipe> {
        val existing = repository.find(recipe.id)
        return if (existing == null) repository.add(recipe) else repository.replace(recipe)
    }
}
