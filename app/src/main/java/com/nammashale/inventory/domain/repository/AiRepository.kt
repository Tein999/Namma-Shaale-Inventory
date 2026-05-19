package com.nammashale.inventory.domain.repository

/**
 * Contract for AI/GenAI operations.
 * Abstracted so we can swap OpenAI for any other provider.
 */
interface AiRepository {
    /**
     * Generate an issue description using AI based on asset context.
     * @param assetName Name of the asset
     * @param condition Current condition string
     * @return AI-generated description string, or error message
     */
    suspend fun generateIssueDescription(
        assetName: String,
        condition: String
    ): Result<String>

    /**
     * Suggest repair actions based on asset name and issue description.
     * @param assetName Name of the asset
     * @param issueDescription The issue being faced
     * @return AI-suggested repair actions
     */
    suspend fun suggestRepairActions(
        assetName: String,
        issueDescription: String
    ): Result<String>
}
