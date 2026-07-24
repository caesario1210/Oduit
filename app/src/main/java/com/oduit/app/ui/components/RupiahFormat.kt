package com.oduit.app.ui.components

import java.text.NumberFormat
import java.util.Locale

fun Double.toRupiah(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    return format.format(this)
}

fun Long.toRupiah(): String {
    return this.toDouble().toRupiah()
}
