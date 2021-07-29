package com.yaroslavgamayunov.toodoo.util

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers

fun clickOnButton(@IdRes id: Int) {
    Espresso.onView(ViewMatchers.withId(id)).perform(ViewActions.click())
}