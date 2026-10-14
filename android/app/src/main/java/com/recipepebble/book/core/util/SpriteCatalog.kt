package com.recipepebble.book.core.util

import androidx.annotation.DrawableRes
import com.recipepebble.book.R
import com.recipepebble.book.data.sample.SampleData

object SpriteCatalog {

    @DrawableRes
    fun drawableFor(key: String): Int = when (key) {
        SampleData.SPRITE_BREAD -> R.drawable.sprite_bread
        SampleData.SPRITE_PEPPER -> R.drawable.sprite_pepper
        SampleData.SPRITE_CUPCAKE -> R.drawable.sprite_cupcake
        else -> R.drawable.sprite_herb
    }
}
