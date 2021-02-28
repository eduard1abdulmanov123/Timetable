package com.abdulmanov.schedule

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

fun getDaysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale("ru")).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()

    if(firstDayOfWeek != DayOfWeek.MONDAY){
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)

        daysOfWeek = rhs + lhs
    }

    return daysOfWeek
}

fun getFullTitlesDaysOfWeekFromLocale(): List<String> {
    val daysOfWeek = getDaysOfWeekFromLocale()

    return daysOfWeek.map {
        val name = it.getDisplayName(TextStyle.FULL, Locale("ru"))
        name.first().toUpperCase() + name.slice(1 until name.length)
    }
}

fun getFullTitleDayOfWeekForNumber(number: Int): String {
    val fullTitles = getFullTitlesDaysOfWeekFromLocale()
    return fullTitles[number - 1]
}

fun getFormattedDate(date: String): String{
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("ru"))
    return dateFormatter.format(LocalDate.parse(date))
}