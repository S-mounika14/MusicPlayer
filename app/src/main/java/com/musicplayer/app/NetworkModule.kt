package com.musicplayer.app.di

import android.util.Log
import com.musicplayer.app.data.remote.MusicApi
import com.musicplayer.app.data.repository.MusicRepository
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkModule {

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        prettyPrint = true
        explicitNulls = false
    }

    private val httpClient by lazy {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(jsonConfig)
                // Accept text/javascript as JSON (for iTunes API)
                json(jsonConfig, ContentType.Text.JavaScript)
                json(jsonConfig, ContentType.Text.Plain)
            }

            install(Logging) {
                level = LogLevel.BODY
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("KtorClient", message)
                    }
                }
            }

            install(DefaultRequest) {
                header(HttpHeaders.UserAgent, "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36")
                header(HttpHeaders.Accept, "*/*")
                header(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9")
            }

            expectSuccess = true
        }
    }

    val musicApi: MusicApi by lazy {
        MusicApi(httpClient)
    }

    val musicRepository: MusicRepository by lazy {
        MusicRepository(musicApi)
    }
}