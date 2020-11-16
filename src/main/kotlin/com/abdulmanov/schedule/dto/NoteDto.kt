package com.abdulmanov.schedule.dto

data class NoteDto(
        val content: String,
        val date: Long,
        val visibility: Boolean
)