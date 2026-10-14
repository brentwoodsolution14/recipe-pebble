package com.recipepebble.book.presentation.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.recipepebble.book.R
import com.recipepebble.book.core.util.SpriteCatalog
import com.recipepebble.book.core.util.TimeFormatter
import com.recipepebble.book.databinding.ItemRecipeCardBinding
import com.recipepebble.book.domain.model.Recipe

class RecipeAdapter(
    private val onOpen: (Recipe) -> Unit,
    private val onFavourite: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeHolder>() {

    private val items = mutableListOf<Recipe>()

    fun submit(recipes: List<Recipe>) {
        items.clear()
        items.addAll(recipes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeHolder {
        val binding = ItemRecipeCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class RecipeHolder(
        private val binding: ItemRecipeCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            val context = binding.root.context
            binding.recipeName.text = recipe.name
            binding.recipeMeta.text =
                TimeFormatter.cardMeta(recipe.minutes, recipe.ingredients.size)
            binding.recipeThumb.setImageResource(SpriteCatalog.drawableFor(recipe.spriteKey))

            bindTag(0, recipe)
            bindTag(1, recipe)

            binding.recipeFavourite.setIconResource(
                if (recipe.favourite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            )
            binding.recipeFavourite.iconTint = ContextCompat.getColorStateList(
                context,
                if (recipe.favourite) R.color.terracotta else R.color.ink_faint
            )
            ViewCompat.setStateDescription(
                binding.recipeFavourite,
                context.getString(
                    if (recipe.favourite) R.string.state_favourite_on
                    else R.string.state_favourite_off
                )
            )
            binding.recipeFavourite.setOnClickListener { onFavourite(recipe) }
            binding.recipeCard.setOnClickListener { onOpen(recipe) }
        }

        private fun bindTag(index: Int, recipe: Recipe) {
            val chip = if (index == 0) binding.recipeTagOne else binding.recipeTagTwo
            val label = recipe.tags.getOrNull(index)
            if (label == null) {
                chip.visibility = View.GONE
            } else {
                chip.visibility = View.VISIBLE
                chip.bind(label, false)
            }
        }
    }
}
