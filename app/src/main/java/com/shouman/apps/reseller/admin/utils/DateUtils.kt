package com.shouman.apps.reseller.admin.utils

import android.content.Context
import com.shouman.apps.reseller.admin.R
import com.shouman.apps.reseller.admin.common.getCurrentDateWithoutTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {

        fun getWeekDay(context: Context, dateMilliSecond: Long): String {


            if (getCurrentDateWithoutTime().time == dateMilliSecond)
                return context.getString(R.string.today)

            val date = Date(dateMilliSecond)
            val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault());
            return simpleDateFormat.format(date)
        }

        fun getMonthAndMonthDay(dateMilliSecond: Long?): String {
            dateMilliSecond?.let {
                val pattern = "dd MMMM"
                val date = Date(it)
                val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault());
                return simpleDateFormat.format(date)
            }
            return ""
        }

        fun getMYear(dateMilliSecond: Long?): String {
            dateMilliSecond?.let {
                val pattern = "YYYY"
                val date = Date(it)
                val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault());
                return simpleDateFormat.format(date)
            }
            return ""
        }

        fun getShortTime(dateMilliSecond: Long?):String {
            dateMilliSecond?.let {
                val date = Date(it)
                val simpleDateFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
                return simpleDateFormat.format(date)
            }
            return ""
        }
    }


}