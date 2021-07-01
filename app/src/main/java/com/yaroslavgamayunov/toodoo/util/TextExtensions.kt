package com.yaroslavgamayunov.toodoo.util

import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.text.color
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun CharSequence.getColoredText(color: Int): CharSequence {
    return SpannableStringBuilder().color(color) { append(this@getColoredText) }
}

fun Instant.formatDate(showTime: Boolean): String {
    // TODO: Consider time zones
    val pattern = if (showTime) "EEE, d MMM yyyy HH:mm" else "EEE, d MMM yyyy"
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
        .withZone(ZoneId.from(ZoneOffset.UTC))
    //.withZone(ZoneId.systemDefault())
    return formatter.format(this)
}