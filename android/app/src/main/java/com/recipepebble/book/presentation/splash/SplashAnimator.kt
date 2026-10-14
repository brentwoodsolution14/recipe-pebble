package com.recipepebble.book.presentation.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator

class SplashAnimator {

    private val running = mutableListOf<Animator>()

    fun playPlateEntrance(plate: View, sprite: View) {
        plate.alpha = 0f
        plate.scaleX = PLATE_FROM_SCALE
        plate.scaleY = PLATE_FROM_SCALE
        sprite.alpha = 0f
        sprite.scaleX = PLATE_FROM_SCALE
        sprite.scaleY = PLATE_FROM_SCALE

        val plateSet = AnimatorSet()
        plateSet.playTogether(
            ObjectAnimator.ofFloat(plate, View.ALPHA, 0f, 1f),
            ObjectAnimator.ofFloat(plate, View.SCALE_X, PLATE_FROM_SCALE, 1f),
            ObjectAnimator.ofFloat(plate, View.SCALE_Y, PLATE_FROM_SCALE, 1f),
            ObjectAnimator.ofFloat(sprite, View.ALPHA, 0f, 1f),
            ObjectAnimator.ofFloat(sprite, View.SCALE_X, PLATE_FROM_SCALE, 1f),
            ObjectAnimator.ofFloat(sprite, View.SCALE_Y, PLATE_FROM_SCALE, 1f)
        )
        plateSet.duration = PLATE_DURATION
        plateSet.interpolator = OvershootInterpolator(PLATE_TENSION)
        track(plateSet)
        plateSet.start()
    }

    fun playTitleEntrance(title: View, riseFrom: Float) {
        title.alpha = 0f
        title.translationY = riseFrom
        val set = AnimatorSet()
        set.playTogether(
            ObjectAnimator.ofFloat(title, View.ALPHA, 0f, 1f),
            ObjectAnimator.ofFloat(title, View.TRANSLATION_Y, riseFrom, 0f)
        )
        set.duration = TITLE_DURATION
        set.startDelay = TITLE_DELAY
        set.interpolator = DecelerateInterpolator()
        track(set)
        set.start()
    }

    fun playFadeIn(view: View, duration: Long, startDelay: Long) {
        view.alpha = 0f
        val fade = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
        fade.duration = duration
        fade.startDelay = startDelay
        fade.interpolator = DecelerateInterpolator()
        track(fade)
        fade.start()
    }

    fun startSteamLoop(dots: List<View>, riseBy: Float) {
        dots.forEachIndexed { index, dot ->
            dot.alpha = 0f
            val rise = ObjectAnimator.ofFloat(dot, View.TRANSLATION_Y, 0f, riseBy)
            rise.duration = STEAM_DURATION
            rise.startDelay = STEAM_STAGGER * index
            rise.repeatCount = ValueAnimator.INFINITE
            rise.repeatMode = ValueAnimator.RESTART
            rise.interpolator = LinearInterpolator()

            val fade = ObjectAnimator.ofFloat(dot, View.ALPHA, STEAM_ALPHA, 0f)
            fade.duration = STEAM_DURATION
            fade.startDelay = STEAM_STAGGER * index
            fade.repeatCount = ValueAnimator.INFINITE
            fade.repeatMode = ValueAnimator.RESTART
            fade.interpolator = AccelerateDecelerateInterpolator()

            track(rise)
            track(fade)
            rise.start()
            fade.start()
        }
    }

    fun startPlateBreathing(plate: View) {
        val breathe = ObjectAnimator.ofFloat(plate, View.ROTATION, -BREATHE_ANGLE, BREATHE_ANGLE)
        breathe.duration = BREATHE_DURATION
        breathe.repeatCount = ValueAnimator.INFINITE
        breathe.repeatMode = ValueAnimator.REVERSE
        breathe.interpolator = AccelerateDecelerateInterpolator()
        track(breathe)
        breathe.start()
    }

    private fun track(animator: Animator) {
        running.add(animator)
    }

    fun cancelAll() {
        running.forEach { animator ->
            animator.removeAllListeners()
            animator.cancel()
        }
        running.clear()
    }

    companion object {
        private const val PLATE_FROM_SCALE = 0.82f
        private const val PLATE_DURATION = 620L
        private const val PLATE_TENSION = 1.1f
        private const val TITLE_DURATION = 520L
        private const val TITLE_DELAY = 180L
        private const val STEAM_DURATION = 1400L
        private const val STEAM_STAGGER = 220L
        private const val STEAM_ALPHA = 0.9f
        private const val BREATHE_ANGLE = 1.6f
        private const val BREATHE_DURATION = 2600L

        const val SUBTITLE_DURATION = 420L
        const val SUBTITLE_DELAY = 380L
        const val PROGRESS_DURATION = 360L
        const val PROGRESS_DELAY = 560L
        const val TITLE_RISE_DP = 28f
    }
}
