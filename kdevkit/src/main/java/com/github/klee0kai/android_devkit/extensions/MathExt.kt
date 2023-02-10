package com.github.klee0kai.android_devkit.extensions

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
    return String.format("%.${-exp}f", this);
}