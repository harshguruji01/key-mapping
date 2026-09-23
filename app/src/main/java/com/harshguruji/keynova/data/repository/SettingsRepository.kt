package com.harshguruji.keynova.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "keynova_settings")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val MASTER_MAPPING_ENABLED = booleanPreferencesKey("master_mapping_enabled")
        val ACTIVE_PROFILE_ID = stringPreferencesKey("active_profile_id")
        val LOW_POWER_MODE = booleanPreferencesKey("low_power_mode")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val SNAP_TO_GRID = booleanPreferencesKey("snap_to_grid")
        val GRID_SIZE = intPreferencesKey("grid_size")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val VIRTUAL_MOUSE_ENABLED = booleanPreferencesKey("virtual_mouse_enabled")
        val FLOATING_OVERLAY_ENABLED = booleanPreferencesKey("floating_overlay_enabled")
    }

    val isMasterMappingEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.MASTER_MAPPING_ENABLED] ?: true
    }

    val activeProfileId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ACTIVE_PROFILE_ID]
    }

    val isLowPowerMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LOW_POWER_MODE] ?: false
    }

    val isHapticFeedbackEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAPTIC_FEEDBACK] ?: true
    }

    val isSnapToGrid: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SNAP_TO_GRID] ?: true
    }

    val gridSize: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GRID_SIZE] ?: 20
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
    }

    val isVirtualMouseEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.VIRTUAL_MOUSE_ENABLED] ?: true
    }

    val isFloatingOverlayEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.FLOATING_OVERLAY_ENABLED] ?: false
    }

    suspend fun setMasterMappingEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.MASTER_MAPPING_ENABLED] = enabled }
    }

    suspend fun setActiveProfileId(profileId: String) {
        context.dataStore.edit { it[PreferencesKeys.ACTIVE_PROFILE_ID] = profileId }
    }

    suspend fun setLowPowerMode(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.LOW_POWER_MODE] = enabled }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HAPTIC_FEEDBACK] = enabled }
    }

    suspend fun setSnapToGrid(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SNAP_TO_GRID] = enabled }
    }

    suspend fun setGridSize(size: Int) {
        context.dataStore.edit { it[PreferencesKeys.GRID_SIZE] = size }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setVirtualMouseEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.VIRTUAL_MOUSE_ENABLED] = enabled }
    }

    suspend fun setFloatingOverlayEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.FLOATING_OVERLAY_ENABLED] = enabled }
    }
}
