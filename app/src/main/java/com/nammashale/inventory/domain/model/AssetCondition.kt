package com.nammashale.inventory.domain.model

/**
 * Represents the condition/health state of a school asset.
 * Used for filtering, dashboard stats, and color coding in UI.
 */
enum class AssetCondition(val label: String, val colorHex: String) {
    WORKING("Working", "#4CAF50"),      // Green
    NEEDS_REPAIR("Needs Repair", "#FFC107"), // Yellow/Amber
    BROKEN("Broken", "#F44336")         // Red
}
