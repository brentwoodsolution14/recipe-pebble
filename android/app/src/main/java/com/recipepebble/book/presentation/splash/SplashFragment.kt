package com.recipepebble.book.presentation.splash

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.recipepebble.book.MainActivity
import com.recipepebble.book.core.di.ServiceLocator
import com.recipepebble.book.databinding.FragmentSplashBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding

    private val viewModel: SplashViewModel by viewModels {
        ServiceLocator.factory(requireContext())
    }

    private var animator: SplashAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startDecoration()
        observeState()
    }

    private fun startDecoration() {
        val bound = binding ?: return
        val helper = SplashAnimator()
        animator = helper
        helper.playPlateEntrance(bound.splashPlate, bound.splashSprite)
        helper.playTitleEntrance(bound.splashTitle, dp(SplashAnimator.TITLE_RISE_DP))
        helper.playFadeIn(
            bound.splashSubtitle,
            SplashAnimator.SUBTITLE_DURATION,
            SplashAnimator.SUBTITLE_DELAY
        )
        helper.playFadeIn(
            bound.splashRule,
            SplashAnimator.PROGRESS_DURATION,
            SplashAnimator.PROGRESS_DELAY
        )
        helper.playFadeIn(
            bound.splashProgress,
            SplashAnimator.PROGRESS_DURATION,
            SplashAnimator.PROGRESS_DELAY
        )
        helper.playFadeIn(
            bound.splashCaption,
            SplashAnimator.PROGRESS_DURATION,
            SplashAnimator.PROGRESS_DELAY
        )
        helper.startSteamLoop(
            listOf(bound.steamOne, bound.steamTwo, bound.steamThree),
            -dp(STEAM_RISE_DP)
        )
        helper.startPlateBreathing(bound.splashPlate)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: SplashViewModel.SplashUiState) {
        if (!isAdded) return
        binding ?: return
        if (state.handOffReady) {
            handOff()
        }
    }

    private fun handOff() {
        if (!isAdded) return
        val host = activity as? MainActivity ?: return
        if (host.isFinishing || host.isDestroyed) return
        if (!host.navigator.showMenu()) return
        viewModel.consumeHandOff()
    }

    private fun dp(value: Float): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value,
        resources.displayMetrics
    )

    override fun onDestroyView() {
        animator?.cancelAll()
        animator = null
        _binding?.splashPlate?.animate()?.cancel()
        _binding?.splashTitle?.animate()?.cancel()
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val STEAM_RISE_DP = 10f
    }
}
