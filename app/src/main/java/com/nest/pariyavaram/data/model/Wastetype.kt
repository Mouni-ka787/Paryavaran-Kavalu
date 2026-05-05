package com.nest.pariyavaram.data.model

/**
 * Represents the category of waste for a report.
 * Used in NewReportScreen card selector and stored in WasteReport entity.
 */
enum class WasteType(
    val displayName: String,
    val emoji      : String,
    val description: String
) {
    PLASTIC(
        displayName = "Plastic",
        emoji       = "🛍️",
        description = "Bags, bottles, wrappers"
    ),
    ORGANIC(
        displayName = "Organic",
        emoji       = "🍃",
        description = "Food waste, garden waste"
    ),
    ELECTRONIC(
        displayName = "E-Waste",
        emoji       = "📱",
        description = "Devices, batteries, wires"
    ),
    MIXED(
        displayName = "Mixed",
        emoji       = "🗑️",
        description = "General / unclassified waste"
    ),
    MEDICAL(
        displayName = "Medical",
        emoji       = "🏥",
        description = "Medical / bio-hazard waste"
    );

    companion object {
        fun fromName(name: String?): WasteType =
            entries.firstOrNull { it.name == name } ?: MIXED
    }
}