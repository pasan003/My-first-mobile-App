package com.firstapp.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firstapp.myapplication.databinding.ItemTransactionBinding
import com.firstapp.myapplication.utils.CurrencyUtils

/**
 * RecyclerView Adapter for displaying a list of [Transaction] items.
 *
 * @param onItemClick Callback invoked when a transaction item is tapped.
 */
class TransactionAdapter(
    private val onItemClick: ((Transaction) -> Unit)? = null
) :
    ListAdapter<Transaction, TransactionAdapter.ViewHolder>(DiffCallback()) {

    /**
     * Holds references to the views in a single transaction item.
     */
    class ViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction, onItemClick: ((Transaction) -> Unit)?) {
            val context = binding.root.context

            // Set category icon
            binding.ivCategoryIcon.setImageResource(transaction.iconResId)

            // Set title and category
            binding.tvTitle.text = transaction.title
            binding.tvCategory.text = transaction.category
            binding.tvDate.text = transaction.date

            // Format and set the amount
            val formattedAmount = CurrencyUtils.format(transaction.amount)

            binding.tvAmount.text = if (transaction.isExpense) {
                "-$formattedAmount"
            } else {
                "+$formattedAmount"
            }

            // Style amount text based on income vs expense
            binding.tvAmount.setTextColor(
                if (transaction.isExpense) {
                    context.getColor(R.color.text_expense)
                } else {
                    context.getColor(R.color.text_income)
                }
            )

            // Set item click listener
            binding.root.setOnClickListener {
                onItemClick?.invoke(transaction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    /**
     * DiffUtil callback for efficient list updates.
     * Matches on the database [Transaction.id] for stable item identity.
     */
    class DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}
