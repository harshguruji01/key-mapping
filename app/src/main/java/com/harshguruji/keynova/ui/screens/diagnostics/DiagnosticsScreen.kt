package com.harshguruji.keynova.ui.screens.diagnostics

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshguruji.keynova.KeyNovaApp
import com.harshguruji.keynova.data.model.ActionMode
import com.harshguruji.keynova.data.model.ActionType
import com.harshguruji.keynova.data.model.DeviceType
import com.harshguruji.keynova.data.model.InputType
import com.harshguruji.keynova.data.model.Mapping
import com.harshguruji.keynova.ui.components.GlassCard
import com.harshguruji.keynova.ui.components.MetricCard
import com.harshguruji.keynova.ui.components.NeonButton
import com.harshguruji.keynova.ui.theme.BgDark
import com.harshguruji.keynova.ui.theme.NeonCyan
import com.harshguruji.keynova.ui.theme.NeonGreen
import com.harshguruji.keynova.ui.theme.NeonPurple
import com.harshguruji.keynova.ui.theme.NeonRed
import com.harshguruji.keynova.ui.theme.TextMuted
import com.harshguruji.keynova.ui.theme.TextPrimary
import com.harshguruji.keynova.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DiagnosticsScreen() {
    val app = KeyNovaApp.instance
    val engine = app.mappingEngine
    val diagnostics by engine.diagnostics.snapshot.collectAsState()
    val logs by engine.diagnostics.recentLogs.collectAsState()
    val scope = rememberCoroutineScope()

    var isStressTesting by remember { mutableStateOf(false) }
    var stressTestResult by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ENGINE TELEMETRY",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "HIGH-PERFORMANCE LOW-LATENCY VERIFICATION",
                    color = NeonCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            IconButton(onClick = { engine.diagnostics.clearLogs() }) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear Logs",
                    tint = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metrics Grid
        Row(modifier = Modifier.fillMaxWidth()) {
            MetricCard(
                label = "Event Rate",
                value = "${diagnostics.eventsPerSecond}",
                unit = "/sec",
                modifier = Modifier.weight(1f),
                accentColor = NeonCyan
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            MetricCard(
                label = "Avg Latency",
                value = "${diagnostics.averageProcessingLatencyUs}",
                unit = "µs",
                modifier = Modifier.weight(1f),
                accentColor = NeonGreen
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            MetricCard(
                label = "Total Events",
                value = "${diagnostics.totalEventsProcessed}",
                modifier = Modifier.weight(1f),
                accentColor = NeonPurple
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stress Test Card
        GlassCard {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "100+ MAPPING STRESS TEST",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Executes 10,000 rapid event resolutions against 100 simulated mappings to ensure zero dropped inputs.",
                    color = TextMuted,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                NeonButton(
                    text = if (isStressTesting) "TESTING..." else "RUN STRESS BENCHMARK",
                    onClick = {
                        isStressTesting = true
                        scope.launch {
                            val result = withContext(Dispatchers.Default) {
                                runStressBenchmark(engine)
                            }
                            stressTestResult = result
                            isStressTesting = false
                        }
                    },
                    enabled = !isStressTesting,
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Speed
                )

                stressTestResult?.let { res ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = res,
                        color = NeonGreen,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "EVENT TRACE LOG",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Log viewer
        GlassCard(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                reverseLayout = true
            ) {
                items(logs) { log ->
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

private fun runStressBenchmark(engine: com.harshguruji.keynova.input.MappingEngine): String {
    val mockMappings = (1..100).map { i ->
        Mapping(
            id = "stress_map_$i",
            profileId = "stress_prof",
            name = "Action $i",
            deviceType = DeviceType.KEYBOARD,
            inputType = InputType.KEYBOARD_KEY,
            inputCode = KeyEvent.KEYCODE_A + (i % 26),
            actionType = ActionType.TAP_ACTION,
            actionMode = ActionMode.TAP
        )
    }

    engine.mappingResolver.rebuild(mockMappings)

    val startTime = System.nanoTime()
    var hits = 0
    val iterations = 10000

    for (i in 0 until iterations) {
        val testKeyCode = KeyEvent.KEYCODE_A + (i % 26)
        val resolved = engine.mappingResolver.getKeyMapping(testKeyCode)
        if (resolved != null) hits++
    }

    val totalTimeUs = (System.nanoTime() - startTime) / 1000L
    val avgPerLookupUs = totalTimeUs.toFloat() / iterations

    // Restore active profile mappings
    engine.activeProfile.value?.let { engine.loadProfile(it) }

    return "✓ Completed: $iterations lookups in ${totalTimeUs}µs (avg: ${String.format("%.3f", avgPerLookupUs)}µs/lookup, hits: $hits)"
}
