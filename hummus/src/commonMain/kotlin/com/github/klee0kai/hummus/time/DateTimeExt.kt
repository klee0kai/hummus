package com.github.klee0kai.hummus.time

import kotlinx.datetime.LocalDateTime


fun LocalDateTime.formatToString(): String {
    val yyyy = year.toString().padStart(4, '0')
    val mm = monthNumber.toString().padStart(2, '0')
    val dd = dayOfMonth.toString().padStart(2, '0')

    val hh = hour.toString().padStart(2, '0')
    val min = minute.toString().padStart(2, '0')
    val sec = second.toString().padStart(2, '0')

    return "$yyyy-$mm-$dd $hh:$min:$sec"
}