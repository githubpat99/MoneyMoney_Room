package com.nickpatrick.swissmoneysaver.util

import android.content.Context
import com.nickpatrick.swissmoneysaver.R

class ListEntries {
}

fun getEinnahmenList(context: Context): List<String> {
    return listOf(
        context.getString(R.string.einkommen),
        context.getString(R.string.boni)
    )
}

fun getAusgabenList(context: Context): List<String> {
    return listOf(
        context.getString(R.string.haushalt),
        context.getString(R.string.versicherung),
        context.getString(R.string.krankenkasse),
        context.getString(R.string.miete),
        context.getString(R.string.ausgang),
        context.getString(R.string.geschenke),
        context.getString(R.string.steuern),
        context.getString(R.string.nebenkosten),
        context.getString(R.string.ferien),
        context.getString(R.string.diverses)
    )
}