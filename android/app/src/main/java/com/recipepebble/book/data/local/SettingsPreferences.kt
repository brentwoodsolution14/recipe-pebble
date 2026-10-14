package com.recipepebble.book.data.local

import android.content.Context
import android.content.SharedPreferences
import com.recipepebble.book.domain.model.AppSettings

class SettingsPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun read(): AppSettings = AppSettings(
        keepAwake = prefs.getBoolean(KEY_AWAKE, false),
        checklistFirst = prefs.getBoolean(KEY_CHECKLIST, true),
        largerText = prefs.getBoolean(KEY_LARGER, false),
        defaultMinutes = prefs.getInt(KEY_MINUTES, DEFAULT_MINUTES),
        favouritesOnly = prefs.getBoolean(KEY_FAVOURITES_ONLY, false)
    )

    fun write(settings: AppSettings) {
        prefs.edit()
            .putBoolean(KEY_AWAKE, settings.keepAwake)
            .putBoolean(KEY_CHECKLIST, settings.checklistFirst)
            .putBoolean(KEY_LARGER, settings.largerText)
            .putInt(KEY_MINUTES, settings.defaultMinutes)
            .putBoolean(KEY_FAVOURITES_ONLY, settings.favouritesOnly)
            .apply()
    }

    companion object {
        private const val STORE = "recipe_pebble_settings"
        private const val KEY_AWAKE = "keep_awake"
        private const val KEY_CHECKLIST = "checklist_first"
        private const val KEY_LARGER = "larger_text"
        private const val KEY_MINUTES = "default_minutes"
        private const val KEY_FAVOURITES_ONLY = "favourites_only"
        private const val DEFAULT_MINUTES = 25
    }
}
