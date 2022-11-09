package com.digitalinclined.edugate.utils

import java.text.SimpleDateFormat
import java.util.*

class DateTimeFormatFetcher {

    // get time and date
    fun getDateTime(timestamp: Long): String {
        return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(timestamp)
    }

}