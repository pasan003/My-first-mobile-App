package com.firstapp.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firstapp.myapplication.databinding.ItemExpenseHistoryBinding
import java.text.NumberFormat

/**
 * RecyclerView Adapter for displaying a list of [Transaction] items
 * on the Expense History screen.
 *
 * Each item displays a category icon, title, category name, date,
 * and amount using the primary text color (no red/green — negative
 * values indicate spending).
 *
 * @param onItemClick Optional callback invoked when a transaction item is tapped.
 *                    Will later navigate to [ExpenseDetailActivity].
 */
class ExpenseHistoryAdapter(
    private val onItemClick: ((Transaction) -> Unit)? = null
) : ListAdapter<Transaction, ExpenseHistoryAdapter.ViewHolder>(DiffCallback()) {

    /**
     * Holds references to the views in a single expense history item.
     */
    class ViewHolder(private val binding: ItemExpenseHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction, onItemClick: ((Transaction) -> Unit)?) {
            val context = binding.root.context

            // Set category icon
            binding.ivCategoryIcon.setImageResource(transaction.iconResId)

            // Set title, category, and date
            binding.tvTitle.text = transaction.title
            binding.tvCategory.text = transaction.category
            binding.tvDate.text = transaction.date

            // Format and set the amount using primary text color
            val currencyFormat = NumberFormat.getCurrencyInstance()
            val formattedAmount = currencyFormat.format(transaction.amount)

            binding.tvAmount.text = if (transaction.isExpense) {
                "-$formattedAmount"
            } else {
                "+$formattedAmount"
            }

            // Use primary text color for amounts (no red/green)
            binding.tvAmount.setTextColor(context.getColor(R.color.text_primary))

            // Set item click listener
            binding.root.setOnClickListener {
                onItemClick?.invoke(transaction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExpenseHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    /**
     * DiffUtil callback for efficient list updates.
     */
    class DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.title == newItem.title && oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}
