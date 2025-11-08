package com.rige.dulcegest.core.utils

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

val APP_ZONE_ID: ZoneId = ZoneId.systemDefault()

val ISO_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun LocalDate.toStartOfDayMillis(): Long {
    return this.atStartOfDay(APP_ZONE_ID).toInstant().toEpochMilli()
}

fun LocalDate.toEndOfDayMillis(): Long {
    return this.atTime(LocalTime.MAX).atZone(APP_ZONE_ID).toInstant().toEpochMilli()
}