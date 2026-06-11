package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Gemini REST API Request & Response via Moshi ---

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<ContentJson>,
    val systemInstruction: ContentJson? = null
)

@JsonClass(generateAdapter = true)
data class ContentJson(
    val parts: List<PartJson>
)

@JsonClass(generateAdapter = true)
data class PartJson(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<CandidateJson>?
)

@JsonClass(generateAdapter = true)
data class CandidateJson(
    val content: ContentJson?
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: GeminiApi = retrofit.create(GeminiApi::class.java)
}

class GeminiService {
    suspend fun askAssistant(prompt: String, chatHistory: List<ChatMessageEntity>): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Hello! I am your official FIFA World Cup 2026 AI Fan Assistant. Currently, my API key is not fully configured, but I can still tell you about the tournament!\n\nToday (June 11, 2026), the historic World Cup starts with Mexico vs Senegal at Estadio Azteca, and USA vs England at MetLife Stadium! Ask me anything about tickets, travel between NY and LA, concessions at Estadio Azteca, or how many badges you can earn!"
        }

        // Build system instructions for FIFA 2026
        val systemInstruction = ContentJson(
            parts = listOf(
                PartJson(
                    text = """
                        You are the official intelligence engine of the FIFA World Cup 2026 Mobile Platform. 
                        Your persona is specialized as a sports tech expert, travel companion, stadium guide, and local football oracle.
                        The World Cup has officially started TODAY, June 11, 2026. Keep answers energetic and helpful!
                        Always assist fans with stadium navigation, travel (buses, Metro line details, Rideshare LOT indices), concessions menus, tournament hub schedules, and fan passport items.
                        Be extremely helpful, precise, and polite. Always tailor response to the cities: New York/New Jersey (MetLife), Mexico City (Estadio Azteca), and Vancouver (BC Place).
                    """.trimIndent()
                )
            )
        )

        // Convert chat history into contents
        val contents = mutableListOf<ContentJson>()
        chatHistory.takeLast(10).forEach { msg ->
            contents.add(
                ContentJson(
                    parts = listOf(PartJson(text = if (msg.sender == "USER") msg.content else msg.content))
                )
            )
        }
        contents.add(ContentJson(parts = listOf(PartJson(text = prompt))))

        val request = GeminiRequest(
            contents = contents,
            systemInstruction = systemInstruction
        )

        return try {
            val response = GeminiClient.apiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I apologize, I received an empty response. Let me know if you would like me to lookup schedules or travel transit lines again!"
        } catch (e: Exception) {
            "Error matching assistant feedback directly: ${e.localizedMessage}. Please try asking again!"
        }
    }
}
