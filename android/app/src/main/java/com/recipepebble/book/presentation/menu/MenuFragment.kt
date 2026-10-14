package com.recipepebble.book.presentation.menu

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.recipepebble.book.MainActivity
import com.recipepebble.book.R
import com.recipepebble.book.core.di.ServiceLocator
import com.recipepebble.book.core.util.TimeFormatter
import com.recipepebble.book.databinding.FragmentMenuBinding
import com.recipepebble.book.presentation.dialog.SettingsDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding

    private val viewModel: MenuViewModel by viewModels {
        ServiceLocator.factory(requireContext())
    }

    private var adapter: RecipeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpList()
        setUpActions()
        playEntrance()
        observeState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun setUpList() {
        val bound = binding ?: return
        val recipeAdapter = RecipeAdapter(
            onOpen = { recipe -> openDetail(recipe.id) },
            onFavourite = { recipe -> viewModel.onFavouriteTapped(recipe.id) }
        )
        adapter = recipeAdapter
        bound.menuList.layoutManager = LinearLayoutManager(requireContext())
        bound.menuList.adapter = recipeAdapter
        bound.menuList.layoutAnimation =
            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_fall_down)
    }

    private fun setUpActions() {
        val bound = binding ?: return
        bound.menuCta.setOnClickListener { openComposer() }
        bound.menuSettings.setOnClickListener { openSettings() }
        bound.menuFilterAll.setOnClickListener { viewModel.setFavouritesOnly(false) }
        bound.menuFilterFavourites.setOnClickListener { viewModel.setFavouritesOnly(true) }
    }

    private fun playEntrance() {
        val bound = binding ?: return
        val rise = resources.getDimensionPixelSize(R.dimen.gap_l).toFloat()
        listOf(bound.menuHeroFirst, bound.menuHeroSecond, bound.menuCounts).forEach { view ->
            view.alpha = 0f
            view.translationY = rise
            view.animate().alpha(1f).translationY(0f).setDuration(ENTRANCE_MS).start()
        }
        val cards = listOf(bound.statRecipes, bound.statFavourites, bound.statTags)
        cards.forEachIndexed { index, card ->
            card.alpha = 0f
            card.translationY = rise
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(STAT_STAGGER_MS * index)
                .setDuration(STAT_MS)
                .start()
        }
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

    private fun render(state: MenuUiState) {
        if (!isAdded) return
        val bound = binding ?: return
        bound.menuCounts.text = TimeFormatter.library(state.totalRecipes, state.favouriteCount)
        renderStats(state)
        renderFilter(state.favouritesOnly)
        adapter?.submit(state.visible)
        bound.menuList.scheduleLayoutAnimation()
        renderEmpty(state)
    }

    private fun renderStats(state: MenuUiState) {
        val bound = binding ?: return
        bound.statRecipes.setStat(
            getString(R.string.menu_stat_recipes),
            state.totalRecipes.toString(),
            R.color.terracotta
        )
        bound.statFavourites.setStat(
            getString(R.string.menu_stat_favourites),
            state.favouriteCount.toString(),
            R.color.teal
        )
        bound.statTags.setStat(
            getString(R.string.menu_stat_tags),
            state.tagCount.toString(),
            R.color.honey_deep
        )
        bound.statRecipes.setVisibleIfPositive(state.totalRecipes)
        bound.statFavourites.setVisibleIfPositive(state.favouriteCount)
        bound.statTags.setVisibleIfPositive(state.tagCount)
        bound.menuStatsRow.visibility = if (state.statsVisible >= MIN_STATS) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun renderFilter(favouritesOnly: Boolean) {
        val bound = binding ?: return
        applyChipLook(bound.menuFilterAll, !favouritesOnly)
        applyChipLook(bound.menuFilterFavourites, favouritesOnly)
    }

    private fun applyChipLook(button: MaterialButton, selected: Boolean) {
        val context = button.context
        button.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (selected) R.color.terracotta else R.color.transparent
            )
        )
        button.setTextColor(
            ContextCompat.getColor(
                context,
                if (selected) R.color.page_cream else R.color.ink_muted
            )
        )
        button.strokeColor = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (selected) R.color.terracotta else R.color.outline_soft
            )
        )
        ViewCompat.setStateDescription(
            button,
            context.getString(
                if (selected) R.string.state_selected else R.string.state_not_selected
            )
        )
    }

    private fun renderEmpty(state: MenuUiState) {
        val bound = binding ?: return
        val show = state.isEmpty
        val visibility = if (show) View.VISIBLE else View.GONE
        bound.menuEmptyArt.visibility = visibility
        bound.menuEmptyTitle.visibility = visibility
        bound.menuEmptyBody.visibility = visibility
        if (show) {
            bound.menuEmptyTitle.setText(
                if (state.favouritesOnly) R.string.menu_empty_favourites_title
                else R.string.menu_empty_title
            )
            bound.menuEmptyBody.setText(
                if (state.favouritesOnly) R.string.menu_empty_favourites_body
                else R.string.menu_empty_body
            )
        }
    }

    private fun openComposer() {
        if (!isAdded) return
        val host = activity as? MainActivity ?: return
        host.navigator.showGame()
    }

    private fun openDetail(recipeId: String) {
        if (!isAdded) return
        val host = activity as? MainActivity ?: return
        host.navigator.showDetail(recipeId, false)
    }

    private fun openSettings() {
        if (!isAdded) return
        SettingsDialog().show(parentFragmentManager, SettingsDialog.TAG)
    }

    override fun onDestroyView() {
        _binding?.menuList?.adapter = null
        _binding?.menuHeroFirst?.animate()?.cancel()
        _binding?.menuHeroSecond?.animate()?.cancel()
        _binding?.menuCounts?.animate()?.cancel()
        _binding?.statRecipes?.animate()?.cancel()
        _binding?.statFavourites?.animate()?.cancel()
        _binding?.statTags?.animate()?.cancel()
        adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ENTRANCE_MS = 420L
        private const val STAT_MS = 320L
        private const val STAT_STAGGER_MS = 80L
        private const val MIN_STATS = 2
    }
}
