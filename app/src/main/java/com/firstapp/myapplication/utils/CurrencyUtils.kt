package com.firstapp.myapplication.utils

import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * Consistent currency formatting helper.
 *
 * Formats values as "Rs. 1,250.00" regardless of the device locale,
 * matching the style already used across the SpendWise screens.
 */
object CurrencyUtils {

    /** Default symbol used when no profile currency is available. */
    const val DEFAULT_SYMBOL = "Rs."

    private val formatter: NumberFormat = DecimalFormat("#,##0.00")

    /** Formats [amount] as "Rs. 1,250.00" using the default symbol. */
    fun format(amount: Double): String = format(amount, DEFAULT_SYMBOL)

    /** Formats [amount] with a custom symbol, e.g. format(1250.0, "$") -> "$ 1,250.00". */
    fun format(amount: Double, symbol: String): String = "$symbol " + formatter.format(amount)

    /**
     * Maps a stored currency code (e.g. "LKR") to its display symbol.
     * Falls back to the default "Rs." symbol for unknown codes.
     */
    fun symbolFor(currencyCode: String): String = when (currencyCode.uppercase()) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "JPY" -> "¥"
        "INR" -> "₹"
        "AUD" -> "A$"
        "CAD" -> "C$"
        else -> DEFAULT_SYMBOL
    }

    /**
     * Maps a stored currency code to a friendly display name, e.g.
     * "LKR" -> "Sri Lankan Rupee (LKR)". Falls back to the code itself.
     */
    fun currencyDisplay(currencyCode: String): String = when (currencyCode.uppercase()) {
        "LKR" -> "Sri Lankan Rupee (LKR)"
        "USD" -> "US Dollar (USD)"
        "EUR" -> "Euro (EUR)"
        "GBP" -> "British Pound (GBP)"
        "JPY" -> "Japanese Yen (JPY)"
        "INR" -> "Indian Rupee (INR)"
        "AUD" -> "Australian Dollar (AUD)"
        "CAD" -> "Canadian Dollar (CAD)"
        else -> currencyCode.uppercase()
    }
}
