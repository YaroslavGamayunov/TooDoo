package com.yaroslavgamayunov.toodoo.util

import android.content.Context
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat


fun getColorFromAttrs(context: Context, @AttrRes attrId: Int): Int {
    val typedValue = TypedValue()
    val theme: Theme = context.theme
    theme.resolveAttribute(attrId, typedValue, true)
    return typedValue.data
}

fun getDrawable(context: Context, @DrawableRes drawableId: Int): Drawable? {
    return ResourcesCompat.getDrawable(context.resources, drawableId, context.theme)
}

fun getDimension(context: Context, @DimenRes dimenId: Int): Float {
    return context.resources.getDimension(dimenId)
}