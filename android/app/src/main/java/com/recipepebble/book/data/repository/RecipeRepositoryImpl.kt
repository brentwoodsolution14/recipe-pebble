package com.recipepebble.book.data.repository

import com.recipepebble.book.data.local.RecipePreferences
import com.recipepebble.book.data.sample.SampleData
import com.recipepebble.book.domain.model.Recipe
import com.recipepebble.book.domain.repository.RecipeRepository

class RecipeRepositoryImpl(private val storage: RecipePreferences) : RecipeRepository {

    private var cache: MutableList<Recipe>? = null

    override fun load(): List<Recipe> {
        cache?.let { return it.toList() }
        val stored = if (storage.hasStoredBook()) storage.readRecipes() else emptyList()
        val initial = if (stored.isEmpty() && !storage.hasStoredBook()) {
            storage.writeRecipes(SampleData.recipes)
            SampleData.recipes
        } else {
            stored
        }
        val list = initial.toMutableList()
        cache = list
        return list.toList()
    }

    override fun save(recipes: List<Recipe>) {
        val list = recipes.toMutableList()
        cache = list
        storage.writeRecipes(list)
    }

    override fun add(recipe: Recipe): List<Recipe> {
        val list = load().toMutableList()
        list.add(0, recipe)
        save(list)
        return list.toList()
    }

    override fun replace(recipe: Recipe): List<Recipe> {
        val list = load().toMutableList()
        val index = list.indexOfFirst { it.id == recipe.id }
        if (index >= 0) list[index] = recipe else list.add(0, recipe)
        save(list)
        return list.toList()
    }

    override fun remove(id: String): List<Recipe> {
        val list = load().toMutableList()
        list.removeAll { it.id == id }
        save(list)
        return list.toList()
    }

    override fun find(id: String): Recipe? = load().firstOrNull { it.id == id }

    override fun resetToSample(): List<Recipe> {
        save(SampleData.recipes)
        return SampleData.recipes
    }
}
