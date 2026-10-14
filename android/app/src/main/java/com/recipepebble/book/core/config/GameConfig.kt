package com.recipepebble.book.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L

    const val SAVE_DELAY_MS = 450L
    const val LIBRARY_WARMUP_MS = 450L

    const val MIN_MINUTES = 5
    const val MAX_MINUTES = 180
    const val MINUTE_STEP = 5
    const val DEFAULT_MINUTES = 25

    const val MAX_NAME_LENGTH = 48
    const val MAX_INGREDIENT_LENGTH = 32
    const val MAX_STEP_LENGTH = 180
    const val MAX_INGREDIENTS = 24
    const val MAX_STEPS = 16
    const val MAX_TAGS_PER_RECIPE = 3

    const val STAGE_TITLE = 0
    const val STAGE_INGREDIENTS = 1
    const val STAGE_METHOD = 2
    const val STAGE_COUNT = 3
}
