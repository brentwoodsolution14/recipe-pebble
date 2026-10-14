package com.recipepebble.book.core.di

import android.content.Context
import com.recipepebble.book.data.local.RecipePreferences
import com.recipepebble.book.data.local.SettingsPreferences
import com.recipepebble.book.data.repository.RecipeRepositoryImpl
import com.recipepebble.book.data.repository.SettingsRepositoryImpl
import com.recipepebble.book.domain.repository.RecipeRepository
import com.recipepebble.book.domain.repository.SettingsRepository
import com.recipepebble.book.domain.usecase.DeleteRecipeUseCase
import com.recipepebble.book.domain.usecase.GetRecipesUseCase
import com.recipepebble.book.domain.usecase.SaveRecipeUseCase
import com.recipepebble.book.domain.usecase.ToggleFavouriteUseCase
import com.recipepebble.book.domain.usecase.ToggleIngredientUseCase

object ServiceLocator {

    private var recipeRepository: RecipeRepository? = null
    private var settingsRepository: SettingsRepository? = null
    private var factoryInstance: ViewModelFactory? = null

    fun init(context: Context) {
        if (recipeRepository == null) {
            recipeRepository = RecipeRepositoryImpl(RecipePreferences(context))
        }
        if (settingsRepository == null) {
            settingsRepository = SettingsRepositoryImpl(SettingsPreferences(context))
        }
        if (factoryInstance == null) {
            factoryInstance = ViewModelFactory(
                getRecipes = GetRecipesUseCase(requireRecipes()),
                saveRecipe = SaveRecipeUseCase(requireRecipes()),
                toggleFavourite = ToggleFavouriteUseCase(requireRecipes()),
                deleteRecipe = DeleteRecipeUseCase(requireRecipes()),
                toggleIngredient = ToggleIngredientUseCase(requireRecipes()),
                settings = requireSettings(),
                recipes = requireRecipes()
            )
        }
    }

    fun factory(context: Context): ViewModelFactory {
        init(context.applicationContext)
        return factoryInstance ?: throw IllegalStateException(MESSAGE)
    }

    fun settings(context: Context): SettingsRepository {
        init(context.applicationContext)
        return requireSettings()
    }

    fun recipes(context: Context): RecipeRepository {
        init(context.applicationContext)
        return requireRecipes()
    }

    private fun requireRecipes(): RecipeRepository =
        recipeRepository ?: throw IllegalStateException(MESSAGE)

    private fun requireSettings(): SettingsRepository =
        settingsRepository ?: throw IllegalStateException(MESSAGE)

    private const val MESSAGE = "ServiceLocator is not initialised"
}
