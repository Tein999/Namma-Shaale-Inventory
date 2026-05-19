package com.nammashale.inventory.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility object for date formatting across the app.
 */
object DateUtils {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    fun formatDate(timestamp: Long): String =
        dateFormat.format(Date(timestamp))

    fun formatDateTime(timestamp: Long): String =
        dateTimeFormat.format(Date(timestamp))

    fun formatMonthYear(timestamp: Long): String =
        monthYearFormat.format(Date(timestamp))

    /**
     * Returns human-readable "time ago" string.
     */
    fun timeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < 60_000L -> "Just now"
            diff < 3_600_000L -> "${diff / 60_000}m ago"
            diff < 86_400_000L -> "${diff / 3_600_000}h ago"
            diff < 2_592_000_000L -> "${diff / 86_400_000}d ago"
            else -> formatDate(timestamp)
        }
    }

    /**
     * Checks if a health check is overdue (older than 30 days).
     */
    fun isHealthCheckOverdue(lastCheckTimestamp: Long?): Boolean {
        if (lastCheckTimestamp == null) return true
        val thirtyDaysMs = 30L * 24 * 60 * 60 * 1000
        return (System.currentTimeMillis() - lastCheckTimestamp) > thirtyDaysMs
    }
}
