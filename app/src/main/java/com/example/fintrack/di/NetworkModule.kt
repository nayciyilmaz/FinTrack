package com.example.fintrack.di

import com.example.fintrack.data.remote.api.AdvisorService
import com.example.fintrack.data.remote.api.AuthService
import com.example.fintrack.data.remote.api.BudgetService
import com.example.fintrack.data.remote.api.RecurringItemService
import com.example.fintrack.data.remote.api.SavingsGoalService
import com.example.fintrack.data.remote.api.TransactionService
import com.example.fintrack.data.remote.interceptor.AuthInterceptor
import com.example.fintrack.data.remote.interceptor.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideTransactionService(retrofit: Retrofit): TransactionService =
        retrofit.create(TransactionService::class.java)

    @Provides
    @Singleton
    fun provideBudgetService(retrofit: Retrofit): BudgetService =
        retrofit.create(BudgetService::class.java)

    @Provides
    @Singleton
    fun provideSavingsGoalService(retrofit: Retrofit): SavingsGoalService =
        retrofit.create(SavingsGoalService::class.java)

    @Provides
    @Singleton
    fun provideAdvisorService(retrofit: Retrofit): AdvisorService =
        retrofit.create(AdvisorService::class.java)

    @Provides
    @Singleton
    fun provideRecurringItemService(retrofit: Retrofit): RecurringItemService =
        retrofit.create(RecurringItemService::class.java)
}
