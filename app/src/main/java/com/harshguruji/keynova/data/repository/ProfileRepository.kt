package com.harshguruji.keynova.data.repository

import com.harshguruji.keynova.data.database.KeyNovaDatabase
import com.harshguruji.keynova.data.database.entity.MappingEntity
import com.harshguruji.keynova.data.database.entity.ProfileEntity
import com.harshguruji.keynova.data.database.entity.VirtualControlEntity
import com.harshguruji.keynova.data.model.Mapping
import com.harshguruji.keynova.data.model.Profile
import com.harshguruji.keynova.data.model.VirtualControl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ProfileRepository(private val database: KeyNovaDatabase) {

    private val profileDao = database.profileDao()
    private val mappingDao = database.mappingDao()
    private val controlDao = database.virtualControlDao()

    fun getAllProfilesFlow(): Flow<List<Profile>> {
        return profileDao.getAllProfiles().map { entities ->
            entities.map { entity ->
                val mappings = mappingDao.getMappingsForProfile(entity.id).map { it.toDomain() }
                val controls = controlDao.getControlsForProfile(entity.id).map { it.toDomain() }
                entity.toDomain(mappings, controls)
            }
        }
    }

    suspend fun getProfile(profileId: String): Profile? {
        val entity = profileDao.getProfileById(profileId) ?: return null
        val mappings = mappingDao.getMappingsForProfile(profileId).map { it.toDomain() }
        val controls = controlDao.getControlsForProfile(profileId).map { it.toDomain() }
        return entity.toDomain(mappings, controls)
    }

    suspend fun getProfileForPackage(packageName: String): Profile? {
        val entity = profileDao.getProfileForPackage(packageName) ?: return null
        val mappings = mappingDao.getMappingsForProfile(entity.id).map { it.toDomain() }
        val controls = controlDao.getControlsForProfile(entity.id).map { it.toDomain() }
        return entity.toDomain(mappings, controls)
    }

    suspend fun getDefaultProfile(): Profile {
        val entity = profileDao.getDefaultProfile()
        if (entity != null) {
            val mappings = mappingDao.getMappingsForProfile(entity.id).map { it.toDomain() }
            val controls = controlDao.getControlsForProfile(entity.id).map { it.toDomain() }
            return entity.toDomain(mappings, controls)
        }

        // Initialize default profiles if empty
        val defaultFps = DefaultProfiles.createFpsProfile()
        val moba = DefaultProfiles.createMobaProfile()
        val retro = DefaultProfiles.createRetroGamepadProfile()

        saveProfile(defaultFps)
        saveProfile(moba)
        saveProfile(retro)

        return defaultFps
    }

    suspend fun saveProfile(profile: Profile) {
        val profileEntity = ProfileEntity(
            id = profile.id,
            name = profile.name,
            description = profile.description,
            targetPackage = profile.targetPackage,
            isDefault = profile.isDefault,
            cursorSensitivityX = profile.cursorSensitivityX,
            cursorSensitivityY = profile.cursorSensitivityY,
            cursorAcceleration = profile.cursorAcceleration,
            scrollSensitivity = profile.scrollSensitivity,
            createdAt = profile.createdAt,
            updatedAt = System.currentTimeMillis()
        )
        profileDao.insertProfile(profileEntity)

        // Save mappings
        val mappingEntities = profile.mappings.map { mapping ->
            MappingEntity(
                id = mapping.id,
                profileId = profile.id,
                name = mapping.name,
                deviceType = mapping.deviceType,
                inputType = mapping.inputType,
                inputCode = mapping.inputCode,
                secondaryCodesCsv = mapping.secondaryCodes.joinToString(","),
                actionType = mapping.actionType,
                actionMode = mapping.actionMode,
                targetControlId = mapping.targetControlId,
                enabled = mapping.enabled,
                sensitivity = mapping.sensitivity,
                holdThresholdMs = mapping.holdThresholdMs,
                repeatIntervalMs = mapping.repeatIntervalMs,
                createdAt = mapping.createdAt,
                updatedAt = System.currentTimeMillis()
            )
        }
        mappingDao.deleteMappingsForProfile(profile.id)
        mappingDao.insertAllMappings(mappingEntities)

        // Save controls
        val controlEntities = profile.controls.map { control ->
            VirtualControlEntity(
                id = control.id,
                profileId = profile.id,
                name = control.name,
                type = control.type,
                posX = control.posX,
                posY = control.posY,
                width = control.width,
                height = control.height,
                opacity = control.opacity,
                radius = control.radius,
                deadZone = control.deadZone,
                assignedKeyLabel = control.assignedKeyLabel,
                styleColorHex = control.styleColorHex,
                isVisible = control.isVisible
            )
        }
        controlDao.deleteControlsForProfile(profile.id)
        controlDao.insertAllControls(controlEntities)
    }

    suspend fun duplicateProfile(profileId: String, newName: String): Profile? {
        val original = getProfile(profileId) ?: return null
        val newProfileId = "profile_" + UUID.randomUUID().toString().take(8)
        val duplicatedMappings = original.mappings.map {
            it.copy(id = "map_" + UUID.randomUUID().toString().take(8), profileId = newProfileId)
        }
        val duplicatedControls = original.controls.map {
            it.copy(id = "ctrl_" + UUID.randomUUID().toString().take(8), profileId = newProfileId)
        }
        val duplicated = original.copy(
            id = newProfileId,
            name = newName,
            isDefault = false,
            mappings = duplicatedMappings,
            controls = duplicatedControls,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        saveProfile(duplicated)
        return duplicated
    }

    suspend fun deleteProfile(profileId: String) {
        profileDao.deleteProfileById(profileId)
        mappingDao.deleteMappingsForProfile(profileId)
        controlDao.deleteControlsForProfile(profileId)
    }

    private fun ProfileEntity.toDomain(mappings: List<Mapping>, controls: List<VirtualControl>) = Profile(
        id = id,
        name = name,
        description = description,
        targetPackage = targetPackage,
        isDefault = isDefault,
        cursorSensitivityX = cursorSensitivityX,
        cursorSensitivityY = cursorSensitivityY,
        cursorAcceleration = cursorAcceleration,
        scrollSensitivity = scrollSensitivity,
        mappings = mappings,
        controls = controls,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun MappingEntity.toDomain() = Mapping(
        id = id,
        profileId = profileId,
        name = name,
        deviceType = deviceType,
        inputType = inputType,
        inputCode = inputCode,
        secondaryCodes = if (secondaryCodesCsv.isBlank()) emptyList() else secondaryCodesCsv.split(",").mapNotNull { it.trim().toIntOrNull() },
        actionType = actionType,
        actionMode = actionMode,
        targetControlId = targetControlId,
        enabled = enabled,
        sensitivity = sensitivity,
        holdThresholdMs = holdThresholdMs,
        repeatIntervalMs = repeatIntervalMs,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun VirtualControlEntity.toDomain() = VirtualControl(
        id = id,
        profileId = profileId,
        name = name,
        type = type,
        posX = posX,
        posY = posY,
        width = width,
        height = height,
        opacity = opacity,
        radius = radius,
        deadZone = deadZone,
        assignedKeyLabel = assignedKeyLabel,
        styleColorHex = styleColorHex,
        isVisible = isVisible
    )
}
