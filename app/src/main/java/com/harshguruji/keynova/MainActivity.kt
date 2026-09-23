package com.harshguruji.keynova

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.harshguruji.keynova.ui.navigation.BottomNavItems
import com.harshguruji.keynova.ui.navigation.KeyNovaBottomBar
import com.harshguruji.keynova.ui.navigation.Screen
import com.harshguruji.keynova.ui.screens.devices.DevicesScreen
import com.harshguruji.keynova.ui.screens.diagnostics.DiagnosticsScreen
import com.harshguruji.keynova.ui.screens.home.HomeScreen
import com.harshguruji.keynova.ui.screens.mapper.MapperScreen
import com.harshguruji.keynova.ui.screens.onboarding.OnboardingScreen
import com.harshguruji.keynova.ui.screens.profiles.ProfilesScreen
import com.harshguruji.keynova.ui.screens.settings.SettingsScreen
import com.harshguruji.keynova.ui.screens.splash.SplashScreen
import com.harshguruji.keynova.ui.theme.KeyNovaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val app by lazy { KeyNovaApp.instance }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KeyNovaTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Splash.route
                val scope = rememberCoroutineScope()

                val isOnboardingCompleted by app.settingsRepository.isOnboardingCompleted.collectAsState(initial = false)

                val showBottomBar = BottomNavItems.any { it.route == currentRoute }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            KeyNovaBottomBar(
                                currentRoute = currentRoute,
                                onNavigate = { targetRoute ->
                                    navController.navigate(targetRoute) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp(0f))
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Splash.route
                        ) {
                            composable(Screen.Splash.route) {
                                SplashScreen(
                                    isOnboardingCompleted = isOnboardingCompleted,
                                    onNavigateNext = { isFirstRun ->
                                        if (isFirstRun) {
                                            navController.navigate(Screen.Onboarding.route) {
                                                popUpTo(Screen.Splash.route) { inclusive = true }
                                            }
                                        } else {
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo(Screen.Splash.route) { inclusive = true }
                                            }
                                        }
                                    }
                                )
                            }

                            composable(Screen.Onboarding.route) {
                                OnboardingScreen(
                                    onFinish = {
                                        scope.launch {
                                            app.settingsRepository.setOnboardingCompleted(true)
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                                            }
                                        }
                                    }
                                )
                            }

                            composable(Screen.Home.route) {
                                HomeScreen(
                                    onNavigateToMapper = { navController.navigate(Screen.Mapper.route) },
                                    onNavigateToProfiles = { navController.navigate(Screen.Profiles.route) },
                                    onNavigateToDevices = { navController.navigate(Screen.Devices.route) },
                                    onNavigateToDiagnostics = { navController.navigate(Screen.Diagnostics.route) }
                                )
                            }

                            composable(Screen.Mapper.route) {
                                MapperScreen()
                            }

                            composable(Screen.Profiles.route) {
                                ProfilesScreen()
                            }

                            composable(Screen.Devices.route) {
                                DevicesScreen()
                            }

                            composable(Screen.Settings.route) {
                                SettingsScreen()
                            }

                            composable(Screen.Diagnostics.route) {
                                DiagnosticsScreen()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && app.mappingEngine.processKeyEvent(event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && app.mappingEngine.processKeyEvent(event)) {
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        if (event != null && app.mappingEngine.processGenericMotionEvent(event)) {
            return true
        }
        return super.onGenericMotionEvent(event)
    }
}
