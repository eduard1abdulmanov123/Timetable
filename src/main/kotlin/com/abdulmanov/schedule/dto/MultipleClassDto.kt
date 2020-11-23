package com.abdulmanov.schedule.dto

data class MultipleClassDto(
        val nameSubject: String = "",
        val nameTeacher: String = "",
        val audience: String = "",
        val typeClass: String = "",
        val color: String = "",
        val startOfClass: String = "",
        val endOfClass: String = "",
        val dayOfWeek: Int = 0,
        val periodicity: Int = 0
)