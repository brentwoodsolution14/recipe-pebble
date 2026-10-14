package com.recipepebble.book.core.ui

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.recipepebble.book.R
import com.recipepebble.book.databinding.ViewStatCardBinding

class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewStatCardBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        background = ContextCompat.getDrawable(context, R.drawable.bg_stat_card)
        val pad = resources.getDimensionPixelSize(R.dimen.gap_s)
        setPadding(pad, pad, pad, pad)
    }

    fun setStat(label: String, value: String, colorRes: Int) {
        val tint = ContextCompat.getColor(context, colorRes)
        binding.statValue.text = value
        binding.statValue.setTextColor(tint)
        binding.statLabel.text = label
        binding.statAccent.background?.mutate()?.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
        contentDescription = label + " " + value
    }

    fun setVisibleIfPositive(value: Int) {
        visibility = if (value > 0) View.VISIBLE else View.GONE
    }
}
