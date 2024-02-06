package com.example.android.photoviewer.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.android.photoviewer.core.app.Constants
import com.example.android.photoviewer.ui.model.DisplayStyle
import com.example.android.photoviewer.ui.theme.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by
        preferencesDataStore(name = Constants.APP_SETTINGS)

@ViewModelScoped
class AppSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context) {

    private object PreferenceKeys {
        val themeKey = stringPreferencesKey(name = Constants.APP_THEME_PREFERENCE_KEY)
        val displayStyleKey = stringPreferencesKey(name = Constants.DISPLAY_STYLE_KEY)
    }

    private val dataStore = context.dataStore

    suspend fun setAppTheme(appTheme: AppTheme) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.themeKey] = appTheme.toString()
        }
    }

    val appTheme: Flow<AppTheme> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val appThemeStr = preferences[PreferenceKeys.themeKey]
            if (appThemeStr != null) {
                AppTheme.valueOf(appThemeStr)
            } else {
                AppTheme.Default
            }
        }

    suspend fun setDisplayStyle(displayStyle: DisplayStyle) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.displayStyleKey] = displayStyle.toString()
        }
    }

    val displayStyle: Flow<DisplayStyle> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val displayStyleStr = preferences[PreferenceKeys.displayStyleKey]
            if (displayStyleStr != null) {
                DisplayStyle.valueOf(displayStyleStr)
            } else {
                DisplayStyle.Card
            }
        }
}