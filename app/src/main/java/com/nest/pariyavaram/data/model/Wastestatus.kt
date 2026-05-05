package com.nest.pariyavaram.data.model


/**
 * Lifecycle status of a waste report.
 * Drives map pin colour (red = PENDING, green = CLEANED)
 * and HistoryScreen filter chips.
 */
enum class WasteStatus(
    val displayName: String,
    val emoji: String
) {
    PENDING(
        displayName  = "Pending",
        emoji        = "🔴"
    ),
    CLEANED(
        displayName  = "Cleaned",
        emoji        = "🟢"
    );

    companion object {
        fun fromName(name: String?): WasteStatus =
            values().firstOrNull { it.name == name } ?: PENDING
    }
}