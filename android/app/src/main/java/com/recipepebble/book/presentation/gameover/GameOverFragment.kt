package com.recipepebble.book.presentation.gameover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.recipepebble.book.MainActivity
import com.recipepebble.book.R
import com.recipepebble.book.core.di.ServiceLocator
import com.recipepebble.book.core.util.SpriteCatalog
import com.recipepebble.book.core.util.TimeFormatter
import com.recipepebble.book.databinding.FragmentGameoverBinding
import com.recipepebble.book.databinding.ItemIngredientEditBinding
import com.recipepebble.book.databinding.ItemStepReadBinding
import com.recipepebble.book.domain.model.Recipe
import com.recipepebble.book.presentation.dialog.ConfirmResetDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GameOverFragment : Fragment() {

    private var _binding: FragmentGameoverBinding? = null
    private val binding get() = _binding

    private val viewModel: GameOverViewModel by viewModels {
        ServiceLocator.factory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameoverBinding.inflate(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeaders()
        setUpActions()
        listenForConfirm()
        viewModel.load(
            arguments?.getString(ARG_ID).orEmpty(),
            arguments?.getBoolean(ARG_SAVED) == true
        )
        observeState()
    }

    private fun setUpHeaders() {
        val bound = binding ?: return
        bound.detailIngredientsHeader.bind(
            getString(R.string.detail_section_ingredients),
            getString(R.string.detail_section_ingredients_caption)
        )
        bound.detailStepsHeader.bind(
            getString(R.string.detail_section_method),
            getString(R.string.detail_section_method_caption)
        )
    }

    private fun setUpActions() {
        val bound = binding ?: return
        bound.detailAgain.setOnClickListener { viewModel.onCookAgain() }
        bound.detailMenu.setOnClickListener { backToMenu() }
        bound.detailBackIcon.setOnClickListener { goBack() }
        bound.detailFavourite.setOnClickListener {
            viewModel.onFavourite()
            popHeart()
        }
        bound.detailDelete.setOnClickListener { askDelete() }
    }

    private fun listenForConfirm() {
        setFragmentResultListener(ConfirmResetDialog.REQUEST_DELETE) { _, result ->
            if (result.getBoolean(ConfirmResetDialog.RESULT_CONFIRMED)) {
                viewModel.onDelete()
            }
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

    private fun render(state: GameOverUiState) {
        if (!isAdded) return
        val bound = binding ?: return
        if (state.deleted) {
            backToMenu()
            return
        }
        val recipe = state.recipe ?: return
        bound.detailTitle.text = recipe.name
        bound.detailMeta.text = TimeFormatter.detailMeta(
            recipe.minutes,
            recipe.ingredients.size,
            recipe.steps.size
        )
        bound.detailThumb.setImageResource(SpriteCatalog.drawableFor(recipe.spriteKey))
        bound.detailRibbon.visibility = if (state.savedNow) View.VISIBLE else View.GONE
        renderTags(recipe)
        renderStats(state)
        renderFavourite(recipe)
        renderIngredients(recipe)
        renderSteps(recipe)
    }

    private fun renderTags(recipe: Recipe) {
        val bound = binding ?: return
        val chips = listOf(bound.detailTagOne, bound.detailTagTwo, bound.detailTagThree)
        chips.forEachIndexed { index, chip ->
            val label = recipe.tags.getOrNull(index)
            if (label == null) {
                chip.visibility = View.GONE
            } else {
                chip.visibility = View.VISIBLE
                chip.bind(label, false)
            }
        }
        bound.detailTagRow.visibility = if (recipe.tags.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun renderStats(state: GameOverUiState) {
        val bound = binding ?: return
        bound.detailStatMinutes.setStat(
            getString(R.string.detail_stat_minutes),
            state.minutes.toString(),
            R.color.terracotta
        )
        bound.detailStatItems.setStat(
            getString(R.string.detail_stat_items),
            state.ingredientCount.toString(),
            R.color.teal
        )
        bound.detailStatSteps.setStat(
            getString(R.string.detail_stat_steps),
            state.stepCount.toString(),
            R.color.honey_deep
        )
        bound.detailStatMinutes.setVisibleIfPositive(state.minutes)
        bound.detailStatItems.setVisibleIfPositive(state.ingredientCount)
        bound.detailStatSteps.setVisibleIfPositive(state.stepCount)
    }

    private fun renderFavourite(recipe: Recipe) {
        val bound = binding ?: return
        bound.detailFavourite.setText(
            if (recipe.favourite) R.string.detail_favourite_on
            else R.string.detail_favourite_add
        )
        bound.detailFavourite.setIconResource(
            if (recipe.favourite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
        )
        val tint = ContextCompat.getColorStateList(
            requireContext(),
            if (recipe.favourite) R.color.teal else R.color.ink_muted
        )
        bound.detailFavourite.iconTint = tint
        bound.detailFavourite.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (recipe.favourite) R.color.teal else R.color.ink_muted
            )
        )
        ViewCompat.setStateDescription(
            bound.detailFavourite,
            getString(
                if (recipe.favourite) R.string.state_favourite_on
                else R.string.state_favourite_off
            )
        )
    }

    private fun renderIngredients(recipe: Recipe) {
        val bound = binding ?: return
        bound.detailIngredientsHost.removeAllViews()
        recipe.ingredients.forEachIndexed { index, item ->
            val row = ItemIngredientEditBinding.inflate(
                layoutInflater,
                bound.detailIngredientsHost,
                false
            )
            row.ingredientRow.bind(item.name, item.amount, item.checked, false)
            row.ingredientRow.onToggle { viewModel.onIngredientToggled(index) }
            bound.detailIngredientsHost.addView(row.root)
        }
    }

    private fun renderSteps(recipe: Recipe) {
        val bound = binding ?: return
        bound.detailStepsHost.removeAllViews()
        recipe.steps.forEachIndexed { index, step ->
            val row = ItemStepReadBinding.inflate(
                layoutInflater,
                bound.detailStepsHost,
                false
            )
            row.stepReadNumber.text = (index + 1).toString()
            row.stepReadText.text = step.text
            bound.detailStepsHost.addView(row.root)
        }
    }

    private fun popHeart() {
        val bound = binding ?: return
        bound.detailFavourite.animate()
            .scaleX(HEART_POP)
            .scaleY(HEART_POP)
            .setDuration(HEART_MS)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                try {
                    if (!isAdded) return@withEndAction
                    val target = binding ?: return@withEndAction
                    target.detailFavourite.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(HEART_MS)
                        .start()
                } catch (e: Exception) {
                }
            }
            .start()
    }

    private fun askDelete() {
        if (!isAdded) return
        ConfirmResetDialog.newInstance(false)
            .show(parentFragmentManager, ConfirmResetDialog.TAG)
    }

    private fun goBack() {
        if (!isAdded) return
        val host = activity as? MainActivity ?: return
        if (host.isFinishing || host.isDestroyed) return
        host.navigator.back()
    }

    private fun backToMenu() {
        if (!isAdded) return
        val host = activity as? MainActivity ?: return
        if (host.isFinishing || host.isDestroyed) return
        if (!host.navigator.backToMenu()) return
        viewModel.consumeDeleted()
    }

    override fun onDestroyView() {
        _binding?.detailFavourite?.animate()?.cancel()
        _binding?.detailIngredientsHost?.removeAllViews()
        _binding?.detailStepsHost?.removeAllViews()
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_ID = "recipe_id"
        private const val ARG_SAVED = "saved_now"
        private const val HEART_POP = 1.25f
        private const val HEART_MS = 240L

        fun newInstance(recipeId: String, savedNow: Boolean): GameOverFragment {
            val fragment = GameOverFragment()
            val args = Bundle()
            args.putString(ARG_ID, recipeId)
            args.putBoolean(ARG_SAVED, savedNow)
            fragment.arguments = args
            return fragment
        }
    }
}
