package com.rige.dulcegest.core.utils

import android.content.Context

fun Float.toPx(context: Context): Float {
    return this * context.resources.displayMetrics.density
}
