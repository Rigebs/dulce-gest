package com.rige.dulcegest.core.utils

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

fun String.toFriendlyDate(): String {
    return try {
        val parts = this.split("T", " ")
        if (parts.isNotEmpty()) parts[0] else this
    } catch (e: Exception) {
        this
    }
}

fun String.toFriendlyDateTime(): String {
    return try {
        val ldt = LocalDateTime.parse(this)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        ldt.format(formatter)
    } catch (e: Exception) {
        this
    }
}
