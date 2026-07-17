package com.firstapp.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firstapp.myapplication.databinding.ItemCategoryBinding

/**
 * RecyclerView Adapter for displaying a list of [CategoryItem] objects
 * on the Category Manager screen.
 *
 * Each item displays a category icon, name, expense count, color indicator,
 * and an edit icon on the right.
 *
 * @param onEditClick Callback invoked when the edit icon on a category is tapped.
 */
class CategoryAdapter(
    private val onEditClick: ((CategoryItem) -> Unit)? = null
) : ListAdapter<CategoryItem, CategoryAdapter.ViewHolder>(DiffCallback()) {

    /**
     * Holds references to the views in a single category item.
     */
    class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryItem, onEditClick: ((CategoryItem) -> Unit)?) {
            val context = binding.root.context

            // Set category icon
            binding.ivCategoryIcon.setImageResource(category.iconResId)

            // Set category name
            binding.tvCategoryName.text = category.name

            // Set expense count with plural handling
            binding.tvExpenseCount.text = context.resources.getQuantityString(
                R.plurals.category_expense_count,
                category.expenseCount,
                category.expenseCount
            )

            // Set color indicator background
            binding.viewColorIndicator.setBackgroundColor(
                context.getColor(category.colorIndicatorResId)
            )

            // Set edit icon click listener
            binding.ivEditCategory.setOnClickListener {
                onEditClick?.invoke(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onEditClick)
    }

    /**
     * DiffUtil callback for efficient list updates.
     * Matches on [CategoryItem.id] for item identity and [CategoryItem.name]
     * and [CategoryItem.expenseCount] for content comparison.
     */
    class DiffCallback : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem == newItem
        }
    }
}
