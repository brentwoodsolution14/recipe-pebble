package com.recipepebble.book.presentation.game

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.recipepebble.book.MainActivity
import com.recipepebble.book.R
import com.recipepebble.book.core.config.GameConfig
import com.recipepebble.book.core.di.ServiceLocator
import com.recipepebble.book.core.ui.TagChipView
import com.recipepebble.book.core.util.TimeFormatter
import com.recipepebble.book.databinding.FragmentGameBinding
import com.recipepebble.book.domain.model.RecipeTag
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding

    private val viewModel: GameViewModel by viewModels {
        ServiceLocator.factory(requireContext())
    }

    private var ingredientAdapter: IngredientAdapter? = null
    private var stepAdapter: StepAdapter? = null
    private var shownStage = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpHeaders()
        setUpLists()
        setUpInputs()
        setUpActions()
        observeState()
    }

    private fun setUpHeaders() {
        val bound = binding ?: return
        bound.stageOneHeader.bind(
            getString(R.string.game_section_title),
            getString(R.string.game_section_title_caption)
        )
        bound.stageTwoHeader.bind(
            getString(R.string.game_section_ingredients),
            getString(R.string.game_section_ingredients_caption)
        )
        bound.stageThreeHeader.bind(
            getString(R.string.game_section_method),
            getString(R.string.game_section_method_caption)
        )
    }

    private fun setUpLists() {
        val bound = binding ?: return
        val ingredients = IngredientAdapter(
            onToggle = { index -> viewModel.onToggleIngredient(index) },
            onRemove = { index -> viewModel.onRemoveIngredient(index) }
        )
        val steps = StepAdapter { index, delta -> viewModel.onMoveStep(index, delta) }
        ingredientAdapter = ingredients
        stepAdapter = steps
        bound.gameIngredientList.layoutManager = LinearLayoutManager(requireContext())
        bound.gameIngredientList.adapter = ingredients
        bound.gameStepList.layoutManager = LinearLayoutManager(requireContext())
        bound.gameStepList.adapter = steps
    }

    private fun setUpInputs() {
        val bound = binding ?: return
        bound.gameNameInput.setText(viewModel.state.value.name)
        bound.gameNameInput.doAfterTextChanged { editable ->
            viewModel.onNameChanged(editable?.toString().orEmpty())
        }
        bound.gameMinutesMinus.setOnClickListener { viewModel.onMinutesDelta(-1) }
        bound.gameMinutesPlus.setOnClickListener { viewModel.onMinutesDelta(1) }
        tagChips().forEachIndexed { index, chip ->
            val label = RecipeTag.labels().getOrNull(index).orEmpty()
            chip.bind(label, false)
            chip.makeTappable { tapped -> viewModel.onTagToggled(tapped) }
        }
    }

    private fun setUpActions() {
        val bound = binding ?: return
        bound.gamePrimary.setOnClickListener { viewModel.onPrimary() }
        bound.gameBack.setOnClickListener { viewModel.onSecondary() }
        bound.gameBackIcon.setOnClickListener { leaveComposer() }
        bound.gameDraft.setOnClickListener { viewModel.onSaveDraft() }
        bound.gameAddIngredient.setOnClickListener {
            val name = bound.gameIngredientInput.text?.toString().orEmpty()
            val amount = bound.gameAmountInput.text?.toString().orEmpty()
            viewModel.onAddIngredient(name, amount)
            bound.gameIngredientInput.setText("")
            bound.gameAmountInput.setText("")
        }
        bound.gameAddStep.setOnClickListener {
            val text = bound.gameStepInput.text?.toString().orEmpty()
            viewModel.onAddStep(text)
            bound.gameStepInput.setText("")
        }
    }

    private fun tagChips(): List<TagChipView> {
        val bound = binding ?: return emptyList()
        return listOf(
            bound.tagOne,
            bound.tagTwo,
            bound.tagThree,
            bound.tagFour,
            bound.tagFive,
            bound.tagSix,
            bound.tagSeven,
            bound.tagEight
        )
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

    private fun render(state: GameUiState) {
        if (!isAdded) return
        val bound = binding ?: return
        renderStage(state)
        renderSegments(state.stage)
        bound.gameMinutesValue.text = TimeFormatter.minutes(state.minutes)
        bound.gameSummary.text = TimeFormatter.composerSummary(
            state.ingredients.size,
            state.steps.size,
            state.minutes
        )
        ingredientAdapter?.submit(state.ingredients)
        stepAdapter?.submit(state.steps)
        renderTags(state)
        renderPrimary(state)
        renderWarning(state)
        renderNameError(state)
        if (state.savedId.isNotEmpty()) openDetail(state.savedId)
        if (state.leaving) leaveComposer()
    }

    private fun renderStage(state: GameUiState) {
        val bound = binding ?: return
        bound.stageOne.visibility =
            if (state.stage == GameConfig.STAGE_TITLE) View.VISIBLE else View.GONE
        bound.stageTwo.visibility =
            if (state.stage == GameConfig.STAGE_INGREDIENTS) View.VISIBLE else View.GONE
        bound.stageThree.visibility =
            if (state.stage == GameConfig.STAGE_METHOD) View.VISIBLE else View.GONE
        bound.gameStageCaption.setText(
            when (state.stage) {
                GameConfig.STAGE_TITLE -> R.string.game_stage_one
                GameConfig.STAGE_INGREDIENTS -> R.string.game_stage_two
                else -> R.string.game_stage_three
            }
        )
        if (shownStage != state.stage) {
            shownStage = state.stage
            val target = when (state.stage) {
                GameConfig.STAGE_TITLE -> bound.stageOne
                GameConfig.STAGE_INGREDIENTS -> bound.stageTwo
                else -> bound.stageThree
            }
            target.startAnimation(
                AnimationUtils.loadAnimation(requireContext(), R.anim.stage_fade_in)
            )
            bound.gameScroll.scrollTo(0, 0)
        }
    }

    private fun renderSegments(stage: Int) {
        val bound = binding ?: return
        val segments = listOf(bound.gameSegOne, bound.gameSegTwo, bound.gameSegThree)
        segments.forEachIndexed { index, segment ->
            segment.setBackgroundResource(
                if (index <= stage) R.drawable.bg_seg_on else R.drawable.bg_seg_off
            )
        }
    }

    private fun renderTags(state: GameUiState) {
        tagChips().forEachIndexed { index, chip ->
            val label = RecipeTag.labels().getOrNull(index).orEmpty()
            chip.bind(label, state.tags.contains(label))
        }
    }

    private fun renderPrimary(state: GameUiState) {
        val bound = binding ?: return
        bound.gamePrimary.setText(
            if (state.isLastStage) R.string.game_save else R.string.game_next
        )
        val enabled = state.primaryEnabled && !state.saving
        bound.gamePrimary.isEnabled = enabled
        bound.gamePrimary.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (enabled) R.color.terracotta else R.color.disabled_fill
            )
        )
        bound.gamePrimary.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (enabled) R.color.page_cream else R.color.disabled_text
            )
        )
        bound.gameSaving.visibility = if (state.saving) View.VISIBLE else View.GONE
        bound.gamePrimary.alpha = if (state.saving) SAVING_ALPHA else 1f
        bound.gameBack.isEnabled = !state.saving
        bound.gameDraft.isEnabled = !state.saving && state.name.isNotBlank()
    }

    private fun renderWarning(state: GameUiState) {
        val bound = binding ?: return
        when (state.warning) {
            GameUiState.Warning.NONE -> bound.gameWarning.visibility = View.GONE
            GameUiState.Warning.INGREDIENTS -> {
                bound.gameWarning.setText(R.string.game_warning_ingredients)
                bound.gameWarning.visibility = View.VISIBLE
            }

            GameUiState.Warning.STEPS -> {
                bound.gameWarning.setText(R.string.game_warning_steps)
                bound.gameWarning.visibility = View.VISIBLE
            }
        }
    }

    private fun renderNameError(state: GameUiState) {
        val bound = binding ?: return
        if (state.nameError) {
            bound.gameNameField.error = getString(R.string.game_name_error)
            bound.gameNameField.startAnimation(
                AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
            )
        } else {
            bound.gameNameField.error = null
        }
    }

    private fun openDetail(recipeId: String) {
        if (!isAdded) return
        val host = activity as? MainActivity ?: return
        if (host.isFinishing || host.isDestroyed) return
        if (!host.navigator.showDetail(recipeId, true)) return
        viewModel.consumeSaved()
    }

    private fun leaveComposer() {
        if (!isAdded) return
        val host = activity as? MainActivity ?: return
        if (host.isFinishing || host.isDestroyed) return
        if (!host.navigator.back()) return
        viewModel.consumeLeaving()
    }

    override fun onDestroyView() {
        _binding?.gameIngredientList?.adapter = null
        _binding?.gameStepList?.adapter = null
        _binding?.gameNameField?.clearAnimation()
        _binding?.stageOne?.clearAnimation()
        _binding?.stageTwo?.clearAnimation()
        _binding?.stageThree?.clearAnimation()
        ingredientAdapter = null
        stepAdapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val SAVING_ALPHA = 0.7f
    }
}
