package com.firstapp.myapplication.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

/**
 * Consistent currency formatting helper.
 *
 * Formats values as "Rs. 1,250.00" regardless of the device locale,
 * matching the style already used across the SpendWise screens.
 */
object CurrencyUtils {

    private val formatter: NumberFormat = DecimalFormat("#,##0.00")

    /** Formats [amount] as "Rs. 1,250.00". */
    fun format(amount: Double): String = "Rs. " + formatter.format(amount)

    /** Formats [amount] as "1,250.00" without the currency prefix. */
    fun formatNumber(amount: Double): String = formatter.format(amount)

    /** Formats [amount] with no decimal places (used for compact labels). */
    fun formatCompact(amount: Double): String {
        val compact = DecimalFormat("#,##0")
        return "Rs. " + compact.format(amount)
    }

    /** Parses a user-entered amount string like "1,250" or "1250.50" into a Double. */
    fun parse(amountText: String): Double {
        return amountText
            .replace(",", "")
            .trim()
            .toDoubleOrNull() ?: 0.0
    }

    @Suppress("unused")
    private fun defaultLocale(): Locale = Locale.getDefault()
}
