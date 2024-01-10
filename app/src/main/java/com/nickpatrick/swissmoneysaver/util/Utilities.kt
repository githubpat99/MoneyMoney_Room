package com.nickpatrick.swissmoneysaver.util

import android.content.Context
import androidx.compose.ui.text.input.OffsetMapping
import com.nickpatrick.swissmoneysaver.R
import com.nickpatrick.swissmoneysaver.data.BudgetItem
import com.nickpatrick.swissmoneysaver.data.Item
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class Utilities {

    object MonthUtils {
        val monthlyText = mapOf(
            "01" to R.string.january,
            "02" to R.string.february,
            "03" to R.string.march,
            "04" to R.string.april,
            "05" to R.string.may,
            "06" to R.string.june,
            "07" to R.string.july,
            "08" to R.string.august,
            "09" to R.string.september,
            "10" to R.string.october,
            "11" to R.string.november,
            "12" to R.string.december
        )

        fun getMonthName(context: Context, month: String): String {
            val resId = monthlyText[month] ?: return ""
            return context.getString(resId)
        }
    }




    companion object {

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

        fun getStringDateFromTimestamp(timestamp: Long) : String {
            val localDateTime = Instant.ofEpochMilli(timestamp * 1000)
                .atZone(ZoneId.of("CET"))
                .toLocalDateTime()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
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

        fun writeJsonToFile(context: Context, json: String, fileName: String): Boolean {
            return try {
                val file = File(context.filesDir, fileName)
                val fileOutputStream = FileOutputStream(file)
                fileOutputStream.write(json.toByteArray())
                fileOutputStream.close()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun readJsonFromFile(context: Context, fileName: String): String {
            return try {
                val inputStream: InputStream = context.assets.open(fileName)
                val buffer = ByteArray(inputStream.available())
                inputStream.read(buffer)
                inputStream.close()
                String(buffer)
            } catch (e: IOException) {
                e.printStackTrace()
                ""
            }
        }

        fun readJsonFromAssets(context: Context, fileName: String): String {
            return context.assets.open(fileName).bufferedReader().use { it.readText() }
        }

        fun calculateApproxEndSaldo(
            approxStartSaldo: Double,
            budgetItems: List<BudgetItem>,
            year: String,
        ): Double {

            val yearInt = year.toInt()

            val endDate = LocalDate.of(yearInt, 12, 31)
            val endOfDayTs = endDate.atTime(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC)

            val startDate = LocalDate.of(yearInt, 1, 1)
            val startOfDayTs = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC)

            var totalAmount = approxStartSaldo
            var newItemList = mutableListOf<Item>()
//        var newItem: Item = Item(0, 0, "", "", 0, 0.0, 0.0, false)

            for (item in budgetItems) {

                var itemAmount = 0.0
                var date = item.timestamp
                while (date <= endOfDayTs) {
                    if (date >= startOfDayTs) {
                        if (item.debit) {
                            itemAmount += item.amount
                        } else {
                            itemAmount -= item.amount
                        }
                        val newItem = Item(0, date, "", item.name, 0, item.amount, 0.0, item.debit)

                        println ("Utilities - newItem: ${newItem.description} - ${newItem.amount}")

                        newItemList.add(newItem)


                    }
                    date = when (item.type) {
                        12 -> LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC).plusMonths(1)
                            .toEpochSecond(ZoneOffset.UTC)

                        6 -> LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC).plusMonths(2)
                            .toEpochSecond(ZoneOffset.UTC)

                        4 -> LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC).plusMonths(3)
                            .toEpochSecond(ZoneOffset.UTC)

                        3 -> LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC).plusMonths(4)
                            .toEpochSecond(ZoneOffset.UTC)

                        2 -> LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC).plusMonths(6)
                            .toEpochSecond(ZoneOffset.UTC)

                        else -> LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC).plusMonths(12)
                            .toEpochSecond(ZoneOffset.UTC)
                    }
                }

                totalAmount += itemAmount
            }

            val df = DecimalFormat("#.##")
            val approxEndSaldo = df.format(totalAmount).replace(",",".")

            return approxEndSaldo.toDoubleOrNull() ?: 0.0
        }

        fun addYearToTimestamp(timestamp: Long, budgetYear: Int): Long {
            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC)
            val tsYear = dateTime.year
            val yearDiff = budgetYear - tsYear
            return dateTime.plusYears(yearDiff.toLong()).toEpochSecond(ZoneOffset.UTC)
        }

        fun getActualYear(): String {
            return LocalDateTime.now().year.toString()
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
