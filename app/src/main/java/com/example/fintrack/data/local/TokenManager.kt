package com.example.fintrack.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fintrack_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenCrypto: TokenCrypto
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val PAYDAY_KEY = stringPreferencesKey("payday")
    }

    private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpired: SharedFlow<Unit> = _sessionExpired.asSharedFlow()

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = tokenCrypto.encrypt(token) }
    }

    fun getToken(): Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]?.let { tokenCrypto.decrypt(it) }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        context.dataStore.edit { it[REFRESH_TOKEN_KEY] = tokenCrypto.encrypt(refreshToken) }
    }

    fun getRefreshToken(): Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[REFRESH_TOKEN_KEY]?.let { tokenCrypto.decrypt(it) }
    }

    suspend fun savePayday(payday: Int) {
        context.dataStore.edit { it[PAYDAY_KEY] = payday.toString() }
    }

    fun getPayday(): Flow<Int> = context.dataStore.data.map {
        it[PAYDAY_KEY]?.toIntOrNull() ?: 1
    }

    suspend fun clearAll() {
        context.dataStore.edit {
            it.remove(TOKEN_KEY)
            it.remove(REFRESH_TOKEN_KEY)
            it.remove(PAYDAY_KEY)
        }
    }

    fun notifySessionExpired() {
        _sessionExpired.tryEmit(Unit)
    }
}
