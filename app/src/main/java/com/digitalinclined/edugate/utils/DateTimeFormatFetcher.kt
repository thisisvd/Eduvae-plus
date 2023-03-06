package com.digitalinclined.edugate.utils

import java.text.SimpleDateFormat
import java.util.*

class DateTimeFormatFetcher {

    // get time and date
    fun getDateTime(timestamp: Long): String {
        return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(timestamp)
    }

    // get time and date
    fun getDateWithIncludedTime(timestamp: Long): String {
        return SimpleDateFormat("MMM dd, yyyy hh:mm", Locale.getDefault()).format(timestamp)
    }
}