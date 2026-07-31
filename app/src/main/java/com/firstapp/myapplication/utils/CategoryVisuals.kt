package com.firstapp.myapplication.utils

import com.firstapp.myapplication.R

/**
 * Maps stored category icon/color *names* (e.g. "ic_food", "primary_container")
 * to the actual drawable/color resource IDs used by the UI.
 */
object CategoryVisuals {

    /** Drawable resource names that can be assigned to a category. */
    val availableIcons: List<String> = listOf(
        "ic_food",
        "ic_transport",
        "ic_shopping",
        "ic_bills",
        "ic_entertainment",
        "ic_health",
        "ic_education",
        "ic_category_outline"
    )

    /** Color resource names that can be assigned to a category. */
    val availableColors: List<String> = listOf(
        "primary",
        "secondary",
        "tertiary",
        "primary_container",
        "secondary_container",
        "error_container",
        "text_income",
        "surface_variant"
    )

    private val iconMap: Map<String, Int> = mapOf(
        "ic_food" to R.drawable.ic_food,
        "ic_transport" to R.drawable.ic_transport,
        "ic_shopping" to R.drawable.ic_shopping,
        "ic_bills" to R.drawable.ic_bills,
        "ic_entertainment" to R.drawable.ic_entertainment,
        "ic_health" to R.drawable.ic_health,
        "ic_education" to R.drawable.ic_education,
        "ic_category_outline" to R.drawable.ic_category_outline
    )

    private val colorMap: Map<String, Int> = mapOf(
        "primary" to R.color.primary,
        "secondary" to R.color.secondary,
        "tertiary" to R.color.tertiary,
        "primary_container" to R.color.primary_container,
        "secondary_container" to R.color.secondary_container,
        "error_container" to R.color.error_container,
        "text_income" to R.color.text_income,
        "surface_variant" to R.color.surface_variant
    )

    fun iconResId(iconName: String): Int = iconMap[iconName] ?: R.drawable.ic_category_outline

    fun colorResId(colorName: String): Int = colorMap[colorName] ?: R.color.primary_container
}
