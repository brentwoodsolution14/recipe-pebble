package com.recipepebble.book.domain.repository

import com.recipepebble.book.domain.model.AppSettings

interface SettingsRepository {
    fun load(): AppSettings

    fun save(settings: AppSettings)
}
