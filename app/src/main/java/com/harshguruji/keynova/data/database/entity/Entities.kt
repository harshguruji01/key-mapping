package com.harshguruji.keynova.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.harshguruji.keynova.data.model.ActionMode
import com.harshguruji.keynova.data.model.ActionType
import com.harshguruji.keynova.data.model.ControlType
import com.harshguruji.keynova.data.model.DeviceType
import com.harshguruji.keynova.data.model.InputType

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val targetPackage: String? = null,
    val isDefault: Boolean = false,
    val cursorSensitivityX: Float = 1.0f,
    val cursorSensitivityY: Float = 1.0f,
    val cursorAcceleration: Float = 1.0f,
    val scrollSensitivity: Float = 1.0f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "mappings",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["profileId"]), Index(value = ["profileId", "inputCode"])]
)
data class MappingEntity(
    @PrimaryKey val id: String,
    val profileId: String,
    val name: String,
    val deviceType: DeviceType,
    val inputType: InputType,
    val inputCode: Int,
    val secondaryCodesCsv: String = "", // comma-separated keycodes
    val actionType: ActionType,
    val actionMode: ActionMode = ActionMode.TAP,
    val targetControlId: String? = null,
    val enabled: Boolean = true,
    val sensitivity: Float = 1.0f,
    val holdThresholdMs: Long = 300L,
    val repeatIntervalMs: Long = 100L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "virtual_controls",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["profileId"])]
)
data class VirtualControlEntity(
    @PrimaryKey val id: String,
    val profileId: String,
    val name: String,
    val type: ControlType,
    val posX: Float,
    val posY: Float,
    val width: Float = 64f,
    val height: Float = 64f,
    val opacity: Float = 0.85f,
    val radius: Float = 40f,
    val deadZone: Float = 0.15f,
    val assignedKeyLabel: String = "",
    val styleColorHex: String = "#00F2FE",
    val isVisible: Boolean = true
)
