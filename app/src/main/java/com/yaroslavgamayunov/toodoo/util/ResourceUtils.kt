package com.yaroslavgamayunov.toodoo.util

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat


fun getColorFromAttrs(context: Context, @AttrRes attrId: Int): Int {
    val typedValue = TypedValue()
    val theme: Theme = context.theme
    theme.resolveAttribute(attrId, typedValue, true)
    return typedValue.data
}

fun getDrawable(context: Context, @DrawableRes drawableId: Int, color: Int? = null): Drawable? {
    val drawable = ResourcesCompat.getDrawable(context.resources, drawableId, context.theme)
    color?.let {
        drawable?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            color,
            BlendModeCompat.SRC_ATOP
        )
    }
    return drawable
}

fun getColorStateList(context: Context, @ColorRes statelistId: Int): ColorStateList? {
    return ResourcesCompat.getColorStateList(context.resources, statelistId, context.theme)
}

fun getDimension(context: Context, @DimenRes dimenId: Int): Float {
    return context.resources.getDimension(dimenId)
}