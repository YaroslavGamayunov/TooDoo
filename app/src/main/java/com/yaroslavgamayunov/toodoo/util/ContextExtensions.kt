package com.yaroslavgamayunov.toodoo.util

import android.content.Context
import com.yaroslavgamayunov.toodoo.AppComponentProvider
import com.yaroslavgamayunov.toodoo.di.AppComponent


val Context.appComponent: AppComponent?
    get() = (this as? AppComponentProvider)?.appComponent