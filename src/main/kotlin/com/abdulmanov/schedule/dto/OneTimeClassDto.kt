package com.abdulmanov.schedule.dto

data class OneTimeClassDto(
        val nameSubject: String = "",
        val nameTeacher: String = "",
        val audience: String = "",
        val typeClass: String = "",
        val startOfClass: String = "",
        val endOfClass: String = "",
        val dateOfClass: String = ""
){

    fun isAllFieldsNotEmpty(): Boolean {
        return nameSubject.isNotEmpty() && nameTeacher.isNotEmpty()
                && audience.isNotEmpty() && typeClass.isNotEmpty()
                && startOfClass.isNotEmpty() && endOfClass.isNotEmpty()
                && dateOfClass.isNotEmpty()
    }
}