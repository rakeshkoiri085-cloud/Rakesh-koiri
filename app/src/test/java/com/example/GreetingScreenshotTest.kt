package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.repository.SuperAiPlan
import com.example.ui.components.AppScreen
import com.example.ui.components.SuperAiBottomNav
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.SuperAiTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun super_ai_home_screenshot() {
        composeTestRule.setContent {
            SuperAiTheme {
                HomeScreen(
                    currentPlan = SuperAiPlan.FREE,
                    recentConversations = emptyList(),
                    onNavigate = {},
                    onStartPromptInChat = {},
                    onSelectConversation = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/super_ai_home.png")
    }
}
