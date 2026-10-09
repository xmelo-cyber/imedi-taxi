package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.AppLanguage
import com.example.data.MockDatabase
import com.example.data.User
import com.example.data.UserRole
import com.example.ui.admin.AdminPanelScreen
import com.example.ui.auth.AuthScreen
import com.example.ui.common.ProfileAndSettingsScreen
import com.example.ui.common.RideHistoryScreen
import com.example.ui.driver.DriverDocumentsScreen
import com.example.ui.driver.DriverEarningsScreen
import com.example.ui.driver.DriverHomeScreen
import com.example.ui.passenger.PassengerHomeScreen
import com.example.ui.theme.TaxigoTheme

enum class AppScreen {
    AUTH,
    PASSENGER_HOME,
    DRIVER_HOME,
    DRIVER_EARNINGS,
    DRIVER_DOCUMENTS,
    ADMIN_PANEL,
    SETTINGS,
    HISTORY
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by MockDatabase.isDarkTheme.collectAsState()
            val currentLang by MockDatabase.appLanguage.collectAsState()
            val currentUser by MockDatabase.currentUser.collectAsState()

            TaxigoTheme(darkTheme = isDarkTheme) {
                TaxigoApp(
                    currentLang = currentLang,
                    currentUser = currentUser
                )
            }
        }
    }
}

@Composable
fun TaxigoApp(
    currentLang: AppLanguage,
    currentUser: User
) {
    var currentScreen by remember {
        mutableStateOf(
            if (currentUser.role == UserRole.DRIVER) AppScreen.DRIVER_HOME else AppScreen.PASSENGER_HOME
        )
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        when (currentScreen) {
            AppScreen.AUTH -> {
                AuthScreen(
                    currentLang = currentLang,
                    onLoginSuccess = { loggedInUser ->
                        currentScreen = when (loggedInUser.role) {
                            UserRole.DRIVER -> AppScreen.DRIVER_HOME
                            UserRole.ADMIN -> AppScreen.ADMIN_PANEL
                            UserRole.PASSENGER -> AppScreen.PASSENGER_HOME
                        }
                    },
                    modifier = screenModifier
                )
            }

            AppScreen.PASSENGER_HOME -> {
                PassengerHomeScreen(
                    currentLang = currentLang,
                    onOpenProfile = { currentScreen = AppScreen.SETTINGS },
                    onOpenHistory = { currentScreen = AppScreen.HISTORY },
                    modifier = screenModifier
                )
            }

            AppScreen.DRIVER_HOME -> {
                DriverHomeScreen(
                    currentLang = currentLang,
                    onOpenEarnings = { currentScreen = AppScreen.DRIVER_EARNINGS },
                    onOpenDocuments = { currentScreen = AppScreen.DRIVER_DOCUMENTS },
                    onOpenProfile = { currentScreen = AppScreen.SETTINGS },
                    modifier = screenModifier
                )
            }

            AppScreen.DRIVER_EARNINGS -> {
                BackHandler { currentScreen = AppScreen.DRIVER_HOME }
                DriverEarningsScreen(
                    currentLang = currentLang,
                    onBack = { currentScreen = AppScreen.DRIVER_HOME },
                    modifier = screenModifier
                )
            }

            AppScreen.DRIVER_DOCUMENTS -> {
                BackHandler { currentScreen = AppScreen.DRIVER_HOME }
                DriverDocumentsScreen(
                    currentLang = currentLang,
                    onBack = { currentScreen = AppScreen.DRIVER_HOME },
                    modifier = screenModifier
                )
            }

            AppScreen.ADMIN_PANEL -> {
                BackHandler {
                    currentScreen = if (currentUser.role == UserRole.DRIVER) AppScreen.DRIVER_HOME else AppScreen.PASSENGER_HOME
                }
                AdminPanelScreen(
                    currentLang = currentLang,
                    onBack = {
                        currentScreen = if (currentUser.role == UserRole.DRIVER) AppScreen.DRIVER_HOME else AppScreen.PASSENGER_HOME
                    },
                    modifier = screenModifier
                )
            }

            AppScreen.SETTINGS -> {
                BackHandler {
                    currentScreen = if (currentUser.role == UserRole.DRIVER) AppScreen.DRIVER_HOME else AppScreen.PASSENGER_HOME
                }
                ProfileAndSettingsScreen(
                    currentLang = currentLang,
                    onBack = {
                        currentScreen = if (currentUser.role == UserRole.DRIVER) AppScreen.DRIVER_HOME else AppScreen.PASSENGER_HOME
                    },
                    onOpenAdmin = { currentScreen = AppScreen.ADMIN_PANEL },
                    onLogout = { currentScreen = AppScreen.AUTH },
                    modifier = screenModifier
                )
            }

            AppScreen.HISTORY -> {
                BackHandler { currentScreen = AppScreen.PASSENGER_HOME }
                RideHistoryScreen(
                    currentLang = currentLang,
                    onBack = { currentScreen = AppScreen.PASSENGER_HOME },
                    modifier = screenModifier
                )
            }
        }
    }
}
