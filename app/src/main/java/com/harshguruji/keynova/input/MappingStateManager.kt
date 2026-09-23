package com.harshguruji.keynova.input

import com.harshguruji.keynova.data.model.ActionMode
import com.harshguruji.keynova.data.model.Mapping
import com.harshguruji.keynova.data.model.MappingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

data class MappingExecutionEvent(
    val mapping: Mapping,
    val state: MappingState,
    val timestamp: Long = System.currentTimeMillis()
)

class MappingStateManager(private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) {

    private val activeStates = ConcurrentHashMap<String, MappingState>()
    private val holdJobs = ConcurrentHashMap<String, Job>()
    private val repeatJobs = ConcurrentHashMap<String, Job>()

    private val _events = MutableSharedFlow<MappingExecutionEvent>(extraBufferCapacity = 50)
    val events: SharedFlow<MappingExecutionEvent> = _events.asSharedFlow()

    private val _activeMappingIds = MutableStateFlow<Set<String>>(emptySet())
    val activeMappingIds: StateFlow<Set<String>> = _activeMappingIds.asStateFlow()

    fun onInputDown(mapping: Mapping) {
        val mappingId = mapping.id
        val currentState = activeStates[mappingId] ?: MappingState.IDLE

        when (mapping.actionMode) {
            ActionMode.TAP -> {
                updateState(mapping, MappingState.PRESSED)
                _events.tryEmit(MappingExecutionEvent(mapping, MappingState.PRESSED))
            }
            ActionMode.HOLD -> {
                updateState(mapping, MappingState.PRESSED)
                _events.tryEmit(MappingExecutionEvent(mapping, MappingState.PRESSED))

                holdJobs[mappingId]?.cancel()
                holdJobs[mappingId] = scope.launch {
                    delay(mapping.holdThresholdMs)
                    if (isActive && activeStates[mappingId] == MappingState.PRESSED) {
                        updateState(mapping, MappingState.HELD)
                        _events.tryEmit(MappingExecutionEvent(mapping, MappingState.HELD))
                    }
                }
            }
            ActionMode.TOGGLE -> {
                val newState = if (currentState == MappingState.TOGGLED_ON) {
                    MappingState.TOGGLED_OFF
                } else {
                    MappingState.TOGGLED_ON
                }
                updateState(mapping, newState)
                _events.tryEmit(MappingExecutionEvent(mapping, newState))
            }
            ActionMode.REPEAT -> {
                updateState(mapping, MappingState.PRESSED)
                _events.tryEmit(MappingExecutionEvent(mapping, MappingState.PRESSED))

                repeatJobs[mappingId]?.cancel()
                repeatJobs[mappingId] = scope.launch {
                    delay(mapping.holdThresholdMs)
                    while (isActive && activeStates[mappingId] == MappingState.PRESSED) {
                        _events.tryEmit(MappingExecutionEvent(mapping, MappingState.PRESSED))
                        delay(mapping.repeatIntervalMs.coerceAtLeast(30L))
                    }
                }
            }
        }
    }

    fun onInputUp(mapping: Mapping) {
        val mappingId = mapping.id
        holdJobs.remove(mappingId)?.cancel()
        repeatJobs.remove(mappingId)?.cancel()

        if (mapping.actionMode != ActionMode.TOGGLE) {
            updateState(mapping, MappingState.RELEASED)
            _events.tryEmit(MappingExecutionEvent(mapping, MappingState.RELEASED))
            activeStates.remove(mappingId)
            syncActiveIds()
        }
    }

    fun getState(mappingId: String): MappingState {
        return activeStates[mappingId] ?: MappingState.IDLE
    }

    fun resetAll() {
        holdJobs.values.forEach { it.cancel() }
        holdJobs.clear()
        repeatJobs.values.forEach { it.cancel() }
        repeatJobs.clear()
        activeStates.clear()
        syncActiveIds()
    }

    private fun updateState(mapping: Mapping, state: MappingState) {
        activeStates[mapping.id] = state
        syncActiveIds()
    }

    private fun syncActiveIds() {
        _activeMappingIds.value = activeStates.keys.toSet()
    }
}
