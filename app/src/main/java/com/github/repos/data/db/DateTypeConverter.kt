package com.github.repos.data.db

import android.icu.text.SimpleDateFormat
import androidx.room.TypeConverter
import java.util.Date
import java.util.Locale

class DateTypeConverter {
    @TypeConverter
    fun fromDate(date: Date?): String? {
        return date?.let { DATE_FORMAT.format(it) }
    }

    @TypeConverter
    fun toDate(dateString: String?): Date? {
        return dateString?.let { DATE_FORMAT.parse(it) }
    }

    companion object {
        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    }
}
