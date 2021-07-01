package com.yaroslavgamayunov.toodoo.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat


fun Context.getColorFromAttrs(@AttrRes attrId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrId, typedValue, true)
    return typedValue.data
}

fun Context.getDrawableCompat(@DrawableRes drawableId: Int, color: Int? = null): Drawable? {
    val drawable = ResourcesCompat.getDrawable(resources, drawableId, theme)
    color?.let {
        drawable?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            color,
            BlendModeCompat.SRC_ATOP
        )
    }
    return drawable
}

fun Context.getColorStateListCompat(@ColorRes stateListId: Int): ColorStateList? {
    return ResourcesCompat.getColorStateList(resources, stateListId, theme)
}

fun Context.getDimension(@DimenRes dimenId: Int): Float {
    return resources.getDimension(dimenId)
}