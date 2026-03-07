package com.example.financeapp

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.home.ExpenseTypeSelector
import com.example.financeapp.home.HomeScreen
import com.example.financeapp.home.HomeScreenViewModel
import com.example.financeapp.home.KeyboardScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceApp(appViewModel: AppViewModel) {

    val homeScreenViewModel: HomeScreenViewModel = viewModel()
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topBarLocations = listOf(
        "homeScreen",
        "settingsScreen"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = "homeScreen",
                // Pass the innerPadding to the screens or use it in the modifier
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
                enterTransition = {
                    fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                            scaleIn(
                                initialScale = 0.92f,
                                animationSpec = tween(220, delayMillis = 90)
                            )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(90))
                }
            ) {
                composable("homeScreen") {
                    HomeScreen(
                        navController = navController,
                        homeScreenViewModel = homeScreenViewModel,
                        appViewModel = appViewModel
                    )
                }
                composable("settingsScreen") {
                    SettingsScreen(
                        appViewModel = appViewModel,
                        navController = navController
                    )
                }
                composable("keyboardScreen") {
                    KeyboardScreen(
                        navController = navController,
                        appViewModel = appViewModel
                    )
                }
                composable("expenseTypeSelector") {
                    ExpenseTypeSelector(
                        navController = navController,
                        viewModel = appViewModel
                    )
                }
            }

            // Overlay the Floating Navigation Bar
            if (currentRoute in topBarLocations) {
                FloatingNavigationBar(
                    navController = navController,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
fun FloatingNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.95f),
        tonalElevation = 1000.dp,
        shadowElevation = 50.dp,
        border = BorderStroke(
            width = 0.25.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        ),
        modifier = modifier
            .padding(bottom = 30.dp)
            .height(72.dp)
            .width(190.dp),
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentRoute == "homeScreen")
                            MaterialTheme.colorScheme.primaryContainer else
                            Color.Transparent
                    )
                    .clickable(
                        onClick = {
                            if (currentRoute == "homeScreen") return@clickable
                            navController.navigate("homeScreen") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = if (currentRoute == "homeScreen") 
                        MaterialTheme.colorScheme.onPrimaryContainer else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountBalanceWallet,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentRoute == "settingsScreen")
                            MaterialTheme.colorScheme.primaryContainer else
                            Color.Transparent
                    )
                    .clickable(
                        onClick = {
                            if (currentRoute == "settingsScreen") return@clickable
                            navController.navigate("settingsScreen") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (currentRoute == "settingsScreen") 
                        MaterialTheme.colorScheme.onPrimaryContainer else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
