package com.shouman.apps.reseller.admin.utils

import java.text.DateFormat
import java.text.NumberFormat
import java.util.*

class Formatter {
    companion object {

        fun formatToMillisecondDate(timeMillisecond: Long?): String {
            timeMillisecond?.let {
                val formatter = DateFormat.getDateTimeInstance(
                    DateFormat.MEDIUM,
                    DateFormat.SHORT
                )

                return formatter.format(Date(timeMillisecond))
            }
            return ""
        }

        fun formatNumberToCurrency(number: Long?): String {
            number?.let {
                val format: NumberFormat = NumberFormat.getCurrencyInstance()
                format.maximumFractionDigits = 0
                format.currency = Currency.getInstance(Locale.getDefault())

                return format.format(number)
            }
            return ""
        }
    }
}