package com.harshguruji.keynova.input

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

data class DiagnosticsSnapshot(
    val eventsPerSecond: Int = 0,
    val averageProcessingLatencyUs: Long = 0L,
    val totalEventsProcessed: Long = 0L,
    val activeMappingsCount: Int = 0,
    val connectedDevicesCount: Int = 0,
    val lastInputType: String = "None"
)

class InputDiagnostics {

    private val eventCounter = AtomicInteger(0)
    private val totalCounter = AtomicLong(0L)
    private val totalLatencyUs = AtomicLong(0L)
    private var lastRateCalcTime = System.currentTimeMillis()

    private val _snapshot = MutableStateFlow(DiagnosticsSnapshot())
    val snapshot: StateFlow<DiagnosticsSnapshot> = _snapshot.asStateFlow()

    private val _recentLogs = MutableStateFlow<List<String>>(emptyList())
    val recentLogs: StateFlow<List<String>> = _recentLogs.asStateFlow()

    fun recordEvent(durationNanos: Long, inputDescription: String) {
        val count = eventCounter.incrementAndGet()
        totalCounter.incrementAndGet()
        val durationUs = durationNanos / 1000L
        totalLatencyUs.addAndGet(durationUs)

        val now = System.currentTimeMillis()
        val elapsed = now - lastRateCalcTime

        if (elapsed >= 1000L) {
            val rate = ((count * 1000L) / elapsed).toInt()
            val avgLatency = if (count > 0) totalLatencyUs.get() / count else 0L

            _snapshot.value = _snapshot.value.copy(
                eventsPerSecond = rate,
                averageProcessingLatencyUs = avgLatency,
                totalEventsProcessed = totalCounter.get(),
                lastInputType = inputDescription
            )

            eventCounter.set(0)
            totalLatencyUs.set(0L)
            lastRateCalcTime = now
        }
    }

    fun updateMetrics(activeMappings: Int, connectedDevices: Int) {
        _snapshot.value = _snapshot.value.copy(
            activeMappingsCount = activeMappings,
            connectedDevicesCount = connectedDevices
        )
    }

    fun logDiagnosticMessage(message: String) {
        val time = java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date())
        val newEntry = "[$time] $message"
        val updated = (_recentLogs.value + newEntry).takeLast(30)
        _recentLogs.value = updated
    }

    fun clearLogs() {
        _recentLogs.value = emptyList()
    }
}
