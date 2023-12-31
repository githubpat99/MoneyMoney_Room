package com.nickpatrick.swissmoneysaver.ui.home

import android.content.Context
import com.nickpatrick.swissmoneysaver.data.Item
import com.nickpatrick.swissmoneysaver.util.Utilities
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class ItemListGenerator() {

    fun generateItemDetailsList(fileName: String, appContext: Context): List<Item> {
        // Initialize the itemDetailList based on userId or any other logic
        if (fileName.isNotBlank()) {
            // Generate the list here based on userId
            return generateItemDetails(fileName, appContext)
        } else {
            // Handle the case where userId is empty
            return emptyList()
        }
    }

    fun generateItemDetails(fileName: String, appContext: Context): List<Item> {

        return AccountFromCsv(fileName, appContext)
    }

    fun generateItemDetailsFromUrl(csvData: List<List<String>>): List<Item> {

        return AccountFromUrl(csvData)
    }

    fun PatAccount(userId: String): List<Item> {

        val id = 0
        // timestamps for Jan - Dec
        val timestamps: List<Long> = listOf(
            1674724269900,
            1677402669900,
            1679821869900,
            1682500269900,
            1685092269900,
            1687770669900,
            1690362669900,
            1693041069900,
            1695719469900,
            1698311469900,
            1700989869900,
            1703581869900
        )
        val itemList: MutableList<Item> = mutableListOf()

        timestamps.forEach {
            val iMiete = Item(id, it, "Hypothek", "monatlich", 1, -820.00, 850.00, true)
            itemList.add(iMiete)
            val iHaushalt = Item(id, it, "Haushalt", "Einkauf", 1, -1500.00, 777.00, true)
            itemList.add(iHaushalt)
            val iKrankenkasse = Item(id, it, "Krankenkasse", "Prämie", 1, -735.30, 850.00, true)
            itemList.add(iKrankenkasse)
            val iAxa = Item(id, it, "AXA Vers.", "Prämie", 1, -349.50, 850.00, true)
            itemList.add(iAxa)
            val iKreditkare = Item(id, it, "Kreditkarte", "Viseca", 1, -1000.00, 850.00, true)
            itemList.add(iKreditkare)
            val iDiv = Item(id, it, "Verschiedenes", "Diverse Ausgaben", 1, -1000.00, 850.00, true)
            itemList.add(iDiv)
            val iEinkommen = Item(id, it, "Lohn", "Abraxas AG", 1, 12000.00, 777.00, true)
            itemList.add(iEinkommen)

        }
        return itemList
    }

    fun TestAccount(userId: String): List<Item> {

        val id = 0
        // timestamps for Jan - Dec
        val timestamps: List<Long> = listOf(
            1674724269900,
            1677402669900,
            1679821869900,
            1682500269900,
            1685092269900,
            1687770669900,
            1690362669900,
            1693041069900,
            1695719469900,
            1698311469900,
            1700989869900,
            1703581869900
        )
        val itemList: MutableList<Item> = mutableListOf()

        timestamps.forEach {
            val iMiete = Item(id, it, "Miete", "Monatsmiete", 1, -1250.00, 850.00, true)
            itemList.add(iMiete)
            val iHaushalt = Item(id, it, "Haushalt", "Einkauf", 1, -400.00, 777.00, true)
            itemList.add(iHaushalt)
            val iKrankenkasse = Item(id, it, "Krankenkasse", "Prämie", 1, -750.00, 850.00, true)
            itemList.add(iKrankenkasse)
            val iEinkommen = Item(id, it, "Lohn", "Holzbau AG", 1, 4500.00, 777.00, true)
            itemList.add(iEinkommen)

        }
        return itemList
    }

    fun AccountFromDataString(csvData: String): List<Item> {

        val lines = csvData.split("\n")
        val itemList: MutableList<Item> = mutableListOf()

        println("ItemListGenerator - lines: ${lines.size}")

        try {

            // Start the loop from index 1 to skip the first line
            for (i in 1 until lines.size) {
                val line = lines[i]
                val columns = line!!.split(",") // Assuming CSV columns are separated by a comma

                if (columns.size >= 7) {

//              Id, Timestamp, Name, Beschreibung, Typ, Amount, Balance, Debit

                    val id: Int = columns[0].toInt()
                    val ts: Long = Utilities.getLongFromStringDate(columns[1].trim())
                    var name = columns[2].trim().toString()
                    if (name.length > 9)
                        name = name.substring(0, 9)
                    var beschreibung = columns[3].trim()
                    if (beschreibung.length > 20)
                        beschreibung = beschreibung.substring(0, 20)
                    val type = columns[4].toInt()
                    val amountString = columns[5].trim()
                    val amount = if (amountString.isNotEmpty()) amountString.toDouble() else 0.0
                    val balance = columns[6].toDouble()
                    val debit: Boolean = columns[7].toBoolean()
                    val item = Item(id, ts, name, beschreibung, type, amount, balance, debit)

                    itemList.add(item)
                }
            }
        } catch (e: IOException) {
            // Handle file access or parsing errors
            e.printStackTrace()
            println(e.message)
        }

        println("ItemListGenerator - itemList: ${itemList.size}")

        return itemList
    }

    fun AccountFromJson(jsonArray: JSONArray): List<Item> {

        var itemList: MutableList<Item> = mutableListOf()

        var i = 0

        while (i < jsonArray.length()-1) {
            i++

            var nnId: Int = 0
            var nnTs: Long = 0
            var nnName = ""
            var nnBeschreibung = ""
            var nnType: Int = 0
            var nnAmount: Double = 0.0
            var nnBalance: Double = 0.0
            var nnDebit: Boolean = true

            val obj = jsonArray.getJSONArray(i)
            val id: Int? = obj.get(0) as? Int
            if (id != null) {
                nnId = id!!
            }
            val ts: String = obj.get(1).toString()
            if (ts != null) {
                nnTs = Utilities.getLongFromStringDate(ts)
            }
            val name: String? = obj.get(2) as? String
            if (name != null) {
                nnName = name!!
            }
            val beschreibung: String? = obj.get(3) as? String
            if (beschreibung != null) {
                nnBeschreibung = beschreibung!!
            }
            val type: Int? = obj.get(4) as? Int
            if (type != null) {
                nnType = type!!
            }
            val amount: Double? = try {
                obj.get(5).toString().replace(",", ".").toDouble()
            } catch (e: NumberFormatException) {
                null
            }
            if (amount != null) {
                nnAmount = amount!!
            }
            val balance: Double? = try {
                obj.get(5).toString().replace(",", ".").toDouble()
            } catch (e: NumberFormatException) {
                null
            }
            if (balance != null) {
                nnBalance = balance!!
            }

            val debit: Boolean? = obj.get(7) as? Boolean
            if (debit != null) {
                nnDebit = debit!!
            }
            val item =
                Item(nnId, nnTs, nnName, nnBeschreibung, nnType, nnAmount, nnBalance, nnDebit)

            itemList.add(item)
        }
        return itemList
    }

    fun AccountFromCsv(fileName: String, appContext: Context): List<Item> {

        val itemList: MutableList<Item> = mutableListOf()

        try {
            val inputStream = appContext.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))

            var line: String?

            // skip Header
            reader.readLine()

            while (reader.readLine().also { line = it } != null) {

                val columns = line!!.split(",") // Assuming CSV columns are separated by a comma

                if (columns.size >= 2) {

//              Id, Timestamp, Name, Beschreibung, Typ, Amount, Balance, Debit

                    val id: Int = columns[0].toInt()
                    val ts: Long = Utilities.getLongFromStringDate(columns[1].trim())
                    var name = columns[2].trim().toString()
                    if (name.length > 9)
                        name = name.substring(0, 9)
                    var beschreibung = columns[3].trim()
                    if (beschreibung.length > 20)
                        beschreibung = beschreibung.substring(0, 20)
                    val type = columns[4].toInt()
                    val amount = columns[5].toDouble()
                    val balance = columns[6].toDouble()
                    val debit: Boolean = columns[7].toBoolean()
                    val item = Item(id, ts, beschreibung, name, type, amount, balance, debit)

                    itemList.add(item)
                }
            }
            inputStream.close()
        } catch (e: IOException) {
            // Handle file access or parsing errors
            e.printStackTrace()
            println(e.message)
        }

        return itemList
    }

    fun AccountFromUrl(table: List<List<String>>): List<Item> {

        val itemList: MutableList<Item> = mutableListOf()

        try {
            for (i in 1 until table.size) {
                val row = table[i]
                val id: Int = row[0].toInt()
                val ts: Long = Utilities.getLongFromStringDate(row[1].trim())
                var name = row[2].trim().toString()
                if (name.length > 9)
                    name = name.substring(0, 9)
                var beschreibung = row[3].trim()
                if (beschreibung.length > 20)
                    beschreibung = beschreibung.substring(0, 20)
                val type = row[4].toInt()
                var amount: Number = 0
                var balance: Number = 0
                if (row[5].isNotBlank()) {
                    amount = row[5].replace(',', '.').toDouble()
                }
                if (row[6].isNotBlank()) {
                    balance = row[6].replace(',', '.').toDouble()
                }

                val debit: Boolean = row[7].toBoolean()
                val item = Item(
                    id,
                    ts,
                    name,
                    beschreibung,
                    type,
                    amount.toDouble(),
                    balance.toDouble(),
                    debit
                )

                itemList.add(item)

            }
//
        } catch (e: IOException) {
            throw e
        }

        return itemList
    }
}

