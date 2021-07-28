package com.yaroslavgamayunov.toodoo.viewmatchers

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

fun atPosition(position: Int, itemMatcher: Matcher<View>) =
    object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun matchesSafely(item: RecyclerView?): Boolean {
            val viewHolder: RecyclerView.ViewHolder =
                item?.findViewHolderForAdapterPosition(position) ?: return false
            return itemMatcher.matches(viewHolder.itemView)
        }

        override fun describeTo(description: Description?) {
            itemMatcher.describeTo(description)
        }
    }