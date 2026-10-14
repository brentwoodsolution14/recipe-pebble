package com.recipepebble.book.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import com.recipepebble.book.R
import com.recipepebble.book.core.config.GameConfig
import com.recipepebble.book.core.di.ServiceLocator
import com.recipepebble.book.core.util.TimeFormatter
import com.recipepebble.book.databinding.DialogSettingsBinding
import com.recipepebble.book.domain.model.AppSettings

class SettingsDialog : DialogFragment() {

    private var _binding: DialogSettingsBinding? = null
    private val binding get() = _binding

    private var current: AppSettings = AppSettings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_RecipePebble_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSettingsBinding.inflate(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        current = ServiceLocator.settings(requireContext()).load()
        bindValues()
        bindActions()
        listenForConfirm()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun bindValues() {
        val bound = binding ?: return
        bound.settingsKeepAwake.isChecked = current.keepAwake
        bound.settingsChecklistFirst.isChecked = current.checklistFirst
        bound.settingsLargerText.isChecked = current.largerText
        bound.settingsTimeValue.text = TimeFormatter.minutes(current.defaultMinutes)
    }

    private fun bindActions() {
        val bound = binding ?: return
        bound.settingsKeepAwake.setOnCheckedChangeListener { _, checked ->
            persist(current.copy(keepAwake = checked))
        }
        bound.settingsChecklistFirst.setOnCheckedChangeListener { _, checked ->
            persist(current.copy(checklistFirst = checked))
        }
        bound.settingsLargerText.setOnCheckedChangeListener { _, checked ->
            persist(current.copy(largerText = checked))
        }
        bound.settingsTimeMinus.setOnClickListener { shiftMinutes(-1) }
        bound.settingsTimePlus.setOnClickListener { shiftMinutes(1) }
        bound.settingsReset.setOnClickListener { askReset() }
        bound.settingsClose.setOnClickListener { dismissAllowingStateLoss() }
    }

    private fun listenForConfirm() {
        setFragmentResultListener(ConfirmResetDialog.REQUEST_RESET) { _, result ->
            if (result.getBoolean(ConfirmResetDialog.RESULT_CONFIRMED)) {
                ServiceLocator.recipes(requireContext()).resetToSample()
                dismissAllowingStateLoss()
            }
        }
    }

    private fun shiftMinutes(direction: Int) {
        val next = current.defaultMinutes + direction * GameConfig.MINUTE_STEP
        val clamped = when {
            next < GameConfig.MIN_MINUTES -> GameConfig.MIN_MINUTES
            next > GameConfig.MAX_MINUTES -> GameConfig.MAX_MINUTES
            else -> next
        }
        persist(current.copy(defaultMinutes = clamped))
        binding?.settingsTimeValue?.text = TimeFormatter.minutes(clamped)
    }

    private fun persist(settings: AppSettings) {
        current = settings
        ServiceLocator.settings(requireContext()).save(settings)
    }

    private fun askReset() {
        if (!isAdded) return
        ConfirmResetDialog.newInstance(true)
            .show(parentFragmentManager, ConfirmResetDialog.TAG)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "settings_dialog"
    }
}
