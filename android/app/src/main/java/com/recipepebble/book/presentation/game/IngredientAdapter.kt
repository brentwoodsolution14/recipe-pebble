package com.recipepebble.book.presentation.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.recipepebble.book.databinding.ItemIngredientEditBinding
import com.recipepebble.book.domain.model.Ingredient

class IngredientAdapter(
    private val onToggle: (Int) -> Unit,
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<IngredientAdapter.IngredientHolder>() {

    private val items = mutableListOf<Ingredient>()

    fun submit(ingredients: List<Ingredient>) {
        items.clear()
        items.addAll(ingredients)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientHolder {
        val binding = ItemIngredientEditBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IngredientHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class IngredientHolder(
        private val binding: ItemIngredientEditBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Ingredient) {
            binding.ingredientRow.bind(item.name, item.amount, item.checked, true)
            binding.ingredientRow.onToggle {
                val index = bindingAdapterPosition
                if (index != RecyclerView.NO_POSITION) onToggle(index)
            }
            binding.ingredientRow.onRemove {
                val index = bindingAdapterPosition
                if (index != RecyclerView.NO_POSITION) onRemove(index)
            }
        }
    }
}
