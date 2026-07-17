package com.firstapp.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firstapp.myapplication.databinding.ActivityCategoryManagerBinding

/**
 * Activity that displays the Category Manager screen.
 *
 * Features:
 * - Material Top App Bar with back arrow and more options icon
 * - Summary card showing total category count
 * - Search bar (UI only — for future implementation)
 * - RecyclerView populated with sample category data
 * - Empty state layout (hidden by default, for future database integration)
 * - Floating Action Button for adding categories (UI only)
 *
 * This screen will later become the **Read** and **Manage** part of CRUD operations
 * backed by Room Database. The RecyclerView adapter and item layout
 * are designed to be easily swapped to a database-backed data source.
 *
 * Future capabilities:
 * - Create Category: Add button / FAB opens dialog with name, icon, color
 * - Read Categories: RecyclerView loads from Room Database
 * - Update Category: Edit icon on each item opens pre-filled dialog
 * - Delete Category: Delete confirmation dialog with warning message
 */
class CategoryManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
    }

    /**
     * Sets up the toolbar with back navigation and more options menu.
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_category_manager, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_more_options -> {
                showPlaceholderToast(getString(R.string.cd_more_options))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Shows a short toast as a placeholder for future functionality.
     */
    private fun showPlaceholderToast(action: String) {
        Toast.makeText(
            this,
            getString(R.string.sample_toast_placeholder, action),
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Sets up the RecyclerView with sample category data.
     * Will later be replaced with Room Database queries.
     */
    private fun setupRecyclerView() {
        val sampleCategories = getSampleCategories()
        val adapter = CategoryAdapter { category ->
            showPlaceholderToast(
                getString(R.string.cd_edit_category, category.name)
            )
        }
        adapter.submitList(sampleCategories)
        binding.rvCategories.adapter = adapter
    }

    /**
     * Returns a list of 8 sample categories for UI demonstration.
     * Will be replaced by Room Database queries in a future update.
     *
     * Each category includes:
     * - id: Unique identifier (for future Room @PrimaryKey)
     * - name: Display name of the category
     * - iconResId: Drawable resource ID for the category icon
     * - expenseCount: Sample number of expenses in this category
     * - colorIndicatorResId: Color resource ID for the color indicator
     */
    private fun getSampleCategories(): List<CategoryItem> {
        return listOf(
            CategoryItem(
                id = 1,
                name = getString(R.string.cat_food),
                iconResId = R.drawable.ic_food,
                expenseCount = 42,
                colorIndicatorResId = R.color.primary_container
            ),
            CategoryItem(
                id = 2,
                name = getString(R.string.cat_transport),
                iconResId = R.drawable.ic_transport,
                expenseCount = 18,
                colorIndicatorResId = R.color.secondary_container
            ),
            CategoryItem(
                id = 3,
                name = getString(R.string.cat_shopping),
                iconResId = R.drawable.ic_shopping,
                expenseCount = 25,
                colorIndicatorResId = R.color.tertiary
            ),
            CategoryItem(
                id = 4,
                name = getString(R.string.cat_bills),
                iconResId = R.drawable.ic_bills,
                expenseCount = 31,
                colorIndicatorResId = R.color.error_container
            ),
            CategoryItem(
                id = 5,
                name = getString(R.string.cat_entertainment),
                iconResId = R.drawable.ic_entertainment,
                expenseCount = 15,
                colorIndicatorResId = R.color.secondary
            ),
            CategoryItem(
                id = 6,
                name = getString(R.string.cat_health),
                iconResId = R.drawable.ic_health,
                expenseCount = 9,
                colorIndicatorResId = R.color.card_expense
            ),
            CategoryItem(
                id = 7,
                name = getString(R.string.cat_education),
                iconResId = R.drawable.ic_education,
                expenseCount = 6,
                colorIndicatorResId = R.color.primary
            ),
            CategoryItem(
                id = 8,
                name = getString(R.string.cat_other),
                iconResId = R.drawable.ic_category_outline,
                expenseCount = 12,
                colorIndicatorResId = R.color.surface_variant
            )
        )
    }
}
