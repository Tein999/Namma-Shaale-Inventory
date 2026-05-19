package com.nammashale.inventory.data.repository

import com.nammashale.inventory.BuildConfig
import com.nammashale.inventory.data.remote.OpenAiService
import com.nammashale.inventory.data.remote.dto.ChatMessage
import com.nammashale.inventory.data.remote.dto.OpenAiRequest
import com.nammashale.inventory.domain.repository.AiRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AiRepository using OpenAI Chat Completions API.
 *
 * If the API key is "MOCK_KEY" (not configured), returns helpful mock responses
 * so the app works fully offline / in demo mode.
 *
 * HOW TO USE A REAL KEY:
 * 1. Open (or create) local.properties in your project root
 * 2. Add:  OPENAI_API_KEY=sk-your-real-key-here
 * 3. Rebuild the project
 */
@Singleton
class AiRepositoryImpl @Inject constructor(
    private val openAiService: OpenAiService
) : AiRepository {

    private val isMockMode: Boolean
        get() = BuildConfig.OPENAI_API_KEY == "MOCK_KEY" ||
                BuildConfig.OPENAI_API_KEY.isBlank()

    override suspend fun generateIssueDescription(
        assetName: String,
        condition: String
    ): Result<String> {
        if (isMockMode) {
            return Result.success(getMockIssueDescription(assetName, condition))
        }

        return try {
            val request = OpenAiRequest(
                messages = listOf(
                    ChatMessage(
                        role = "system",
                        content = """You are an assistant for a school asset management system.
                            |Generate a concise, professional issue description for a school asset.
                            |Keep it under 50 words. Be specific and actionable.""".trimMargin()
                    ),
                    ChatMessage(
                        role = "user",
                        content = """Asset: $assetName
                            |Current Condition: $condition
                            |Generate an issue description explaining what might be wrong with this asset.""".trimMargin()
                    )
                )
            )

            val response = openAiService.getChatCompletion(
                authorization = "Bearer ${BuildConfig.OPENAI_API_KEY}",
                request = request
            )

            if (response.isSuccessful) {
                val content = response.body()?.choices?.firstOrNull()?.message?.content
                if (content != null) {
                    Result.success(content.trim())
                } else {
                    Result.failure(Exception("Empty response from AI"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API Error ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            // Network error — fall back to mock
            Result.success(getMockIssueDescription(assetName, condition))
        }
    }

    override suspend fun suggestRepairActions(
        assetName: String,
        issueDescription: String
    ): Result<String> {
        if (isMockMode) {
            return Result.success(getMockRepairSuggestion(assetName, issueDescription))
        }

        return try {
            val request = OpenAiRequest(
                messages = listOf(
                    ChatMessage(
                        role = "system",
                        content = """You are a school maintenance advisor.
                            |Provide practical, step-by-step repair suggestions for school assets.
                            |Be concise and safe. Suggest contacting professionals when needed.""".trimMargin()
                    ),
                    ChatMessage(
                        role = "user",
                        content = """Asset: $assetName
                            |Issue: $issueDescription
                            |Suggest repair/maintenance actions. Keep it under 80 words.""".trimMargin()
                    )
                )
            )

            val response = openAiService.getChatCompletion(
                authorization = "Bearer ${BuildConfig.OPENAI_API_KEY}",
                request = request
            )

            if (response.isSuccessful) {
                val content = response.body()?.choices?.firstOrNull()?.message?.content
                if (content != null) {
                    Result.success(content.trim())
                } else {
                    Result.failure(Exception("Empty response from AI"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.success(getMockRepairSuggestion(assetName, issueDescription))
        }
    }

    // ─── Mock Responses (for demo / offline mode) ─────────────────────────────

    private fun getMockIssueDescription(assetName: String, condition: String): String {
        return when (condition.lowercase()) {
            "broken" -> "$assetName is completely non-functional. Physical damage observed. " +
                "Unit cannot be used for any educational activity. Requires immediate attention."
            "needs_repair", "needs repair" -> "$assetName shows signs of wear and reduced performance. " +
                "Intermittent failures occurring. Scheduled maintenance is overdue."
            else -> "$assetName is currently operational but showing early signs of wear. " +
                "Regular inspection recommended."
        }
    }

    private fun getMockRepairSuggestion(assetName: String, issueDescription: String): String {
        return """Recommended actions for $assetName:
1. Document the issue with photos before any repair attempt.
2. Check if the item is under warranty or insurance coverage.
3. Contact the school maintenance department or approved vendor.
4. If safe to do so, attempt basic troubleshooting (restart, clean, reconnect).
5. Tag the asset with a "DO NOT USE" label if it poses a safety risk.
6. Log all repair activities in this system for future reference."""
    }
}
