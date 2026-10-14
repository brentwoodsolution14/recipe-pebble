package com.recipepebble.book.data.repository

import com.recipepebble.book.data.local.SettingsPreferences
import com.recipepebble.book.domain.model.AppSettings
import com.recipepebble.book.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val storage: SettingsPreferences) : SettingsRepository {

    private var cache: AppSettings? = null

    override fun load(): AppSettings {
        cache?.let { return it }
        val loaded = storage.read()
        cache = loaded
        return loaded
    }

    override fun save(settings: AppSettings) {
        cache = settings
        storage.write(settings)
    }
}
