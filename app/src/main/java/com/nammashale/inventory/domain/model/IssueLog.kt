package com.nammashale.inventory.domain.model

/**
 * Represents a logged issue for a specific asset.
 * Each asset can have multiple issue entries over time.
 */
data class IssueLog(
    val id: Long = 0L,
    val assetId: Long,
    val description: String,
    val aiSuggestion: String = "",
    val dateLogged: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false
)
