package com.yaroslavgamayunov.toodoo.pageobjects

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.yaroslavgamayunov.toodoo.R
import com.yaroslavgamayunov.toodoo.ui.mainpage.TaskAdapter
import com.yaroslavgamayunov.toodoo.viewmatchers.atPosition
import org.hamcrest.Matcher

object MainPage {
    fun performActionsOnTaskListItem(position: Int, actions: ViewAction) {
        val itemActions = RecyclerViewActions.actionOnItemAtPosition<TaskAdapter.TaskViewHolder>(
            position,
            actions)

        onView(withId(R.id.taskRecyclerView)).perform(itemActions)
    }

    fun checkListItem(position: Int, matcher: Matcher<View>) {
        onView(withId(R.id.taskRecyclerView)).check(matches(atPosition(position, matcher)))
    }

    fun clickOnButton(id: Int) {
        onView(withId(id)).perform(click())
    }

    const val addTaskButtonId = R.id.addTaskFab
}