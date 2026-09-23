package com.harshguruji.keynova.ui.screens.mapper

import android.view.KeyEvent
import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshguruji.keynova.KeyNovaApp
import com.harshguruji.keynova.data.model.ActionMode
import com.harshguruji.keynova.data.model.ActionType
import com.harshguruji.keynova.data.model.ControlType
import com.harshguruji.keynova.data.model.DeviceType
import com.harshguruji.keynova.data.model.InputType
import com.harshguruji.keynova.data.model.Mapping
import com.harshguruji.keynova.data.model.VirtualControl
import com.harshguruji.keynova.input.ConflictInfo
import com.harshguruji.keynova.ui.components.ConflictDialog
import com.harshguruji.keynova.ui.components.GlassCard
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
import com.harshguruji.keynova.utils.KeyCodeHelper
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapperScreen() {
    val app = KeyNovaApp.instance
    val engine = app.mappingEngine
    val activeProfile by engine.activeProfile.collectAsState()
    val scope = rememberCoroutineScope()

    var showGrid by remember { mutableStateOf(true) }
    var snapToGrid by remember { mutableStateOf(true) }
    val gridSizeDp = 24.dp

    // Working copy of controls and mappings for undo/redo
    val controls = remember { mutableStateListOf<VirtualControl>() }
    val mappings = remember { mutableStateListOf<Mapping>() }

    // Undo stack
    val undoStack = remember { mutableStateListOf<List<VirtualControl>>() }
    val redoStack = remember { mutableStateListOf<List<VirtualControl>>() }

    var selectedControlId by remember { mutableStateOf<String?>(null) }
    var showAddMappingSheet by remember { mutableStateOf(false) }
    var isCapturingInput by remember { mutableStateOf(false) }
    var pendingConflict by remember { mutableStateOf<ConflictInfo?>(null) }

    val activeMappingIds by engine.stateManager.activeMappingIds.collectAsState()

    // Initialize from active profile
    LaunchedEffect(activeProfile?.id) {
        activeProfile?.let { profile ->
            controls.clear()
            controls.addAll(profile.controls)
            mappings.clear()
            mappings.addAll(profile.mappings)
        }
    }

    fun pushUndo() {
        undoStack.add(controls.map { it.copy() })
        redoStack.clear()
        if (undoStack.size > 20) undoStack.removeAt(0)
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val prev = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(controls.map { it.copy() })
            controls.clear()
            controls.addAll(prev)
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(controls.map { it.copy() })
            controls.clear()
            controls.addAll(next)
        }
    }

    fun saveCurrentLayout() {
        activeProfile?.let { prof ->
            val updated = prof.copy(
                controls = controls.toList(),
                mappings = mappings.toList(),
                updatedAt = System.currentTimeMillis()
            )
            scope.launch {
                app.profileRepository.saveProfile(updated)
                engine.loadProfile(updated)
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        val density = LocalDensity.current
        val canvasWidthPx = constraints.maxWidth.toFloat()
        val canvasHeightPx = constraints.maxHeight.toFloat()

        // 1. Grid Background
        if (showGrid) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridStep = with(density) { gridSizeDp.toPx() }
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = BorderDark.copy(alpha = 0.35f),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += gridStep
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = BorderDark.copy(alpha = 0.35f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += gridStep
                }
            }
        }

        // 2. Render Virtual Controls
        controls.forEachIndexed { index, control ->
            val isSelected = control.id == selectedControlId

            // Check if this control is actively triggered by hardware input
            val isTriggered = mappings.any { m ->
                m.targetControlId == control.id && activeMappingIds.contains(m.id)
            }

            val glowColor by animateColorAsState(
                targetValue = when {
                    isTriggered -> NeonGreen
                    isSelected -> NeonCyan
                    else -> Color(android.graphics.Color.parseColor(control.styleColorHex)).copy(alpha = control.opacity)
                },
                animationSpec = tween(150),
                label = "controlGlow"
            )

            val posX = (control.posX * canvasWidthPx).roundToInt()
            val posY = (control.posY * canvasHeightPx).roundToInt()

            Box(
                modifier = Modifier
                    .offset { IntOffset(posX, posY) }
                    .size(control.width.dp, control.height.dp)
                    .clip(if (control.type == ControlType.JOYSTICK) CircleShape else RoundedCornerShape(14.dp))
                    .background(SurfaceDark.copy(alpha = control.opacity))
                    .border(
                        width = if (isSelected || isTriggered) 2.5.dp else 1.5.dp,
                        color = glowColor,
                        shape = if (control.type == ControlType.JOYSTICK) CircleShape else RoundedCornerShape(14.dp)
                    )
                    .pointerInput(control.id) {
                        detectDragGestures(
                            onDragStart = {
                                pushUndo()
                                selectedControlId = control.id
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                var newX = (control.posX * canvasWidthPx) + dragAmount.x
                                var newY = (control.posY * canvasHeightPx) + dragAmount.y

                                if (snapToGrid) {
                                    val stepPx = with(density) { gridSizeDp.toPx() }
                                    newX = (newX / stepPx).roundToInt() * stepPx
                                    newY = (newY / stepPx).roundToInt() * stepPx
                                }

                                val normX = (newX / canvasWidthPx).coerceIn(0f, 0.95f)
                                val normY = (newY / canvasHeightPx).coerceIn(0f, 0.95f)

                                controls[index] = control.copy(posX = normX, posY = normY)
                            }
                        )
                    }
                    .clickable { selectedControlId = control.id },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = control.assignedKeyLabel.ifEmpty { control.name },
                        color = glowColor,
                        fontSize = if (control.type == ControlType.JOYSTICK) 13.sp else 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    if (control.type == ControlType.JOYSTICK) {
                        Text(
                            text = "STICK",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // 3. Top Floating Toolbar
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, BorderDark, RoundedCornerShape(20.dp)),
            color = SurfaceDark.copy(alpha = 0.92f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { undo() }, enabled = undoStack.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Undo",
                        tint = if (undoStack.isNotEmpty()) NeonCyan else TextMuted
                    )
                }

                IconButton(onClick = { redo() }, enabled = redoStack.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Redo,
                        contentDescription = "Redo",
                        tint = if (redoStack.isNotEmpty()) NeonCyan else TextMuted
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(onClick = { snapToGrid = !snapToGrid }) {
                    Text(
                        text = if (snapToGrid) "SNAP" else "FREE",
                        color = if (snapToGrid) NeonCyan else TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(onClick = { showGrid = !showGrid }) {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = "Grid",
                        tint = if (showGrid) NeonCyan else TextMuted
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(onClick = { showAddMappingSheet = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Control",
                        tint = NeonGreen
                    )
                }

                IconButton(onClick = { saveCurrentLayout() }) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save Layout",
                        tint = NeonBlue
                    )
                }
            }
        }

        // 4. Bottom Selected Control Properties Panel
        val selectedControl = controls.find { it.id == selectedControlId }
        AnimatedVisibility(
            visible = selectedControl != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp, start = 16.dp, end = 16.dp)
        ) {
            selectedControl?.let { ctrl ->
                val ctrlIndex = controls.indexOfFirst { it.id == ctrl.id }
                val associatedMapping = mappings.find { it.targetControlId == ctrl.id }

                GlassCard(
                    borderColor = NeonCyan.copy(alpha = 0.5f),
                    cornerRadius = 20.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CONTROL: ${ctrl.name.uppercase()}",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Row {
                                IconButton(
                                    onClick = {
                                        pushUndo()
                                        controls.removeAt(ctrlIndex)
                                        mappings.removeAll { it.targetControlId == ctrl.id }
                                        selectedControlId = null
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = NeonRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Binding button
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BgDark)
                                    .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                                    .clickable {
                                        isCapturingInput = true
                                        engine.inputCaptureCallback = { inputType, code, modifiers ->
                                            val keyLabel = when (inputType) {
                                                InputType.KEYBOARD_KEY -> KeyCodeHelper.getKeyLabel(code)
                                                InputType.MOUSE_BUTTON -> KeyCodeHelper.getMouseButtonLabel(code)
                                                else -> "Key $code"
                                            }
                                            controls[ctrlIndex] = ctrl.copy(assignedKeyLabel = keyLabel)

                                            // Update or create mapping
                                            val newMapping = Mapping(
                                                id = associatedMapping?.id ?: ("map_" + UUID.randomUUID().toString().take(8)),
                                                profileId = activeProfile?.id ?: "",
                                                name = "${ctrl.name} Mapping",
                                                deviceType = if (inputType == InputType.MOUSE_BUTTON) DeviceType.MOUSE else DeviceType.KEYBOARD,
                                                inputType = inputType,
                                                inputCode = code,
                                                secondaryCodes = modifiers,
                                                actionType = ActionType.TAP_ACTION,
                                                actionMode = associatedMapping?.actionMode ?: ActionMode.TAP,
                                                targetControlId = ctrl.id
                                            )

                                            // Check conflict
                                            val conflict = engine.mappingResolver.checkConflict(newMapping, mappings)
                                            if (conflict != null) {
                                                pendingConflict = conflict
                                            } else {
                                                val existingIdx = mappings.indexOfFirst { it.targetControlId == ctrl.id }
                                                if (existingIdx >= 0) {
                                                    mappings[existingIdx] = newMapping
                                                } else {
                                                    mappings.add(newMapping)
                                                }
                                            }

                                            isCapturingInput = false
                                            engine.inputCaptureCallback = null
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isCapturingInput) "PRESS KEY..." else "KEY: ${ctrl.assignedKeyLabel.ifEmpty { "UNASSIGNED" }}",
                                    color = if (isCapturingInput) NeonGreen else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Size Slider
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "SIZE: ${ctrl.width.toInt()} DP",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Slider(
                                    value = ctrl.width,
                                    onValueChange = { newSize ->
                                        controls[ctrlIndex] = ctrl.copy(width = newSize, height = newSize)
                                    },
                                    valueRange = 40f..140f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonCyan,
                                        activeTrackColor = NeonCyan,
                                        inactiveTrackColor = SurfaceDark
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 5. Add Mapping Modal Bottom Sheet
    if (showAddMappingSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddMappingSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = CardDark,
            dragHandle = { BottomSheetDefaults.DragHandle(color = BorderDark) }
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                Text(
                    text = "ADD VIRTUAL CONTROL",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(16.dp))

                val controlTypes = listOf(
                    ControlType.BUTTON to "Action Button",
                    ControlType.JOYSTICK to "WASD Movement Joystick",
                    ControlType.FIRE_BUTTON to "Fire / Shoot Button",
                    ControlType.AIM_BUTTON to "Aim / ADS Sight",
                    ControlType.DPAD to "Directional D-Pad"
                )

                controlTypes.forEach { (type, label) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceDark)
                            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                            .clickable {
                                pushUndo()
                                val newId = "ctrl_" + UUID.randomUUID().toString().take(8)
                                val newCtrl = VirtualControl(
                                    id = newId,
                                    profileId = activeProfile?.id ?: "",
                                    name = label,
                                    type = type,
                                    posX = 0.5f,
                                    posY = 0.5f,
                                    width = if (type == ControlType.JOYSTICK) 120f else 64f,
                                    height = if (type == ControlType.JOYSTICK) 120f else 64f,
                                    styleColorHex = if (type == ControlType.FIRE_BUTTON) "#EF4444" else "#00F2FE"
                                )
                                controls.add(newCtrl)
                                selectedControlId = newId
                                showAddMappingSheet = false
                            }
                            .padding(14.dp)
                    ) {
                        Text(text = label, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // 6. Conflict Dialog
    pendingConflict?.let { conflict ->
        ConflictDialog(
            inputLabel = conflict.inputDescription,
            existingName = conflict.existingMapping.name,
            newName = conflict.newMapping.name,
            onReplace = {
                mappings.removeAll { it.id == conflict.existingMapping.id }
                mappings.add(conflict.newMapping)
                pendingConflict = null
            },
            onKeepBoth = {
                mappings.add(conflict.newMapping)
                pendingConflict = null
            },
            onDismiss = { pendingConflict = null }
        )
    }
}
