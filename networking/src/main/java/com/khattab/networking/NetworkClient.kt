package com.khattab.networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.parametersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.text.isNotEmpty

class NetworkClient(
    private val baseUrl: String,
    private val apiQueryKey: String = "api_key",
    private val apiKey: String? = null,
    private val enableLogging: Boolean = true,
    private val timeoutMillis: Long = 30_000
) {

    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                }
            )
        }

        if (enableLogging) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMillis
            connectTimeoutMillis = timeoutMillis
            socketTimeoutMillis = timeoutMillis
        }

        defaultRequest {
            if (baseUrl.isNotEmpty()) {
                url(baseUrl)
            }
            header(HttpHeaders.ContentType, ContentType.Application.Json)

            apiKey?.let {
                parametersOf(apiQueryKey, apiKey)
            }
        }

        engine {
            connectTimeout = timeoutMillis.toInt()
            socketTimeout = timeoutMillis.toInt()
        }
    }
}
