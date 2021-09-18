package com.agelousis.payments.utils.helpers

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs

object CurrencyHelper {

    private val availableCurrencies by lazy {
        Currency.getAvailableCurrencies().toList()
    }

    infix fun getSymbolFromCurrencyCode(currencyCode: String?) =
        currencyCode?.let {
            try {
                Currency.getInstance(currencyCode).getSymbol(Locale.getDefault())
            }
            catch (e: Exception) {
                Currency.getInstance(currencyCode).symbol
            }
        } ?: ""

    infix fun getCurrencyFromCurrencySymbol(currencySymbol: String?) =
        availableCurrencies.firstOrNull {
            it.symbol == currencySymbol
        }?.currencyCode

    infix fun format(value: Double?): String {
        if (value === null) return "0.00"
        val symbols = DecimalFormatSymbols(Locale.ITALIAN)
        symbols.exponentSeparator = "."
        symbols.decimalSeparator = ','
        val formatter = DecimalFormat("###,###0.00", symbols)
        formatter.groupingSize = 3
        formatter.maximumFractionDigits = 3
        return formatter.format(value)
    }

    fun formatAmount(
        value: Double?,
        currencyCode: String?,
        abs: Boolean? = false,
        hideCurrencyCode: Boolean = false,
        maxFractionDigits: Int = 2,
        noMaxFractionDigits: Boolean = false
    ) : String {

        val maxFracDigits = if(noMaxFractionDigits) value?.toString()?.length ?: maxFractionDigits else maxFractionDigits

        val symbol = getSymbolFromCurrencyCode(currencyCode)

        if (value === null) return if(hideCurrencyCode) "0,00" else "%s0,00".format(symbol)
        val negative = value < 0.0

        val symbols = DecimalFormatSymbols(Locale.ITALIAN)
        symbols.exponentSeparator = "."
        symbols.decimalSeparator = ','

        val formatter = DecimalFormat("###,###0.00", symbols)
        formatter.groupingSize = 3

        formatter.maximumFractionDigits = maxFracDigits

        if(maxFracDigits < 2) {
            formatter.minimumFractionDigits = maxFracDigits
        } else {
            formatter.minimumFractionDigits = 2
        }

        var result = formatter.format(abs(value))

        if(maxFractionDigits > 2 && value.toString() == "0.0") {
            result = value.toInt().toString().substring(0,1)
        }

        if(hideCurrencyCode) {
            return if(!negative) result else "- %s".format(result)
        }

        return if (!negative || abs!!) "%s%s".format(symbol, result) else "%s %s%s".format("-", symbol, result)
    }

}