package com.firstapp.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.firstapp.myapplication.databinding.ActivityCategoryManagerBinding
import com.firstapp.myapplication.databinding.DialogAddEditCategoryBinding
import com.firstapp.myapplication.databinding.DialogDeleteCategoryBinding
import com.firstapp.myapplication.database.entity.Category
import com.firstapp.myapplication.utils.CategoryVisuals
import com.firstapp.myapplication.utils.CurrencyUtils
import com.firstapp.myapplication.viewmodel.CategorySaveResult
import com.firstapp.myapplication.viewmodel.CategorySortOption
import com.firstapp.myapplication.viewmodel.CategoryViewModel
import com.firstapp.myapplication.viewmodel.UserProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Category Manager backed by the Room database.
 *
 * - Categories load from Room together with their live transaction count
 *   and total amount (both aggregated by SQL queries, not the UI)
 * - The search bar filters categories in real time while typing
 * - The toolbar menu opens the sort dialog (name, most/least used,
 *   highest/lowest spending — remembered until the screen closes)
 * - FAB / edit icon open the add-edit dialog (name, icon, color) with
 *   duplicate-name validation
 * - Long-press opens the delete flow: categories without expenses are
 *   deleted normally; categories in use first ask to move their expenses
 *   to "Other" inside one Room transaction, so no expense is ever orphaned
 * - Amounts are formatted with the user profile's currency
 */
class CategoryManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryManagerBinding
    private val viewModel: CategoryViewModel by viewModels()
    private val profileViewModel: UserProfileViewModel by viewModels()

    private lateinit var adapter: CategoryAdapter

    /** Current search text — drives the "no matching categories" message. */
    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupFab()
        observeData()
        observeCurrency()
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
                showSortDialog()
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
     * Wires the search bar so every keystroke filters the categories in real time.
     */
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { editable ->
            searchQuery = editable?.toString().orEmpty()
            viewModel.setSearchQuery(searchQuery)
        }
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
     * Observes the raw category count (empty state) and the filtered list
     * (RecyclerView + "no matching categories" message).
     */
    private fun observeData() {
        viewModel.categoryCount.observe(this) { total ->
            binding.tvSummaryCount.text = total.toString()

            val isEmpty = total == 0
            binding.cardSummary.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.cardSearch.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.layoutEmptyState.root.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }

        viewModel.categories.observe(this) { categories ->
            adapter.submitList(categories)

            val noResults = categories.isEmpty() && searchQuery.isNotBlank()
            binding.rvCategories.visibility = if (categories.isEmpty()) View.GONE else View.VISIBLE
            binding.tvNoResults.visibility = if (noResults) View.VISIBLE else View.GONE
        }
    }

    /**
     * Observes the user profile so amounts are formatted with the preferred currency.
     */
    private fun observeCurrency() {
        profileViewModel.profile.observe(this) { profile ->
            adapter.currencySymbol = profile?.let { CurrencyUtils.symbolFor(it.currency) }
                ?: CurrencyUtils.DEFAULT_SYMBOL
            adapter.notifyDataSetChanged()
        }
    }

    /**
     * Shows the sort selection dialog with the five sort options.
     * The chosen option is stored in the ViewModel, so it is remembered
     * until the screen is closed (but survives rotation).
     */
    private fun showSortDialog() {
        val options = listOf(
            getString(R.string.sort_name_az),
            getString(R.string.sort_most_used),
            getString(R.string.sort_least_used),
            getString(R.string.sort_highest_spending),
            getString(R.string.sort_lowest_spending)
        )
        val selectedIndex = viewModel.sortOption.value.ordinal

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.sort_categories_title)
            .setSingleChoiceItems(options.toTypedArray(), selectedIndex) { dialog, which ->
                viewModel.setSortOption(CategorySortOption.entries[which])
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
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
                ) { result ->
                    showSaveResultToast(result, R.string.category_added)
                }
            } else {
                viewModel.update(
                    Category(
                        id = category.id.toLong(),
                        name = name,
                        icon = selectedIcon,
                        color = selectedColor
                    )
                ) { result ->
                    showSaveResultToast(result, R.string.category_updated)
                }
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Shows a toast for the outcome of an insert/update, e.g. success or duplicate name.
     */
    private fun showSaveResultToast(result: CategorySaveResult, successMessage: Int) {
        runOnUiThread {
            val messageRes = when (result) {
                CategorySaveResult.SUCCESS -> successMessage
                CategorySaveResult.DUPLICATE_NAME -> R.string.error_category_duplicate
            }
            Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shows the delete flow.
     *
     * Categories without expenses are deleted normally. Categories that are
     * still used by expenses show a warning with a "Move to Other & Delete"
     * action that reassigns every expense to the "Other" category inside one
     * Room transaction, so no expense is ever left without a valid category.
     */
    private fun showDeleteDialog(category: CategoryItem) {
        val dialogBinding = DialogDeleteCategoryBinding.inflate(layoutInflater)

        val inUse = category.expenseCount > 0
        if (inUse) {
            dialogBinding.tvDeleteMessage.text =
                getString(R.string.delete_category_in_use_message, category.expenseCount)
            dialogBinding.btnDeleteConfirm.text = getString(R.string.delete_category_move_to_other)
        }

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
                ),
                moveExpensesToOther = inUse
            ) { success ->
                runOnUiThread {
                    Toast.makeText(
                        this,
                        if (success) R.string.category_deleted
                        else R.string.error_category_delete_blocked,
                        Toast.LENGTH_SHORT
                    ).show()
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
