package com.recipepebble.book.core.util

object TimeFormatter {

    fun minutes(value: Int): String = value.toString() + " min"

    fun library(recipes: Int, favourites: Int): String =
        recipes.toString() + " recipes · " + favourites.toString() + " favourites"

    fun cardMeta(minutes: Int, ingredients: Int): String =
        minutes.toString() + " min · " + ingredients.toString() + " ingredients"

    fun detailMeta(minutes: Int, ingredients: Int, steps: Int): String =
        minutes.toString() + " MIN · " + ingredients.toString() + " INGREDIENTS · " +
            steps.toString() + " STEPS"

    fun composerSummary(ingredients: Int, steps: Int, minutes: Int): String =
        ingredients.toString() + " INGREDIENTS · " + steps.toString() + " STEPS · " +
            minutes.toString() + " MIN"
}
