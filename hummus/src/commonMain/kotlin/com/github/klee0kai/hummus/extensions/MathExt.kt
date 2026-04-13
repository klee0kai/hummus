package com.github.klee0kai.hummus.extensions

import kotlin.math.pow

fun Number.degExponent(): Int {
    val v = if (this.toFloat() > 0) this.toFloat() else -this.toFloat()
    var exp = 0
    if (v > 1f) {
        while (10f.pow(++exp) < v) {
            /* ignore */
        }
        exp--;
    } else {
        while (10f.pow(--exp) > v) {
            /*ignore */
        }
    }
    return exp
}

fun Number.roundDecExponent(exp: Int): Float {
    if (exp == 0)
        return this.toInt().toFloat()
    if (exp > 0)
        return ((this.toFloat() / 10f.pow(exp)).toInt() * 10f.pow(exp))
    return ((this.toFloat() * 10f.pow(-exp)).toInt().toFloat() / 10f.pow(-exp))
}

fun Number.strFormat(exp: Int): String {
    if (exp >= 0)
        return this.toInt().toString()

    val multiplier = 10f.pow(-exp).toInt()
    val intValue = this.toFloat()
    val scaled = (intValue * multiplier).toLong()

    return buildString {
        append((scaled / multiplier).toInt())
        append('.')
        val remainder = scaled % multiplier
        val padding = -exp - 1
        for (i in 0 until padding) {
            if (remainder < 10f.pow(i + 1)) {
                append('0')
            }
        }
        append(remainder)
    }
}
