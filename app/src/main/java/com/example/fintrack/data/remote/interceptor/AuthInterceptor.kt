package com.example.fintrack.data.remote.interceptor

import android.content.Context
import com.example.fintrack.core.util.LocaleHelper
import com.example.fintrack.data.local.TokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    @ApplicationContext private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenManager.getToken().firstOrNull() }
        if (token == null) {
            Timber.d("Request sent without auth token: %s", chain.request().url)
        }
        val languageCode = LocaleHelper.getLanguage(context)
        val request = chain.request().newBuilder().apply {
            token?.let { addHeader("Authorization", "Bearer $it") }
            addHeader("Accept-Language", languageCode)
        }.build()
        return chain.proceed(request)
    }
}
