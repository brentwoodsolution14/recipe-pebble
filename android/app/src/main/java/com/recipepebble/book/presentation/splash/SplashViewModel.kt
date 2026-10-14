package com.recipepebble.book.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipepebble.book.core.config.GameConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    data class SplashUiState(
        val elapsedMs: Long = 0L,
        val progressPercent: Int = 0,
        val handOffReady: Boolean = false
    )

    private val _state = MutableStateFlow(SplashUiState())
    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    private var timerJob: Job? = null

    init {
        beginCountdown()
    }

    private fun beginCountdown() {
        if (timerJob != null) return
        timerJob = viewModelScope.launch {
            var elapsed = 0L
            while (elapsed < GameConfig.LOADER_DURATION_MS) {
                delay(TICK_MS)
                elapsed += TICK_MS
                val capped = if (elapsed > GameConfig.LOADER_DURATION_MS) {
                    GameConfig.LOADER_DURATION_MS
                } else {
                    elapsed
                }
                _state.value = _state.value.copy(
                    elapsedMs = capped,
                    progressPercent = percentOf(capped)
                )
            }
            _state.value = _state.value.copy(
                elapsedMs = GameConfig.LOADER_DURATION_MS,
                progressPercent = FULL_PERCENT,
                handOffReady = true
            )
        }
    }

    fun consumeHandOff() {
        if (!_state.value.handOffReady) return
        _state.value = _state.value.copy(handOffReady = false)
    }

    private fun percentOf(elapsed: Long): Int {
        val ratio = elapsed.toFloat() / GameConfig.LOADER_DURATION_MS.toFloat()
        val percent = (ratio * FULL_PERCENT).toInt()
        return if (percent > FULL_PERCENT) FULL_PERCENT else percent
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        timerJob = null
    }

    companion object {
        private const val TICK_MS = 200L
        private const val FULL_PERCENT = 100
    }
}
