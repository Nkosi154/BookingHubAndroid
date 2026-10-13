package com.bookinghub.app.data.remote

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "bookinghub_session")

/**
 * Persists the DRF auth token returned by /api/auth/login/ (or /register/) so the
 * user stays logged in between app launches. Everything the app needs to know about
 * "am I logged in" flows from here.
 */
class SessionManager(private val context: Context) {

    private object Keys {
        val TOKEN = stringPreferencesKey("auth_token")
        val USERNAME = stringPreferencesKey("username")
        val IS_PROVIDER = booleanPreferencesKey("is_provider")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[Keys.TOKEN] }
    val usernameFlow: Flow<String?> = context.dataStore.data.map { it[Keys.USERNAME] }
    val isProviderFlow: Flow<Boolean> = context.dataStore.data.map { it[Keys.IS_PROVIDER] ?: false }

    /** Synchronous read used by the OkHttp interceptor, which cannot suspend. */
    fun currentTokenBlocking(): String? = runBlocking { tokenFlow.first() }

    suspend fun saveSession(token: String, username: String, isProvider: Boolean = false) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TOKEN] = token
            prefs[Keys.USERNAME] = username
            prefs[Keys.IS_PROVIDER] = isProvider
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun isLoggedIn(): Boolean = tokenFlow.first() != null
}
