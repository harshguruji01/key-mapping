package com.harshguruji.keynova.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshguruji.keynova.KeyNovaApp
import com.harshguruji.keynova.data.model.DeviceType
import com.harshguruji.keynova.ui.components.GlassCard
import com.harshguruji.keynova.ui.components.MetricCard
import com.harshguruji.keynova.ui.components.NeonButton
import com.harshguruji.keynova.ui.components.StatusChip
import com.harshguruji.keynova.ui.theme.BgDark
import com.harshguruji.keynova.ui.theme.BorderDark
import com.harshguruji.keynova.ui.theme.CardDark
import com.harshguruji.keynova.ui.theme.NeonBlue
import com.harshguruji.keynova.ui.theme.NeonCyan
import com.harshguruji.keynova.ui.theme.NeonGreen
import com.harshguruji.keynova.ui.theme.NeonPurple
import com.harshguruji.keynova.ui.theme.NeonRed
import com.harshguruji.keynova.ui.theme.SurfaceDark
import com.harshguruji.keynova.ui.theme.TextMuted
import com.harshguruji.keynova.ui.theme.TextPrimary
import com.harshguruji.keynova.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    onNavigateToMapper: () -> Unit,
    onNavigateToProfiles: () -> Unit,
    onNavigateToDevices: () -> Unit,
    onNavigateToDiagnostics: () -> Unit
) {
    val app = KeyNovaApp.instance
    val engine = app.mappingEngine
    val isMasterActive by engine.isMasterActive.collectAsState()
    val activeProfile by engine.activeProfile.collectAsState()
    val devices by app.deviceManager.devices.collectAsState()
    val diagnostics by engine.diagnostics.snapshot.collectAsState()

    val hasKeyboard = devices.any { it.type == DeviceType.KEYBOARD }
    val hasMouse = devices.any { it.type == DeviceType.MOUSE }
    val hasGamepad = devices.any { it.type == DeviceType.GAMEPAD || it.type == DeviceType.JOYSTICK }

    val masterBorderColor by animateColorAsState(
        targetValue = if (isMasterActive) NeonCyan else NeonRed.copy(alpha = 0.5f),
        label = "masterBorder"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "KEYNOVA",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "BY HARSHGURUJI",
                    color = NeonCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            Row {
                IconButton(onClick = onNavigateToDiagnostics) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = "Diagnostics",
                        tint = NeonCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Master Switch Card
        GlassCard(
            borderColor = masterBorderColor,
            cornerRadius = 18.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isMasterActive) NeonGreen else NeonRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isMasterActive) "MAPPING ENGINE: ACTIVE" else "MAPPING ENGINE: DISABLED",
                            color = if (isMasterActive) TextPrimary else TextMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isMasterActive) "Events routed to active profile" else "Hardware inputs pass-through directly",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Switch(
                    checked = isMasterActive,
                    onCheckedChange = { engine.setMasterActive(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BgDark,
                        checkedTrackColor = NeonCyan,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = SurfaceDark
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Connected Hardware Status Card
        GlassCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HARDWARE STATUS",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    IconButton(
                        onClick = { app.deviceManager.refreshDevices() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatusChip(text = "Keyboard", isActive = hasKeyboard)
                    StatusChip(text = "Mouse", isActive = hasMouse)
                    StatusChip(text = "Gamepad", isActive = hasGamepad)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${devices.size} total input devices detected",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Active Profile Card
        GlassCard(borderColor = NeonPurple.copy(alpha = 0.4f)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE PROFILE",
                        color = NeonPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${activeProfile?.mappings?.size ?: 0} mappings",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = activeProfile?.name ?: "No Profile Selected",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = activeProfile?.description ?: "Select or create a profile to map controls",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    NeonButton(
                        text = "OPEN MAPPER",
                        onClick = onNavigateToMapper,
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Gamepad
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    NeonButton(
                        text = "SWITCH",
                        onClick = onNavigateToProfiles,
                        modifier = Modifier.weight(1f),
                        isPrimary = false,
                        icon = Icons.Default.SwapHoriz
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Real-Time Engine Metrics
        Text(
            text = "ENGINE TELEMETRY",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            MetricCard(
                label = "Throughput",
                value = "${diagnostics.eventsPerSecond}",
                unit = "events/s",
                modifier = Modifier.weight(1f),
                accentColor = NeonCyan
            )
            Spacer(modifier = Modifier.width(10.dp))
            MetricCard(
                label = "Latency",
                value = "${diagnostics.averageProcessingLatencyUs}",
                unit = "µs",
                modifier = Modifier.weight(1f),
                accentColor = NeonGreen
            )
            Spacer(modifier = Modifier.width(10.dp))
            MetricCard(
                label = "Active Maps",
                value = "${activeProfile?.mappings?.count { it.enabled } ?: 0}",
                modifier = Modifier.weight(1f),
                accentColor = NeonBlue
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
