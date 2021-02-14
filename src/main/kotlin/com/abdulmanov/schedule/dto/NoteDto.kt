package com.abdulmanov.schedule.dto

data class NoteDto(
        val content: String = "",
        val date: String = "",
        val time: String = "",
        val visibility: Boolean = false
) {

    fun isAllFieldsNotEmpty(): Boolean {
        return content.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()
    }
}