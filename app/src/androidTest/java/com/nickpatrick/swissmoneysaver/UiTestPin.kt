package com.nickpatrick.swissmoneysaver

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nickpatrick.swissmoneysaver.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UiTestPin {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testBudgetFormScreen() {
        composeRule.onNodeWithText("patrick").assertExists()
        composeRule.onNodeWithText("Budgetformular").performClick()
        composeRule.onNodeWithText("1.1.2023").assertExists()
        composeRule.onNodeWithText("Budget ready -> save & go...").assertExists()
    }
}
