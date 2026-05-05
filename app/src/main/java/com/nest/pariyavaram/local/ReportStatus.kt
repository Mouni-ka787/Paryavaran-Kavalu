package com.nest.pariyavaram.local

/**
 * Type-safe wrapper for the two report statuses used across the app.
 * The [value] string is what gets stored in / compared against the Room DB column.
 *
 * Usage:
 *   report.status == ReportStatus.PENDING.value   // compare with DB string
 *   ReportStatus.fromString(report.status)         // DB string → enum
 *   dao.updateStatus(id, ReportStatus.CLEANED.value)
 */
enum class ReportStatus(val value: String, val displayName: String) {

    PENDING("Pending", "Pending"),
    CLEANED("Cleaned", "Cleaned");

    companion object {
        /** Safely convert a raw DB string to a ReportStatus. Defaults to PENDING. */
        fun fromString(value: String?): ReportStatus =
            entries.firstOrNull { it.value.equals(value, ignoreCase = true) } ?: PENDING

        /** All status values as plain strings — handy for filter chip lists. */
        val allValues: List<String> = entries.map { it.value }
    }
}