package com.yaroslavgamayunov.toodoo.pageobjects

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.yaroslavgamayunov.toodoo.R
import com.yaroslavgamayunov.toodoo.ui.mainpage.TaskAdapter
import com.yaroslavgamayunov.toodoo.atPosition
import com.yaroslavgamayunov.toodoo.hasItem
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher

object MainPage {
    fun performActionsOnTaskListItem(position: Int, actions: ViewAction) {
        val itemActions = RecyclerViewActions.actionOnItemAtPosition<TaskAdapter.TaskViewHolder>(
            position,
            actions)

        onView(withId(R.id.taskRecyclerView)).perform(itemActions)
    }

    fun performActionsOnTaskListItem(itemText: String, actions: ViewAction) {
        val itemActions =
            RecyclerViewActions.actionOnItem<TaskAdapter.TaskViewHolder>(
                hasDescendant(withText(itemText)),
                actions)

        onView(withId(R.id.taskRecyclerView)).perform(itemActions)
    }

    fun checkListItem(position: Int, matcher: Matcher<View>) {
        val itemMatcher = atPosition(position, matcher)
        onView(withId(R.id.taskRecyclerView)).check(matches(itemMatcher))
    }

    fun checkListItem(itemText: String, matcher: Matcher<View>) {
        val itemMatcher = allOf(hasDescendant(withText(itemText)), matcher)
        onView(withId(R.id.taskRecyclerView)).check(matches(hasItem(itemMatcher)))
    }

    fun checkItemPresence(matcher: Matcher<View>, isPresent: Boolean) {
        val itemMatcher = hasItem(matcher)
        onView(withId(R.id.taskRecyclerView)).check(matches(
            if (isPresent) matcher else not(itemMatcher)
        ))
    }

    const val addTaskButtonId = R.id.addTaskFab
}