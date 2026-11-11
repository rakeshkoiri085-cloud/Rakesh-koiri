package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SuperCyanAccent
import com.example.ui.theme.SuperIndigoPrimary

enum class AppScreen(val route: String, val title: String, val icon: ImageVector) {
    HOME("home", "Home", Icons.Default.Home),
    CHAT("chat", "AI Chat", Icons.Default.ChatBubble),
    CODE("code", "Coding", Icons.Default.Code),
    LEARN("learn", "Learn", Icons.Default.School),
    STUDY("study", "Study", Icons.Default.AutoAwesome),
    PROJECTS("projects", "Projects", Icons.Default.Folder),
    TOOLS("tools", "Tools", Icons.Default.Widgets),
    HISTORY("history", "History", Icons.Default.History),
    SETTINGS("settings", "Settings", Icons.Default.Settings)
}

val BottomNavScreens = listOf(
    AppScreen.HOME,
    AppScreen.CHAT,
    AppScreen.CODE,
    AppScreen.LEARN,
    AppScreen.PROJECTS,
    AppScreen.TOOLS
)

@Composable
fun SuperAiBottomNav(
    currentScreen: AppScreen,
    onSelectScreen: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = modifier.testTag("super_ai_bottom_nav")
    ) {
        BottomNavScreens.forEach { screen ->
            val selected = currentScreen == screen
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectScreen(screen) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SuperIndigoPrimary,
                    selectedTextColor = SuperIndigoPrimary,
                    indicatorColor = SuperIndigoPrimary.copy(alpha = 0.15f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_item_${screen.route}")
            )
        }
    }
}

@Composable
fun SuperAiNavRail(
    currentScreen: AppScreen,
    onSelectScreen: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxHeight()
            .testTag("super_ai_nav_rail")
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        AppScreen.values().forEach { screen ->
            val selected = currentScreen == screen
            NavigationRailItem(
                selected = selected,
                onClick = { onSelectScreen(screen) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title, fontSize = 10.sp) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = SuperIndigoPrimary,
                    selectedTextColor = SuperIndigoPrimary,
                    indicatorColor = SuperIndigoPrimary.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag("rail_item_${screen.route}")
            )
        }
    }
}
