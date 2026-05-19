package com.nammashale.inventory.domain.model

/**
 * Core domain model representing a school asset.
 * This is the single source of truth used across all layers.
 * Decoupled from Room Entity or any framework.
 */
data class Asset(
    val id: Long = 0L,
    val name: String,
    val serialNumber: String,
    val photoUri: String? = null,
    val condition: AssetCondition = AssetCondition.WORKING,
    val description: String = "",
    val dateAdded: Long = System.currentTimeMillis(),
    val lastHealthCheck: Long? = null,
    val location: String = ""
)
