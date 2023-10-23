package com.harshul.jettipapp.util

fun calculateTipAmount(totalBillValue: Double?, tipPerc: Int): Double {
    return totalBillValue?.let { billValue ->
        (billValue * tipPerc) / 100
    } ?: 0.0
}

fun calculateTotalPerPerson(
    totalBillValue: Double,
    splitBy: Int,
    tipPerc: Int
): Double {
    val bill =
        calculateTipAmount(totalBillValue = totalBillValue, tipPerc = tipPerc) + totalBillValue
    return (bill / splitBy)
}