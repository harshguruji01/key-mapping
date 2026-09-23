package com.harshguruji.keynova.ui.screens.devices

import android.view.KeyEvent
import android.view.MotionEvent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshguruji.keynova.KeyNovaApp
import com.harshguruji.keynova.data.model.DeviceInfo
import com.harshguruji.keynova.data.model.DeviceType
import com.harshguruji.keynova.ui.components.GlassCard
import com.harshguruji.keynova.ui.components.StatusChip
import com.harshguruji.keynova.ui.theme.BgDark
import com.harshguruji.keynova.ui.theme.BorderDark
import com.harshguruji.keynova.ui.theme.CardDark
import com.harshguruji.keynova.ui.theme.NeonBlue
import com.harshguruji.keynova.ui.theme.NeonCyan
import com.harshguruji.keynova.ui.theme.NeonGreen
import com.harshguruji.keynova.ui.theme.NeonRed
import com.harshguruji.keynova.ui.theme.SurfaceDark
import com.harshguruji.keynova.ui.theme.TextMuted
import com.harshguruji.keynova.ui.theme.TextPrimary
import com.harshguruji.keynova.ui.theme.TextSecondary
import com.harshguruji.keynova.utils.KeyCodeHelper

@Composable
fun DevicesScreen() {
    val app = KeyNovaApp.instance
    val devices by app.deviceManager.devices.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Connected Devices", "Keyboard Test", "Mouse Test")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "HARDWARE & INPUT",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "REAL-TIME DEVICE TESTING & MONITORING",
                    color = NeonCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            IconButton(onClick = { app.deviceManager.refreshDevices() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = NeonCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceDark,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonCyan
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> DeviceListView(devices)
            1 -> KeyboardTesterView()
            2 -> MouseTesterView()
        }
    }
}

@Composable
private fun DeviceListView(devices: List<DeviceInfo>) {
    if (devices.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "No External Input Devices Detected", color = TextSecondary, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Connect a physical USB/Bluetooth keyboard or mouse",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(devices) { device ->
                GlassCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(SurfaceDark),
                            contentAlignment = Alignment.Center
                        ) {
                            val icon = when (device.type) {
                                DeviceType.KEYBOARD -> Icons.Default.Keyboard
                                DeviceType.MOUSE -> Icons.Default.Mouse
                                DeviceType.GAMEPAD, DeviceType.JOYSTICK -> Icons.Default.Gamepad
                                else -> Icons.Default.Keyboard
                            }
                            Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = device.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Type: ${device.type.displayName} | ID: ${device.id}",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        StatusChip(text = "Online", isActive = device.isConnected)
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyboardTesterView() {
    val engine = KeyNovaApp.instance.mappingEngine
    val modifierState by engine.modifierManager.modifierState.collectAsState()
    val activeMappings by engine.stateManager.activeMappingIds.collectAsState()
    val recentLogs by engine.diagnostics.recentLogs.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Modifier Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ModifierBadge("SHIFT", modifierState.isShiftPressed)
            ModifierBadge("CTRL", modifierState.isCtrlPressed)
            ModifierBadge("ALT", modifierState.isAltPressed)
            ModifierBadge("META", modifierState.isMetaPressed)
            ModifierBadge("CAPS", modifierState.isCapsLockOn)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Key Matrix Preview
        GlassCard(cornerRadius = 14.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PRESS ANY PHYSICAL KEY TO TEST",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Common keys row
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    KeyTestChip("W", KeyEvent.KEYCODE_W)
                    KeyTestChip("A", KeyEvent.KEYCODE_A)
                    KeyTestChip("S", KeyEvent.KEYCODE_S)
                    KeyTestChip("D", KeyEvent.KEYCODE_D)
                    KeyTestChip("SPACE", KeyEvent.KEYCODE_SPACE)
                    KeyTestChip("ESC", KeyEvent.KEYCODE_ESCAPE)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "LIVE EVENT STREAM",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Live Log Box
        GlassCard(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                reverseLayout = true
            ) {
                items(recentLogs) { log ->
                    Text(
                        text = log,
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModifierBadge(label: String, isActive: Boolean) {
    val targetColor = if (isActive) NeonCyan else TextMuted
    val animatedBg by animateColorAsState(targetValue = if (isActive) CardDark else SurfaceDark, label = "modBg")

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(animatedBg)
            .border(1.dp, targetColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = targetColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun KeyTestChip(label: String, keyCode: Int) {
    val engine = KeyNovaApp.instance.mappingEngine
    val isMapped = engine.mappingResolver.getKeyMapping(keyCode) != null
    val isPressed = isMapped && engine.stateManager.activeMappingIds.collectAsState().value.isNotEmpty()

    val chipColor = if (isPressed) NeonGreen else if (isMapped) NeonCyan else TextMuted

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(1.dp, chipColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = chipColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun MouseTesterView() {
    val engine = KeyNovaApp.instance.mappingEngine
    val activeButtons by engine.mouseProcessor.activeButtons.collectAsState()

    val isLeftDown = (activeButtons and MotionEvent.BUTTON_PRIMARY) != 0
    val isRightDown = (activeButtons and MotionEvent.BUTTON_SECONDARY) != 0
    val isMiddleDown = (activeButtons and MotionEvent.BUTTON_TERTIARY) != 0

    Column(modifier = Modifier.fillMaxSize()) {
        GlassCard(cornerRadius = 16.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "VIRTUAL MOUSE DIAGNOSTICS",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Mouse buttons preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MouseButtonBadge("Left Click", isLeftDown)
                    MouseButtonBadge("Middle Wheel", isMiddleDown)
                    MouseButtonBadge("Right Click", isRightDown)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Move mouse or click buttons to verify pointer lock & tracking",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun MouseButtonBadge(label: String, isPressed: Boolean) {
    val targetColor = if (isPressed) NeonGreen else TextMuted
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isPressed) CardDark else SurfaceDark)
            .border(1.dp, targetColor.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = targetColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
