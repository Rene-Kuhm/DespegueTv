package com.nuvio.tv.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.nuvio.tv.domain.model.TmdbSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tmdbSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "tmdb_settings")

@Singleton
class TmdbSettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.tmdbSettingsDataStore

    private val enabledKey = booleanPreferencesKey("tmdb_enabled")
    private val useArtworkKey = booleanPreferencesKey("tmdb_use_artwork")
    private val useBasicInfoKey = booleanPreferencesKey("tmdb_use_basic_info")
    private val useDetailsKey = booleanPreferencesKey("tmdb_use_details")
    private val useCreditsKey = booleanPreferencesKey("tmdb_use_credits")
    private val useEpisodesKey = booleanPreferencesKey("tmdb_use_episodes")

    val settings: Flow<TmdbSettings> = dataStore.data.map { prefs ->
        TmdbSettings(
            enabled = prefs[enabledKey] ?: true,
            useArtwork = prefs[useArtworkKey] ?: false,
            useBasicInfo = prefs[useBasicInfoKey] ?: false,
            useDetails = prefs[useDetailsKey] ?: false,
            useCredits = prefs[useCreditsKey] ?: false,
            useEpisodes = prefs[useEpisodesKey] ?: false
        )
    }

    suspend fun setEnabled(enabled: Boolean) {
        dataStore.edit { it[enabledKey] = enabled }
    }

    suspend fun setUseArtwork(enabled: Boolean) {
        dataStore.edit { it[useArtworkKey] = enabled }
    }

    suspend fun setUseBasicInfo(enabled: Boolean) {
        dataStore.edit { it[useBasicInfoKey] = enabled }
    }

    suspend fun setUseDetails(enabled: Boolean) {
        dataStore.edit { it[useDetailsKey] = enabled }
    }

    suspend fun setUseCredits(enabled: Boolean) {
        dataStore.edit { it[useCreditsKey] = enabled }
    }

    suspend fun setUseEpisodes(enabled: Boolean) {
        dataStore.edit { it[useEpisodesKey] = enabled }
    }
}
