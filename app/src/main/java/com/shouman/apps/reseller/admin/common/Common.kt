package com.shouman.apps.reseller.admin.common

import java.util.*

fun getCurrentDateWithoutTime(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return calendar.time
}