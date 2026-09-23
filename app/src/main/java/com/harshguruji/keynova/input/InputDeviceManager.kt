package com.harshguruji.keynova.input

import android.content.Context
import android.hardware.input.InputManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.InputDevice
import com.harshguruji.keynova.data.model.DeviceInfo
import com.harshguruji.keynova.data.model.DeviceType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class InputDeviceManager(private val context: Context) : InputManager.InputDeviceListener {

    private val inputManager = context.getSystemService(Context.INPUT_SERVICE) as? InputManager
    private val _devices = MutableStateFlow<List<DeviceInfo>>(emptyList())
    val devices: StateFlow<List<DeviceInfo>> = _devices.asStateFlow()

    private val _deviceEvents = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val deviceEvents: SharedFlow<String> = _deviceEvents.asSharedFlow()

    private val handler = Handler(Looper.getMainLooper())

    fun startListening() {
        inputManager?.registerInputDeviceListener(this, handler)
        refreshDevices()
    }

    fun stopListening() {
        inputManager?.unregisterInputDeviceListener(this)
    }

    fun refreshDevices() {
        val deviceList = mutableListOf<DeviceInfo>()
        val deviceIds = InputDevice.getDeviceIds()

        for (id in deviceIds) {
            val device = InputDevice.getDevice(id) ?: continue
            // Skip purely internal virtual devices like virtual touchscreens unless no other devices exist
            if (device.isVirtual && device.sources and InputDevice.SOURCE_KEYBOARD == 0) continue

            val type = determineDeviceType(device)
            val info = DeviceInfo(
                id = device.id,
                name = device.name ?: "Unknown Device",
                vendorId = device.vendorId,
                productId = device.productId,
                descriptor = device.descriptor ?: "N/A",
                type = type,
                isConnected = true,
                sources = device.sources,
                keyboardType = device.keyboardType,
                hasVibrator = device.vibrator.hasVibrator()
            )
            deviceList.add(info)
        }

        _devices.value = deviceList
    }

    override fun onInputDeviceAdded(deviceId: Int) {
        val device = InputDevice.getDevice(deviceId)
        val name = device?.name ?: "Input Device"
        _deviceEvents.tryEmit("$name connected")
        refreshDevices()
    }

    override fun onInputDeviceRemoved(deviceId: Int) {
        _deviceEvents.tryEmit("Device disconnected")
        refreshDevices()
    }

    override fun onInputDeviceChanged(deviceId: Int) {
        refreshDevices()
    }

    private fun determineDeviceType(device: InputDevice): DeviceType {
        val sources = device.sources

        val isMouse = (sources and InputDevice.SOURCE_MOUSE == InputDevice.SOURCE_MOUSE) ||
                (sources and InputDevice.SOURCE_TRACKBALL == InputDevice.SOURCE_TRACKBALL)
        val isGamepad = (sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD)
        val isJoystick = (sources and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK)
        val isStylus = (sources and InputDevice.SOURCE_STYLUS == InputDevice.SOURCE_STYLUS)
        val isKeyboard = (sources and InputDevice.SOURCE_KEYBOARD == InputDevice.SOURCE_KEYBOARD) &&
                device.keyboardType != InputDevice.KEYBOARD_TYPE_NONE

        return when {
            isMouse -> DeviceType.MOUSE
            isGamepad -> DeviceType.GAMEPAD
            isJoystick -> DeviceType.JOYSTICK
            isStylus -> DeviceType.STYLUS
            isKeyboard -> DeviceType.KEYBOARD
            else -> DeviceType.OTHER
        }
    }
}
