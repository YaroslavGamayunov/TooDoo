package com.yaroslavgamayunov.toodoo.util

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun getColoredText(text: CharSequence, color: Int): CharSequence {
    return SpannableString(text).apply {
        setSpan(
            ForegroundColorSpan(
                color
            ),
            0, text.lastIndex + 1,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }
}

fun formatInstantSimple(instant: Instant, showTime: Boolean): String {
    val pattern = if (showTime) "EEE, d MMM yyyy HH:mm" else "EEE, d MMM yyyy"
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}