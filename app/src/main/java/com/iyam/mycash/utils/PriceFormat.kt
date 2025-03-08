package com.iyam.mycash.utils

import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale
import kotlin.math.roundToInt

fun Float.toCurrencyFormat(): String {
    val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    format.currency = Currency.getInstance("IDR")

    return format.format(this.roundToInt()).replace("IDR", "Rp ")
}

fun editTextFormatPrice(price: String): String {
    return try {
        val parsed = price.replace("[^\\d]".toRegex(), "").toDouble()
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
        "Rp " + format.format(parsed)
    } catch (e: Exception) {
        "Rp 0"
    }
}

fun TextInputEditText.doneEditing(doneBlock: () -> Unit) {
    this.setOnEditorActionListener { _, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
            actionId == EditorInfo.IME_ACTION_DONE ||
            event != null &&
            event.action == KeyEvent.ACTION_DOWN &&
            event.keyCode == KeyEvent.KEYCODE_ENTER
        ) {
            if (event == null || !event.isShiftPressed) {
                doneBlock.invoke()
                return@setOnEditorActionListener true
            }
        }
        return@setOnEditorActionListener true
    }
}
