package com.example.moneymoney_room.util

import androidx.compose.ui.text.input.OffsetMapping
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Utilities {

    object MonthUtils {
        val monthlyText = mapOf(
            "01" to "Januar",
            "02" to "Februar",
            "03" to "März",
            "04" to "April",
            "05" to "Mai",
            "06" to "Juni",
            "07" to "Juli",
            "08" to "August",
            "09" to "September",
            "10" to "Oktober",
            "11" to "November",
            "12" to "Dezember"
        )

        fun getMonthName(month: String): String {
            return monthlyText[month] ?: ""
        }
    }




    companion object {
        fun dateFormat(s: String): DateFormat {
            val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
            return dateFormat
        }

        fun getNowAsLong(): Long {
            return Date().time
        }

        fun getCurrentTimeInMillis(): Long {
            val timeZone = TimeZone.getTimeZone("Europe/Paris") // CET is equivalent to Europe/Paris timezone
            val calendar = Calendar.getInstance(timeZone)
            return calendar.timeInMillis
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

        fun getStringDateFromTimestamp(timestamp: Long) : String {
            val localDateTime = Instant.ofEpochMilli(timestamp * 1000)
                .atZone(ZoneId.of("CET"))
                .toLocalDateTime()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yy")
            return localDateTime.format(formatter)
        }

        fun getKadenz(type: Int): String {
            val kadenz = mapOf(
                12 to "mtl.",
                6 to "2 mtl.",
                4 to "3 mtl.",
                3 to "4 mtl.",
                2 to "6 mtl.",
                1 to "einmal"
            )

            return kadenz[type] ?: ""
        }
        fun getFormattedStartAndEndDatesForYear(year: String): Pair<String, String> {

            val yearInt = year.toInt()
            val startDate = LocalDate.of(yearInt, 1, 1)
            val endDate = LocalDate.of(yearInt, 12, 31)

            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val formattedStartDate = startDate.format(formatter)
            val formattedEndDate = endDate.format(formatter)

            return formattedStartDate to formattedEndDate
        }

    }
}
class DecimalFormatter(
    symbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance()
) {

    private val thousandsSeparator = symbols.groupingSeparator
    private val decimalSeparator = symbols.decimalSeparator

    fun cleanup(input: String): String {

        if (input.matches("\\D".toRegex())) return ""
        if (input.matches("0+".toRegex())) return "0"

        val sb = StringBuilder()

        var hasDecimalSep = false

        for (char in input) {
            if (char.isDigit()) {
                sb.append(char)
                continue
            }
            if (char == decimalSeparator && !hasDecimalSep && sb.isNotEmpty()) {
                sb.append(char)
                hasDecimalSep = true
            }
        }

        return sb.toString()
    }
    fun formatForVisual(input: String): String {

        val split = input.split(decimalSeparator)

        val intPart = split[0]
            .reversed()
            .chunked(3)
            .joinToString(separator = thousandsSeparator.toString())
            .reversed()

        val fractionPart = split.getOrNull(1)

        return if (fractionPart == null) intPart else intPart + decimalSeparator + fractionPart
    }
}
private class FixedCursorOffsetMapping(
    private val contentLength: Int,
    private val formattedContentLength: Int,
) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int = formattedContentLength
    override fun transformedToOriginal(offset: Int): Int = contentLength
}
