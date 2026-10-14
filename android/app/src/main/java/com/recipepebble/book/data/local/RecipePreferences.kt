package com.recipepebble.book.data.local

import android.content.Context
import android.content.SharedPreferences
import com.recipepebble.book.domain.model.Ingredient
import com.recipepebble.book.domain.model.Recipe
import com.recipepebble.book.domain.model.RecipeStep

class RecipePreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun hasStoredBook(): Boolean = prefs.contains(KEY_BOOK)

    fun readRecipes(): List<Recipe> {
        val raw = prefs.getString(KEY_BOOK, null) ?: return emptyList()
        if (raw.isEmpty()) return emptyList()
        return raw.split(RECORD)
            .filter { it.isNotEmpty() }
            .mapNotNull { decodeRecipe(it) }
    }

    fun writeRecipes(recipes: List<Recipe>) {
        val raw = recipes.joinToString(RECORD.toString()) { encodeRecipe(it) }
        prefs.edit().putString(KEY_BOOK, raw).apply()
    }

    private fun encodeRecipe(recipe: Recipe): String {
        val ingredients = recipe.ingredients.joinToString(GROUP.toString()) { item ->
            listOf(
                sanitize(item.name),
                sanitize(item.amount),
                if (item.checked) FLAG_ON else FLAG_OFF
            ).joinToString(PAIR.toString())
        }
        val steps = recipe.steps.joinToString(GROUP.toString()) { sanitize(it.text) }
        val tags = recipe.tags.joinToString(GROUP.toString()) { sanitize(it) }
        return listOf(
            sanitize(recipe.id),
            sanitize(recipe.name),
            recipe.minutes.toString(),
            if (recipe.favourite) FLAG_ON else FLAG_OFF,
            sanitize(recipe.spriteKey),
            tags,
            ingredients,
            steps
        ).joinToString(UNIT.toString())
    }

    private fun decodeRecipe(raw: String): Recipe? {
        val parts = raw.split(UNIT)
        if (parts.size < FIELD_COUNT) return null
        val minutes = parts[2].toIntOrNull() ?: return null
        val tags = parts[5].split(GROUP).filter { it.isNotEmpty() }
        val ingredients = parts[6].split(GROUP)
            .filter { it.isNotEmpty() }
            .mapNotNull { chunk ->
                val fields = chunk.split(PAIR)
                if (fields.size < 3) null
                else Ingredient(fields[0], fields[1], fields[2] == FLAG_ON)
            }
        val steps = parts[7].split(GROUP)
            .filter { it.isNotEmpty() }
            .map { RecipeStep(it) }
        return Recipe(
            id = parts[0],
            name = parts[1],
            minutes = minutes,
            favourite = parts[3] == FLAG_ON,
            spriteKey = parts[4],
            tags = tags,
            ingredients = ingredients,
            steps = steps
        )
    }

    private fun sanitize(value: String): String = value
        .replace(RECORD, ' ')
        .replace(UNIT, ' ')
        .replace(GROUP, ' ')
        .replace(PAIR, ' ')

    companion object {
        private const val STORE = "recipe_pebble_book"
        private const val KEY_BOOK = "book"
        private const val FLAG_ON = "1"
        private const val FLAG_OFF = "0"
        private const val FIELD_COUNT = 8
        private val RECORD = 30.toChar()
        private val UNIT = 31.toChar()
        private val GROUP = 29.toChar()
        private val PAIR = 28.toChar()
    }
}
