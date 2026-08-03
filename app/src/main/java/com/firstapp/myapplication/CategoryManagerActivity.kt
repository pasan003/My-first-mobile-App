package com.firstapp.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firstapp.myapplication.databinding.ActivityCategoryManagerBinding
import com.firstapp.myapplication.databinding.DialogAddEditCategoryBinding
import com.firstapp.myapplication.databinding.DialogDeleteCategoryBinding
import com.firstapp.myapplication.database.entity.Category
import com.firstapp.myapplication.utils.CategoryVisuals
import com.firstapp.myapplication.viewmodel.CategoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Category Manager backed by the Room database.
 *
 * - Categories + live expense counts load from Room
 * - FAB / edit icon open the add-edit dialog (name, icon, color)
 * - Long-press opens the delete confirmation dialog
 * - Deleting a category that still has expenses is blocked with a message
 */
class CategoryManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryManagerBinding
    private val viewModel: CategoryViewModel by viewModels()

    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        observeData()
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
     * Sets up the Floating Action Button to open the Add Category dialog.
     */
    private fun setupFab() {
        binding.fabAddCategory.setOnClickListener {
            showAddEditDialog(null)
        }
        binding.layoutEmptyState.btnEmptyAddCategory.setOnClickListener {
            showAddEditDialog(null)
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
     * Sets up the RecyclerView and observes categories from Room.
     */
    private fun setupRecyclerView() {
        adapter = CategoryAdapter(
            onEditClick = { category ->
                showAddEditDialog(category)
            },
            onItemLongClick = { category ->
                showDeleteDialog(category)
            }
        )
        binding.rvCategories.adapter = adapter
    }

    /**
     * Observes categories and updates the list, summary badge and empty state.
     */
    private fun observeData() {
        viewModel.categories.observe(this) { categories ->
            adapter.submitList(categories)

            // Summary count badge
            binding.tvSummaryCount.text = categories.size.toString()

            // Empty state
            val isEmpty = categories.isEmpty()
            binding.rvCategories.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.cardSummary.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.cardSearch.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.layoutEmptyState.root.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    /**
     * Shows the Add / Edit category dialog (name, icon, color).
     * When [category] is non-null the dialog is pre-filled for editing.
     */
    private fun showAddEditDialog(category: CategoryItem?) {
        val dialogBinding = DialogAddEditCategoryBinding.inflate(layoutInflater)

        // Title
        dialogBinding.tvDialogTitle.setText(
            if (category == null) R.string.add_category_dialog_title
            else R.string.edit_category_dialog_title
        )

        // Icon dropdown
        val iconLabels = CategoryVisuals.availableIcons.map { iconKey ->
            iconKey.removePrefix("ic_").replace('_', ' ')
        }
        val iconAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            iconLabels
        )
        dialogBinding.actvCategoryIcon.setAdapter(iconAdapter)

        // Color circles: map each circle to a stored color name
        val colorCards = listOf(
            dialogBinding.cardColorPrimary to "primary",
            dialogBinding.cardColorSecondary to "secondary",
            dialogBinding.cardColorTertiary to "tertiary",
            dialogBinding.cardColorPrimaryContainer to "primary_container",
            dialogBinding.cardColorSecondaryContainer to "secondary_container",
            dialogBinding.cardColorErrorContainer to "error_container",
            dialogBinding.cardColorIncome to "text_income",
            dialogBinding.cardColorSurfaceVariant to "surface_variant"
        )

        var selectedColor = "primary_container"
        var selectedIcon = "ic_category_outline"

        // Pre-fill for editing
        if (category != null) {
            dialogBinding.etCategoryName.setText(category.name)
            selectedColor = reverseColorName(category.colorIndicatorResId)
            selectedIcon = reverseIconName(category.iconResId)
            val iconLabel = selectedIcon.removePrefix("ic_").replace('_', ' ')
            dialogBinding.actvCategoryIcon.setText(iconLabel, false)
        }

        // Color selection highlight helper
        fun updateColorSelection() {
            val density = resources.displayMetrics.density
            colorCards.forEach { (card, colorName) ->
                // strokeWidth is in pixels; convert dp values for consistent sizing
                card.strokeWidth =
                    if (colorName == selectedColor) (4 * density).toInt() else (1 * density).toInt()
            }
        }
        colorCards.forEach { (card, colorName) ->
            card.setOnClickListener {
                selectedColor = colorName
                updateColorSelection()
            }
        }
        updateColorSelection()

        // Icon selection
        dialogBinding.actvCategoryIcon.setOnItemClickListener { _, _, position, _ ->
            selectedIcon = CategoryVisuals.availableIcons[position]
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnSave.setOnClickListener {
            val name = dialogBinding.etCategoryName.text?.toString()?.trim().orEmpty()
            if (name.isEmpty()) {
                Toast.makeText(this, R.string.error_category_name_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (category == null) {
                viewModel.insert(
                    Category(name = name, icon = selectedIcon, color = selectedColor)
                ) {
                    runOnUiThread {
                        Toast.makeText(this, R.string.category_added, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                viewModel.update(
                    Category(
                        id = category.id.toLong(),
                        name = name,
                        icon = selectedIcon,
                        color = selectedColor
                    )
                ) {
                    runOnUiThread {
                        Toast.makeText(this, R.string.category_updated, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Shows the delete confirmation dialog. Categories that still have
     * expenses cannot be deleted (would break the foreign key), so a
     * friendly message is shown instead.
     */
    private fun showDeleteDialog(category: CategoryItem) {
        if (category.expenseCount > 0) {
            Toast.makeText(
                this,
                getString(R.string.error_category_in_use, category.expenseCount),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val dialogBinding = DialogDeleteCategoryBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnDeleteCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnDeleteConfirm.setOnClickListener {
            viewModel.delete(
                Category(
                    id = category.id.toLong(),
                    name = category.name,
                    icon = reverseIconName(category.iconResId),
                    color = reverseColorName(category.colorIndicatorResId)
                )
            ) {
                runOnUiThread {
                    Toast.makeText(this, R.string.category_deleted, Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun reverseIconName(iconResId: Int): String {
        return CategoryVisuals.availableIcons.firstOrNull { iconKey ->
            CategoryVisuals.iconResId(iconKey) == iconResId
        } ?: "ic_category_outline"
    }

    private fun reverseColorName(colorResId: Int): String {
        return CategoryVisuals.availableColors.firstOrNull { colorKey ->
            CategoryVisuals.colorResId(colorKey) == colorResId
        } ?: "primary_container"
    }
}
