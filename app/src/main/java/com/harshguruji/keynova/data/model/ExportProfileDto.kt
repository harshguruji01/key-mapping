package com.harshguruji.keynova.data.model

import com.google.gson.annotations.SerializedName

data class ExportProfileDto(
    @SerializedName("formatVersion") val formatVersion: Int = 1,
    @SerializedName("appName") val appName: String = "KeyNova",
    @SerializedName("author") val author: String = "HarshGuruJi",
    @SerializedName("profileName") val profileName: String,
    @SerializedName("description") val description: String,
    @SerializedName("targetPackage") val targetPackage: String? = null,
    @SerializedName("cursorSettings") val cursorSettings: CursorSettingsDto = CursorSettingsDto(),
    @SerializedName("mappings") val mappings: List<ExportMappingDto> = emptyList(),
    @SerializedName("virtualControls") val virtualControls: List<ExportControlDto> = emptyList(),
    @SerializedName("exportedAt") val exportedAt: Long = System.currentTimeMillis()
)

data class CursorSettingsDto(
    @SerializedName("sensitivityX") val sensitivityX: Float = 1.0f,
    @SerializedName("sensitivityY") val sensitivityY: Float = 1.0f,
    @SerializedName("acceleration") val acceleration: Float = 1.0f,
    @SerializedName("scrollSensitivity") val scrollSensitivity: Float = 1.0f
)

data class ExportMappingDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("deviceType") val deviceType: String,
    @SerializedName("inputType") val inputType: String,
    @SerializedName("inputCode") val inputCode: Int,
    @SerializedName("secondaryCodes") val secondaryCodes: List<Int> = emptyList(),
    @SerializedName("actionType") val actionType: String,
    @SerializedName("actionMode") val actionMode: String,
    @SerializedName("targetControlId") val targetControlId: String? = null,
    @SerializedName("enabled") val enabled: Boolean = true,
    @SerializedName("sensitivity") val sensitivity: Float = 1.0f
)

data class ExportControlDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("posX") val posX: Float,
    @SerializedName("posY") val posY: Float,
    @SerializedName("width") val width: Float,
    @SerializedName("height") val height: Float,
    @SerializedName("opacity") val opacity: Float,
    @SerializedName("radius") val radius: Float = 40f,
    @SerializedName("deadZone") val deadZone: Float = 0.15f,
    @SerializedName("assignedKeyLabel") val assignedKeyLabel: String = "",
    @SerializedName("styleColorHex") val styleColorHex: String = "#00F2FE",
    @SerializedName("isVisible") val isVisible: Boolean = true
)
