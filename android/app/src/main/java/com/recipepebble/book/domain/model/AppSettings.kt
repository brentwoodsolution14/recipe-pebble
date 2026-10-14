package com.recipepebble.book.domain.model

data class AppSettings(
    val keepAwake: Boolean = false,
    val checklistFirst: Boolean = true,
    val largerText: Boolean = false,
    val defaultMinutes: Int = 25,
    val favouritesOnly: Boolean = false
)
