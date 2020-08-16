package com.shouman.apps.reseller.admin.utils

fun getRandomColor(position: Int): String? {
var pos = position
    val colors: Array<String> =  arrayOf(
    "#EF5350",
    "#EC407A",
    "#AB47BC",
    "#42A5F5",
    "#26C6DA",
    "#26A69A",
    "#FFEE58",
    "#FFA726",
    "#D4E157"
    )
    return if (position < colors.size) colors[position] else {
        pos = position % 9
        colors[pos]
    }
}