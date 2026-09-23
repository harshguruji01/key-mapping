package com.harshguruji.keynova

import android.app.Application
import com.harshguruji.keynova.data.database.KeyNovaDatabase
import com.harshguruji.keynova.data.repository.ProfileRepository
import com.harshguruji.keynova.data.repository.SettingsRepository
import com.harshguruji.keynova.input.InputDeviceManager
import com.harshguruji.keynova.input.MappingEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class KeyNovaApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { KeyNovaDatabase.getInstance(this) }
    val profileRepository by lazy { ProfileRepository(database) }
    val settingsRepository by lazy { SettingsRepository(this) }
    val deviceManager by lazy { InputDeviceManager(this) }
    val mappingEngine by lazy { MappingEngine(deviceManager, applicationScope) }

    val isAccessibilityEnabled = MutableStateFlow(false)

    override fun onCreate() {
        super.onCreate()
        instance = this

        deviceManager.startListening()

        // Load initial default profile and settings
        applicationScope.launch {
            val defaultProfile = profileRepository.getDefaultProfile()
            mappingEngine.loadProfile(defaultProfile)

            settingsRepository.isMasterMappingEnabled.collect { isMasterOn ->
                mappingEngine.setMasterActive(isMasterOn)
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        deviceManager.stopListening()
    }

    companion object {
        lateinit var instance: KeyNovaApp
            private set
    }
}
