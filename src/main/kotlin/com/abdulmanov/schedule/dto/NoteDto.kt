package com.abdulmanov.schedule.dto

data class NoteDto(
        val content: String,
        val date: String,
        val time: String,
        val visibility: Boolean
)