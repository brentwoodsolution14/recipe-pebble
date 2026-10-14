package com.recipepebble.book.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.recipepebble.book.domain.repository.RecipeRepository
import com.recipepebble.book.domain.repository.SettingsRepository
import com.recipepebble.book.domain.usecase.DeleteRecipeUseCase
import com.recipepebble.book.domain.usecase.GetRecipesUseCase
import com.recipepebble.book.domain.usecase.SaveRecipeUseCase
import com.recipepebble.book.domain.usecase.ToggleFavouriteUseCase
import com.recipepebble.book.domain.usecase.ToggleIngredientUseCase
import com.recipepebble.book.presentation.game.GameViewModel
import com.recipepebble.book.presentation.gameover.GameOverViewModel
import com.recipepebble.book.presentation.menu.MenuViewModel
import com.recipepebble.book.presentation.splash.SplashViewModel

class ViewModelFactory(
    private val getRecipes: GetRecipesUseCase,
    private val saveRecipe: SaveRecipeUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase,
    private val deleteRecipe: DeleteRecipeUseCase,
    private val toggleIngredient: ToggleIngredientUseCase,
    private val settings: SettingsRepository,
    private val recipes: RecipeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(SplashViewModel::class.java) ->
            SplashViewModel() as T

        modelClass.isAssignableFrom(MenuViewModel::class.java) ->
            MenuViewModel(getRecipes, toggleFavourite, settings) as T

        modelClass.isAssignableFrom(GameViewModel::class.java) ->
            GameViewModel(saveRecipe, settings) as T

        modelClass.isAssignableFrom(GameOverViewModel::class.java) ->
            GameOverViewModel(getRecipes, toggleFavourite, toggleIngredient, deleteRecipe) as T

        else -> throw IllegalArgumentException("Unknown ViewModel " + modelClass.name)
    }

    fun resetBook() {
        recipes.resetToSample()
    }
}
