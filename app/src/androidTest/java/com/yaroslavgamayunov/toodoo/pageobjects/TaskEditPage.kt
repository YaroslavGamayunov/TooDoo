package com.yaroslavgamayunov.toodoo.pageobjects

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import com.yaroslavgamayunov.toodoo.R

object TaskEditPage {
    fun typeTaskText(text: String) {
        onView(ViewMatchers.withId(R.id.taskDescriptionEditText))
            .perform(click()).perform(ViewActions.typeText(text))
    }

    fun saveChanges() {
        onView(ViewMatchers.withId(R.id.actionSave)).perform(click())
    }
}