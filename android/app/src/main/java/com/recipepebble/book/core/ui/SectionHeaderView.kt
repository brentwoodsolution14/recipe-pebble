package com.recipepebble.book.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.recipepebble.book.databinding.ViewSectionHeaderBinding

class SectionHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewSectionHeaderBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = VERTICAL
    }

    fun bind(title: String, caption: String) {
        binding.sectionTitle.text = title
        binding.sectionCaption.text = caption
        binding.sectionCaption.visibility = if (caption.isEmpty()) View.GONE else View.VISIBLE
    }
}
