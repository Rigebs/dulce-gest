package com.rige.dulcegest.core.utils

import java.text.NumberFormat
import java.util.Locale

fun Double.toSoles(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
    return format.format(this)
}
