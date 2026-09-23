# KeyNova — Lightweight Android Key Mapping & Virtual Mouse

**KeyNova** is a professional, high-performance, lightweight input-mapping utility engineered for Android 11 through Android 16+ (API 30–36).

- **Brand / Developer:** HarshGuruJi
- **Target OS:** Android 11 to 16+
- **Architecture:** Clean Architecture & MVVM (Kotlin, Jetpack Compose, Material 3, Room, Coroutines, Android InputDevice APIs)

---

## ⚡ Core Features

### 1. Hardware Input Detection
- **Keyboards:** Physical USB and Bluetooth keyboard detection, real-time key matrix visualization, modifier tracking (`Shift`, `Ctrl`, `Alt`, `Meta`, `Caps Lock`, `Num Lock`).
- **Mice:** Pointer motion tracking, independent X/Y sensitivity curves, non-linear acceleration, scroll wheel calibration, and button detection (`Primary`, `Secondary`, `Tertiary / Middle`, `Back`, `Forward`).
- **Gamepads & Controllers:** Analog stick position visualizer, trigger meters, and face button detection.
- **Dynamic Device Listener:** Listens to `InputManager.InputDeviceListener` for hot-plugging with debounced notifications.

### 2. High-Performance Input Engine Subsystem
- **$O(1)$ Indexed Lookup Tables:** `MappingResolver` builds pre-indexed lookup maps on profile activation to guarantee sub-millisecond input resolution without CPU overhead.
- **Multi-Key Combinations:** Supports `Ctrl + Shift + S`, `Alt + Enter`, `Shift + Tab` with priority resolution (`Exact Combo > Multi-key > Single Key`).
- **Finite State Machine:** Supports `TAP`, `HOLD` (configurable 200–1000ms threshold), `TOGGLE` (press once ON, press again OFF), and `REPEAT` (safe coroutine-based intervals).
- **Master Mapping Switch:** Global enable/disable toggle that immediately suspends or resumes all event interception.

### 3. Visual Key Mapper
- **Draggable & Resizable Canvas:** Place WASD Joysticks, Fire Buttons, Aim/ADS buttons, and custom triggers freely over an interactive grid.
- **Interactive Auto-Capture Mode:** Tap "Press Key / Mouse Button" to capture physical inputs automatically without manually typing keycodes.
- **Conflict Resolver:** Automatically detects duplicate key assignments and offers `Replace`, `Keep Both`, or `Cancel`.
- **Undo / Redo Buffer:** Easily undo or redo positioning and property edits.
- **Live Physical Glow:** Mapped virtual controls glow neon green when physical keys are pressed during test/preview mode.

### 4. Profiles & App Associations
- **Preloaded Game Templates:**
  - *FPS Gaming Pro* (WASD Movement, Mouse Left=Fire, Mouse Right=Aim, Space=Jump, C=Crouch, R=Reload).
  - *MOBA / Battle Arena* (QWER Ability bar, 1234 item slots, Right-click navigation).
  - *Retro Emulator Gamepad* (D-Pad directional arrows, Action A/B keys).
- **Profile Management:** Duplicate, rename, delete, and associate profiles with specific installed app packages.
- **JSON Import & Export:** Export profiles to shareable `.json` files via Android Share Sheet, with schema validation.

### 5. In-Game Floating HUD & Accessibility
- **Floating Overlay HUD:** Draggable floating pill overlay (`SYSTEM_ALERT_WINDOW`) for in-game toggle between mapping active/disabled and profile switching.
- **Accessibility Service:** `KeyNovaAccessibilityService` allows key event interception for games without compromising user privacy.

---

## 🔒 Security & Privacy Guarantees
KeyNova strictly complies with Android security policies:
- **Zero Keylogging:** Never stores raw typing history, text, or credentials.
- **Zero Sensitive Data Harvesting:** Bypasses password fields and never uploads data to external servers.
- **Offline-First:** All database operations and profile configurations are stored locally on-device.

---

## 🛠️ Building & Running

### Requirements
- **JDK:** Java 17 or Java 21 (`JAVA_HOME` configured)
- **Android SDK:** Platforms 30–36 (Build Tools 35.0.0 / 36.0.0)
- **Gradle:** 8.14 (bundled wrapper script)

### Build Commands
```bash
# Run unit tests
.\gradlew.bat testDebugUnitTest

# Build debug APK
.\gradlew.bat assembleDebug

# Build optimized release APK
.\gradlew.bat assembleRelease
```
