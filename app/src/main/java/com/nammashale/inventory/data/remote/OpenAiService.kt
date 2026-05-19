package com.nammashale.inventory.data.remote

import com.nammashale.inventory.data.remote.dto.OpenAiRequest
import com.nammashale.inventory.data.remote.dto.OpenAiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit interface for the OpenAI Chat Completions API.
 * The Authorization header is injected per-call (allows runtime key injection).
 */
interface OpenAiService {

    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: OpenAiRequest
    ): Response<OpenAiResponse>
}
