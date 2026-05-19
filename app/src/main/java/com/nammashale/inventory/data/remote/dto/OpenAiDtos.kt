package com.nammashale.inventory.data.remote.dto

import com.google.gson.annotations.SerializedName

// ─── Request ──────────────────────────────────────────────────────────────────

data class OpenAiRequest(
    @SerializedName("model") val model: String = "gpt-3.5-turbo",
    @SerializedName("messages") val messages: List<ChatMessage>,
    @SerializedName("max_tokens") val maxTokens: Int = 300,
    @SerializedName("temperature") val temperature: Double = 0.7
)

data class ChatMessage(
    @SerializedName("role") val role: String,        // "system" | "user" | "assistant"
    @SerializedName("content") val content: String
)

// ─── Response ─────────────────────────────────────────────────────────────────

data class OpenAiResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("choices") val choices: List<Choice>?,
    @SerializedName("error") val error: OpenAiError?
)

data class Choice(
    @SerializedName("message") val message: ChatMessage?,
    @SerializedName("finish_reason") val finishReason: String?
)

data class OpenAiError(
    @SerializedName("message") val message: String,
    @SerializedName("type") val type: String,
    @SerializedName("code") val code: String?
)
