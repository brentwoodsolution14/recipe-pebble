package com.recipepebble.book.core.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.recipepebble.book.R
import com.recipepebble.book.presentation.game.GameFragment
import com.recipepebble.book.presentation.gameover.GameOverFragment
import com.recipepebble.book.presentation.menu.MenuFragment
import com.recipepebble.book.presentation.splash.SplashFragment

class Navigator(
    private val manager: FragmentManager,
    private val containerId: Int
) {

    fun showSplash(): Boolean {
        if (manager.isStateSaved) return false
        manager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(containerId, SplashFragment(), TAG_SPLASH)
            .commit()
        return true
    }

    fun showMenu(): Boolean {
        if (manager.isStateSaved) return false
        manager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(containerId, MenuFragment(), TAG_MENU)
            .commit()
        return true
    }

    fun showGame(): Boolean {
        if (manager.isStateSaved) return false
        manager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(containerId, GameFragment(), TAG_GAME)
            .addToBackStack(TAG_GAME)
            .commit()
        return true
    }

    fun showDetail(recipeId: String, savedNow: Boolean): Boolean {
        if (manager.isStateSaved) return false
        manager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_up,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out_down
            )
            .replace(containerId, GameOverFragment.newInstance(recipeId, savedNow), TAG_DETAIL)
            .addToBackStack(TAG_DETAIL)
            .commit()
        return true
    }

    fun backToMenu(): Boolean {
        if (manager.isStateSaved) return false
        while (manager.backStackEntryCount > 0) {
            manager.popBackStackImmediate()
        }
        if (manager.findFragmentByTag(TAG_MENU) == null) {
            return showMenu()
        }
        return true
    }

    fun back(): Boolean {
        if (manager.isStateSaved) return false
        if (manager.backStackEntryCount == 0) return false
        manager.popBackStack()
        return true
    }

    fun current(): Fragment? = manager.findFragmentById(containerId)

    companion object {
        const val TAG_SPLASH = "splash"
        const val TAG_MENU = "menu"
        const val TAG_GAME = "game"
        const val TAG_DETAIL = "detail"
    }
}
