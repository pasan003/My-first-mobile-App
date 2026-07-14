package com.firstapp.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.DividerItemDecoration
import com.firstapp.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    /**
     * Sets up the RecyclerView with sample transaction data.
     */
    private fun setupRecyclerView() {
        val sampleTransactions = getSampleTransactions()
        val adapter = TransactionAdapter()
        adapter.submitList(sampleTransactions)
        binding.rvRecentTransactions.adapter = adapter

        // Add subtle divider between items
        binding.rvRecentTransactions.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
                setDrawable(resources.getDrawable(R.drawable.divider_transaction, theme))
            }
        )
    }

    /**
     * Returns a list of sample transactions for UI demonstration.
     */
    private fun getSampleTransactions(): List<Transaction> {
        return listOf(
            Transaction(
                title = getString(R.string.sample_lunch),
                category = getString(R.string.cat_food),
                amount = 850.00,
                date = getString(R.string.date_today),
                iconResId = R.drawable.ic_food
            ),
            Transaction(
                title = getString(R.string.sample_groceries),
                category = getString(R.string.cat_shopping),
                amount = 3450.00,
                date = getString(R.string.date_today),
                iconResId = R.drawable.ic_shopping
            ),
            Transaction(
                title = getString(R.string.sample_bus_fare),
                category = getString(R.string.cat_transport),
                amount = 320.00,
                date = getString(R.string.date_yesterday),
                iconResId = R.drawable.ic_transport
            ),
            Transaction(
                title = getString(R.string.sample_netflix),
                category = getString(R.string.cat_entertainment),
                amount = 1200.00,
                date = "Dec 12",
                iconResId = R.drawable.ic_entertainment
            ),
            Transaction(
                title = getString(R.string.sample_electricity),
                category = getString(R.string.cat_bills),
                amount = 5200.00,
                date = "Dec 10",
                iconResId = R.drawable.ic_bills
            )
        )
    }
}
