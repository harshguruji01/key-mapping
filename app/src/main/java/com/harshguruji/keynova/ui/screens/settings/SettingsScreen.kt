package com.harshguruji.keynova.ui.screens.settings

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshguruji.keynova.KeyNovaApp
import com.harshguruji.keynova.service.KeyNovaOverlayService
import com.harshguruji.keynova.ui.components.GlassCard
import com.harshguruji.keynova.ui.components.NeonButton
import com.harshguruji.keynova.ui.components.StatusChip
import com.harshguruji.keynova.ui.theme.BgDark
import com.harshguruji.keynova.ui.theme.CardDark
import com.harshguruji.keynova.ui.theme.NeonCyan
import com.harshguruji.keynova.ui.theme.NeonGreen
import com.harshguruji.keynova.ui.theme.SurfaceDark
import com.harshguruji.keynova.ui.theme.TextMuted
import com.harshguruji.keynova.ui.theme.TextPrimary
import com.harshguruji.keynova.ui.theme.TextSecondary
import com.harshguruji.keynova.utils.PermissionHelper
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val app = KeyNovaApp.instance
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settingsRepo = app.settingsRepository

    val isLowPower by settingsRepo.isLowPowerMode.collectAsState(initial = false)
    val isHaptics by settingsRepo.isHapticFeedbackEnabled.collectAsState(initial = true)
    val isOverlayEnabled by settingsRepo.isFloatingOverlayEnabled.collectAsState(initial = false)
    val isAccessibilityActive by app.isAccessibilityEnabled.collectAsState()

    val activeProfile by app.mappingEngine.activeProfile.collectAsState()
    var sensX by remember(activeProfile?.cursorSensitivityX) { mutableFloatStateOf(activeProfile?.cursorSensitivityX ?: 1.0f) }
    var sensY by remember(activeProfile?.cursorSensitivityY) { mutableFloatStateOf(activeProfile?.cursorSensitivityY ?: 1.0f) }

    val hasOverlayPermission = PermissionHelper.isOverlayPermissionGranted(context)
    val hasAccessibilityPermission = PermissionHelper.isAccessibilityEnabled(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "SETTINGS",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = "PREFERENCES & HARDWARE CALIBRATION",
            color = NeonCyan,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sensitivity Calibration Card
        GlassCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "MOUSE & POINTER SENSITIVITY",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "HORIZONTAL SENSITIVITY (X): ${String.format("%.2f", sensX)}x",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Slider(
                    value = sensX,
                    onValueChange = {
                        sensX = it
                        activeProfile?.let { prof ->
                            val updated = prof.copy(cursorSensitivityX = it)
                            scope.launch {
                                app.profileRepository.saveProfile(updated)
                                app.mappingEngine.loadProfile(updated)
                            }
                        }
                    },
                    valueRange = 0.2f..3.0f,
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan, inactiveTrackColor = SurfaceDark)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "VERTICAL SENSITIVITY (Y): ${String.format("%.2f", sensY)}x",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Slider(
                    value = sensY,
                    onValueChange = {
                        sensY = it
                        activeProfile?.let { prof ->
                            val updated = prof.copy(cursorSensitivityY = it)
                            scope.launch {
                                app.profileRepository.saveProfile(updated)
                                app.mappingEngine.loadProfile(updated)
                            }
                        }
                    },
                    valueRange = 0.2f..3.0f,
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan, inactiveTrackColor = SurfaceDark)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Permissions Hub Card
        GlassCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ANDROID PERMISSIONS HUB",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Accessibility Permission Item
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Accessibility Service", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Enables hardware key event detection and touch simulation", color = TextMuted, fontSize = 11.sp)
                    }
                    StatusChip(
                        text = if (hasAccessibilityPermission) "Granted" else "Disabled",
                        isActive = hasAccessibilityPermission
                    )
                }

                if (!hasAccessibilityPermission) {
                    TextButton(onClick = { PermissionHelper.openAccessibilitySettings(context) }) {
                        Text("Configure Accessibility Settings →", color = NeonCyan, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Overlay Permission Item
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Floating Overlay HUD", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Display floating toggle pill over full-screen games", color = TextMuted, fontSize = 11.sp)
                    }
                    StatusChip(
                        text = if (hasOverlayPermission) "Granted" else "Disabled",
                        isActive = hasOverlayPermission
                    )
                }

                if (!hasOverlayPermission) {
                    TextButton(onClick = { PermissionHelper.openOverlaySettings(context) }) {
                        Text("Grant Overlay Permission →", color = NeonCyan, fontSize = 12.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable In-Game Overlay HUD", color = TextSecondary, fontSize = 12.sp)
                        Switch(
                            checked = isOverlayEnabled,
                            onCheckedChange = { enable ->
                                scope.launch {
                                    settingsRepo.setFloatingOverlayEnabled(enable)
                                    val intent = Intent(context, KeyNovaOverlayService::class.java)
                                    if (enable) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            context.startForegroundService(intent)
                                        } else {
                                            context.startService(intent)
                                        }
                                    } else {
                                        context.stopService(intent)
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = BgDark, checkedTrackColor = NeonCyan)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Performance & Low Power Card
        GlassCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PERFORMANCE & BATTERY",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Low-Power Mode", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Reduces UI recompositions & suspends background telemetry", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isLowPower,
                        onCheckedChange = { scope.launch { settingsRepo.setLowPowerMode(it) } },
                        colors = SwitchDefaults.colors(checkedThumbColor = BgDark, checkedTrackColor = NeonCyan)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Haptic Feedback", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Tactile feedback on key bindings and mapper snaps", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isHaptics,
                        onCheckedChange = { scope.launch { settingsRepo.setHapticFeedback(it) } },
                        colors = SwitchDefaults.colors(checkedThumbColor = BgDark, checkedTrackColor = NeonCyan)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App info
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "KeyNova v1.0.0 • Designed for Android 11 to 16+",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Crafted by HarshGuruJi",
                color = NeonCyan,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
