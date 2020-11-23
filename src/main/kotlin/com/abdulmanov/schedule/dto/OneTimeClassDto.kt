package com.abdulmanov.schedule.dto

data class OneTimeClassDto(
        val nameSubject: String,
        val nameTeacher: String,
        val audience: String,
        val typeClass: String,
        val color: String,
        val startOfClass: String,
        val endOfClass: String,
        val dateOfClass: Long = 0
)