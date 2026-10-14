package com.recipepebble.book.core.ui

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.recipepebble.book.R
import com.recipepebble.book.databinding.ViewIngredientRowBinding

class IngredientRowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewIngredientRowBinding.inflate(LayoutInflater.from(context), this)

    private var toggleListener: (() -> Unit)? = null
    private var removeListener: (() -> Unit)? = null

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        binding.rowCheck.setOnCheckedChangeListener { _, _ -> toggleListener?.invoke() }
        binding.rowRemove.setOnClickListener { removeListener?.invoke() }
    }

    fun bind(name: String, amount: String, checked: Boolean, editable: Boolean) {
        binding.rowName.text = name
        binding.rowAmount.text = amount
        binding.rowCheck.setOnCheckedChangeListener(null)
        binding.rowCheck.isChecked = checked
        binding.rowCheck.setOnCheckedChangeListener { _, _ -> toggleListener?.invoke() }
        binding.rowRemove.visibility = if (editable) View.VISIBLE else View.GONE
        applyCheckedLook(checked)
        contentDescription = name + " " + amount
        ViewCompat.setStateDescription(
            this,
            context.getString(if (checked) R.string.state_checked else R.string.state_unchecked)
        )
    }

    fun onToggle(listener: () -> Unit) {
        toggleListener = listener
    }

    fun onRemove(listener: () -> Unit) {
        removeListener = listener
    }

    private fun applyCheckedLook(checked: Boolean) {
        val flags = binding.rowName.paintFlags
        binding.rowName.paintFlags = if (checked) {
            flags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            flags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        binding.rowName.setTextColor(
            ContextCompat.getColor(
                context,
                if (checked) R.color.ink_faint else R.color.ink
            )
        )
    }
}
