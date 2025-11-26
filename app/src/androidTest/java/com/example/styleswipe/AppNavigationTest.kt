package com.example.styleswipe

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testNavigationToAddScreen() {
        // 1. Изчакваме UI да зареди
        composeTestRule.waitForIdle()

        // 2. Проверяваме дали бутонът "+" е на екрана.
        // Използваме "Add Item", защото така кръстихме иконката в MainActivity.kt
        composeTestRule.onNodeWithContentDescription("Add Item").assertIsDisplayed()

        // 3. Цъкаме върху бутона "+"
        composeTestRule.onNodeWithContentDescription("Add Item").performClick()

        // 4. Изчакваме навигацията да приключи
        composeTestRule.waitForIdle()

        // 5. Проверяваме дали сме на втория екран
        // Търсим бутона "ЗАПАЗИ" (или част от текста му "ЗАПАЗИ ДРЕХАТА")
        composeTestRule.onNodeWithText("ЗАПАЗИ", substring = true).assertIsDisplayed()
    }
}