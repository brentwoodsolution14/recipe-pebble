package com.recipepebble.book.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.recipepebble.book.R
import com.recipepebble.book.databinding.DialogConfirmResetBinding

class ConfirmResetDialog : DialogFragment() {

    private var _binding: DialogConfirmResetBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_RecipePebble_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogConfirmResetBinding.inflate(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bound = binding ?: return
        val resetMode = arguments?.getBoolean(ARG_RESET) == true
        bound.confirmBody.setText(
            if (resetMode) R.string.confirm_reset_body else R.string.confirm_delete_body
        )
        bound.confirmYes.setOnClickListener { finish(resetMode, true) }
        bound.confirmNo.setOnClickListener { finish(resetMode, false) }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun finish(resetMode: Boolean, confirmed: Boolean) {
        if (!isAdded) return
        val result = Bundle()
        result.putBoolean(RESULT_CONFIRMED, confirmed)
        setFragmentResult(if (resetMode) REQUEST_RESET else REQUEST_DELETE, result)
        dismissAllowingStateLoss()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "confirm_reset_dialog"
        const val REQUEST_RESET = "confirm_reset_request"
        const val REQUEST_DELETE = "confirm_delete_request"
        const val RESULT_CONFIRMED = "confirmed"
        private const val ARG_RESET = "reset_mode"

        fun newInstance(resetMode: Boolean): ConfirmResetDialog {
            val fragment = ConfirmResetDialog()
            val args = Bundle()
            args.putBoolean(ARG_RESET, resetMode)
            fragment.arguments = args
            return fragment
        }
    }
}
