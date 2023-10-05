package com.example.moneymoney_room.util

import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class Utilities {



    companion object {
        fun dateFormat(s: String): DateFormat {
            val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
            return dateFormat
        }

        fun getNowAsLong(): Long {
            return Date().time
        }

        fun getCurrentDateTimeAsString(): String {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yy")
            return currentDateTime.format(formatter)
        }

        fun getTimestampAsDate(timestamp: Long?): String {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy")
            return dateFormat.format(timestamp)
        }

        fun getLongFromStringDate(stringDate: String): Long {
            return SimpleDateFormat("dd.MM.yyyy").parse(stringDate).time
        }

        fun isNumeric(toCheck: String): Boolean {
            val regex = "^\\s*(3[01]|[12][0-9]|0?[1-9])\\.(1[012]|0?[1-9])\\.((?:19|20)\\d{2})\\s*\$".toRegex()
            return toCheck.matches(regex)
        }

        fun getDateFromTimestamp(timestamp: Long) : LocalDateTime {
            return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }
        fun formatDoubleToString(doubleValue: Double): String {
            val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
            if (numberFormat is DecimalFormat) {
                numberFormat.applyPattern("#,##0.00")
            }
            return numberFormat.format(doubleValue)
        }
    }
}