package com.yaroslavgamayunov.toodoo.util

import android.text.SpannableStringBuilder
import androidx.core.text.color
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun CharSequence.getColoredText(color: Int): CharSequence {
    return SpannableStringBuilder().color(color) { append(this@getColoredText) }
}

fun ZonedDateTime.simpleFormat(showTime: Boolean): String {
    val pattern = if (showTime) "EEE, d MMM yyyy HH:mm" else "EEE, d MMM yyyy"
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
    return formatter.format(this)
}