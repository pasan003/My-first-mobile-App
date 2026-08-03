package com.firstapp.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firstapp.myapplication.databinding.ItemCategoryBinding
import com.firstapp.myapplication.utils.CurrencyUtils
import com.firstapp.myapplication.utils.UiAnimations

/**
 * RecyclerView Adapter for displaying a list of [CategoryItem] objects
 * on the Category Manager screen.
 *
 * Each item displays a category icon, name, transaction count, total amount
 * spent, color indicator, and an edit icon on the right.
 *
 * @param onEditClick Callback invoked when the edit icon on a category is tapped.
 * @param onItemLongClick Callback invoked when a category row is long-pressed.
 */
class CategoryAdapter(
    private val onEditClick: ((CategoryItem) -> Unit)? = null,
    private val onItemLongClick: ((CategoryItem) -> Unit)? = null
) : ListAdapter<CategoryItem, CategoryAdapter.ViewHolder>(DiffCallback()) {

    /** Item ids that have already played their entrance animation. */
    private val animatedIds = mutableSetOf<Long>()

    /**
     * Currency symbol used to format the total amount. Set by the screen
     * (from the user profile) and applied on the next re-bind.
     */
    var currencySymbol: String = CurrencyUtils.DEFAULT_SYMBOL

    /**
     * Holds references to the views in a single category item.
     */
    class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            category: CategoryItem,
            currencySymbol: String,
            onEditClick: ((CategoryItem) -> Unit)?,
            onItemLongClick: ((CategoryItem) -> Unit)?
        ) {
            val context = binding.root.context

            // Set category icon
            binding.ivCategoryIcon.setImageResource(category.iconResId)

            // Set category name
            binding.tvCategoryName.text = category.name

            // Set transaction count with plural handling
            binding.tvExpenseCount.text = context.resources.getQuantityString(
                R.plurals.category_expense_count,
                category.expenseCount,
                category.expenseCount
            )

            // Set total amount spent in this category
            binding.tvTotalAmount.text =
                CurrencyUtils.format(category.totalAmount, currencySymbol)

            // Set color indicator background
            binding.viewColorIndicator.setBackgroundColor(
                context.getColor(category.colorIndicatorResId)
            )

            // Set edit icon click listener
            binding.ivEditCategory.setOnClickListener {
                onEditClick?.invoke(category)
            }

            // Long-press the item to delete the category
            binding.root.setOnLongClickListener {
                onItemLongClick?.invoke(category)
                true
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
        val category = getItem(position)
        holder.bind(category, currencySymbol, onEditClick, onItemLongClick)
        UiAnimations.animateItemIn(holder.itemView, animatedIds, category.id.toLong())
    }

    /**
     * DiffUtil callback for efficient list updates.
     * Matches on [CategoryItem.id] for item identity; the data class equals
     * (name, expenseCount, totalAmount, icon, color) for content comparison.
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
