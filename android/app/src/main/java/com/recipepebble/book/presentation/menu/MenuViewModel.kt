package com.recipepebble.book.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipepebble.book.core.config.GameConfig
import com.recipepebble.book.domain.repository.SettingsRepository
import com.recipepebble.book.domain.usecase.GetRecipesUseCase
import com.recipepebble.book.domain.usecase.ToggleFavouriteUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuViewModel(
    private val getRecipes: GetRecipesUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    private var warmedUp = false

    fun refresh() {
        val favouritesOnly = settings.load().favouritesOnly
        if (!warmedUp) {
            warmedUp = true
            _state.value = _state.value.copy(loading = true, favouritesOnly = favouritesOnly)
            viewModelScope.launch {
                delay(GameConfig.LIBRARY_WARMUP_MS)
                publish(favouritesOnly)
            }
            return
        }
        publish(favouritesOnly)
    }

    fun setFavouritesOnly(value: Boolean) {
        val current = settings.load()
        settings.save(current.copy(favouritesOnly = value))
        publish(value)
    }

    fun onFavouriteTapped(id: String) {
        toggleFavourite(id)
        publish(_state.value.favouritesOnly)
    }

    private fun publish(favouritesOnly: Boolean) {
        val all = getRecipes.all()
        val visible = if (favouritesOnly) all.filter { it.favourite } else all
        _state.value = MenuUiState(
            loading = false,
            favouritesOnly = favouritesOnly,
            visible = visible,
            totalRecipes = all.size,
            favouriteCount = all.count { it.favourite },
            tagCount = all.flatMap { it.tags }.distinct().size
        )
    }
}
