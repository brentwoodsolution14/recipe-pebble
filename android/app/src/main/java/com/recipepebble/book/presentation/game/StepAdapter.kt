package com.recipepebble.book.presentation.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.recipepebble.book.databinding.ItemStepBinding
import com.recipepebble.book.domain.model.RecipeStep

class StepAdapter(
    private val onMove: (Int, Int) -> Unit
) : RecyclerView.Adapter<StepAdapter.StepHolder>() {

    private val items = mutableListOf<RecipeStep>()

    fun submit(steps: List<RecipeStep>) {
        items.clear()
        items.addAll(steps)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepHolder {
        val binding = ItemStepBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StepHolder(binding)
    }

    override fun onBindViewHolder(holder: StepHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    inner class StepHolder(
        private val binding: ItemStepBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(step: RecipeStep, position: Int) {
            binding.stepNumber.text = (position + 1).toString()
            binding.stepText.text = step.text
            binding.stepUp.isEnabled = position > 0
            binding.stepDown.isEnabled = position < items.size - 1
            binding.stepUp.alpha = if (position > 0) 1f else DISABLED_ALPHA
            binding.stepDown.alpha = if (position < items.size - 1) 1f else DISABLED_ALPHA
            binding.stepUp.setOnClickListener {
                val index = bindingAdapterPosition
                if (index != RecyclerView.NO_POSITION) onMove(index, -1)
            }
            binding.stepDown.setOnClickListener {
                val index = bindingAdapterPosition
                if (index != RecyclerView.NO_POSITION) onMove(index, 1)
            }
        }
    }

    companion object {
        private const val DISABLED_ALPHA = 0.35f
    }
}
