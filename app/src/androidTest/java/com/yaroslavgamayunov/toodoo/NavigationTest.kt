package com.yaroslavgamayunov.toodoo

import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yaroslavgamayunov.toodoo.pageobjects.MainPage
import com.yaroslavgamayunov.toodoo.pageobjects.TaskEditPage
import com.yaroslavgamayunov.toodoo.ui.MainActivity
import com.yaroslavgamayunov.toodoo.util.clickOnButton
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testNavigation_taskAddedToMainPage_whenExitedFromTaskEditPage() {
        val taskName = "SampleTask"
        clickOnButton(MainPage.addTaskButtonId)

        TaskEditPage.typeTaskText(taskName)
        TaskEditPage.saveChanges() // Going to main page

        MainPage.checkItemPresence(hasDescendant(withText(taskName)), isPresent = true)
    }
}