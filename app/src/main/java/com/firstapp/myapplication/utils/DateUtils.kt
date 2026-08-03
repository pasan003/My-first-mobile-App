package com.firstapp.myapplication.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Date formatting and period-range helpers.
 *
 * All timestamps are stored in the database as epoch millis; these helpers
 * convert them to display strings and compute analytics date ranges.
 */
object DateUtils {

    private val displayDateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val shortDateFormatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val monthYearLabelFormatter = SimpleDateFormat("MMM yyyy", Locale.getDefault())

    /** Formats an epoch-millis timestamp as "15 Jul 2026". */
    fun formatDate(millis: Long): String = displayDateFormatter.format(Date(millis))

    /** Formats an epoch-millis timestamp as "Tue, 15 Jul 2026". */
    fun formatShortDate(millis: Long): String = shortDateFormatter.format(Date(millis))

    /** Formats an epoch-millis timestamp as "01:30 PM". */
    fun formatTime(millis: Long): String = timeFormatter.format(Date(millis))

    /** Returns the month + year (e.g. "Jan 2026") for a timestamp. */
    fun formatMonthYearLabel(millis: Long): String = monthYearLabelFormatter.format(Date(millis))

    /**
     * Returns a [startDate, endDate) range for the given analytics period.
     */
    fun periodRange(period: AnalyticsPeriod): Pair<Long, Long> {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        return when (period) {
            AnalyticsPeriod.THIS_WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                val start = cal.timeInMillis
                cal.add(Calendar.DAY_OF_YEAR, 7)
                start to cal.timeInMillis
            }
            AnalyticsPeriod.THIS_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = cal.timeInMillis
                cal.add(Calendar.MONTH, 1)
                start to cal.timeInMillis
            }
            AnalyticsPeriod.THIS_YEAR -> {
                cal.set(Calendar.DAY_OF_YEAR, 1)
                val start = cal.timeInMillis
                cal.add(Calendar.YEAR, 1)
                start to cal.timeInMillis
            }
        }
    }

    /** Returns the number of days covered by the given period range (minimum 1). */
    fun daysInRange(start: Long, end: Long): Int {
        val millis = (end - start).coerceAtLeast(86_400_000L)
        return (millis / 86_400_000L).toInt()
    }

    /**
     * Returns the start/end timestamps for each month of the current year
     * (Jan–Dec), used by the "Monthly Spending" section.
     */
    fun monthsOfYear(): List<Pair<Long, Long>> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        return (0 until 12).map { monthIndex ->
            cal.set(Calendar.MONTH, monthIndex)
            val start = cal.timeInMillis
            cal.add(Calendar.MONTH, 1)
            val end = cal.timeInMillis
            cal.add(Calendar.MONTH, -1)
            start to end
        }
    }
}

/** The three analytics periods selectable on the Analytics screen. */
enum class AnalyticsPeriod {
    THIS_WEEK,
    THIS_MONTH,
    THIS_YEAR
}
