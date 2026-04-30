package com.despegue.tv.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authSessionNoticeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "auth_session_notice_store"
)

enum class StartupAuthNotice {
    DESPEGUE,
    TRAKT
}

@Singleton
class AuthSessionNoticeDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val hadDespegueAuthKey = booleanPreferencesKey("had_despegue_auth")
    private val despegueExplicitLogoutKey = booleanPreferencesKey("despegue_explicit_logout")
    private val pendingDespegueNoticeKey = booleanPreferencesKey("pending_despegue_notice")

    private val hadTraktAuthKey = booleanPreferencesKey("had_trakt_auth")
    private val traktExplicitLogoutKey = booleanPreferencesKey("trakt_explicit_logout")
    private val pendingTraktNoticeKey = booleanPreferencesKey("pending_trakt_notice")

    val pendingNotice: Flow<StartupAuthNotice?> = context.authSessionNoticeDataStore.data.map { preferences ->
        when {
            preferences[pendingDespegueNoticeKey] == true -> StartupAuthNotice.DESPEGUE
            preferences[pendingTraktNoticeKey] == true -> StartupAuthNotice.TRAKT
            else -> null
        }
    }

    suspend fun markDespegueAuthenticated() {
        context.authSessionNoticeDataStore.edit { preferences ->
            preferences[hadDespegueAuthKey] = true
            preferences[despegueExplicitLogoutKey] = false
            preferences[pendingDespegueNoticeKey] = false
        }
    }

    suspend fun markDespegueExplicitLogout() {
        context.authSessionNoticeDataStore.edit { preferences ->
            preferences[hadDespegueAuthKey] = false
            preferences[despegueExplicitLogoutKey] = true
            preferences[pendingDespegueNoticeKey] = false
        }
    }

    suspend fun markUnexpectedDespegueLogoutIfNeeded() {
        context.authSessionNoticeDataStore.edit { preferences ->
            val hadAuth = preferences[hadDespegueAuthKey] == true
            val explicitLogout = preferences[despegueExplicitLogoutKey] == true
            if (hadAuth && !explicitLogout) {
                preferences[pendingDespegueNoticeKey] = true
            }
            preferences[hadDespegueAuthKey] = false
            preferences[despegueExplicitLogoutKey] = false
        }
    }

    suspend fun markTraktAuthenticated() {
        context.authSessionNoticeDataStore.edit { preferences ->
            preferences[hadTraktAuthKey] = true
            preferences[traktExplicitLogoutKey] = false
            preferences[pendingTraktNoticeKey] = false
        }
    }

    suspend fun markTraktExplicitLogout() {
        context.authSessionNoticeDataStore.edit { preferences ->
            preferences[hadTraktAuthKey] = false
            preferences[traktExplicitLogoutKey] = true
            preferences[pendingTraktNoticeKey] = false
        }
    }

    suspend fun markUnexpectedTraktLogoutIfNeeded() {
        context.authSessionNoticeDataStore.edit { preferences ->
            val hadAuth = preferences[hadTraktAuthKey] == true
            val explicitLogout = preferences[traktExplicitLogoutKey] == true
            if (hadAuth && !explicitLogout) {
                preferences[pendingTraktNoticeKey] = true
            }
            preferences[hadTraktAuthKey] = false
            preferences[traktExplicitLogoutKey] = false
        }
    }

    suspend fun consumeNotice(notice: StartupAuthNotice) {
        context.authSessionNoticeDataStore.edit { preferences ->
            when (notice) {
                StartupAuthNotice.DESPEGUE -> preferences[pendingDespegueNoticeKey] = false
                StartupAuthNotice.TRAKT -> preferences[pendingTraktNoticeKey] = false
            }
        }
    }
}
