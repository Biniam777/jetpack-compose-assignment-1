package com.example.myproject

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "settings"

val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class DataStoreManager(private val context: Context) {

    private val onboardingKey = booleanPreferencesKey("onboarding_completed")

    // ✅ Save onboarding completion status
    suspend fun saveOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[onboardingKey] = completed
        }
    }

    // ✅ Read onboarding completion status as Flow<Boolean>
    val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[onboardingKey] ?: false
        }
}
