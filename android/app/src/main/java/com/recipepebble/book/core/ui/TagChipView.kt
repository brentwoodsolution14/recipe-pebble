package com.recipepebble.book.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.recipepebble.book.R
import com.recipepebble.book.databinding.ViewTagChipBinding

class TagChipView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewTagChipBinding.inflate(LayoutInflater.from(context), this)

    private var chipLabel: String = ""

    init {
        foregroundGravity = Gravity.CENTER
        isClickable = false
        isFocusable = false
    }

    fun bind(label: String, selected: Boolean) {
        chipLabel = label
        binding.tagLabel.text = label
        binding.tagLabel.setTextColor(
            ContextCompat.getColor(
                context,
                if (selected) R.color.page_cream else R.color.teal
            )
        )
        background = ContextCompat.getDrawable(
            context,
            if (selected) R.drawable.bg_tag_chip_selected else R.drawable.bg_tag_chip
        )
        contentDescription = label
        ViewCompat.setStateDescription(
            this,
            context.getString(
                if (selected) R.string.state_selected else R.string.state_not_selected
            )
        )
    }

    fun makeTappable(onTap: (String) -> Unit) {
        isClickable = true
        isFocusable = true
        setOnClickListener { onTap(chipLabel) }
    }
}
