package com.abdulmanov.schedule.dto

data class MultipleClassDto(
        val nameSubject: String = "",
        val nameTeacher: String = "",
        val audience: String = "",
        val typeClass: String = "",
        val startOfClass: String = "",
        val endOfClass: String = "",
        val dayOfWeek: Int = -1,
        val periodicity: Int = -1
){

    fun isAllFieldsNotEmpty(): Boolean {
        return nameSubject.isNotEmpty() && nameTeacher.isNotEmpty()
                && audience.isNotEmpty() && typeClass.isNotEmpty()
                && startOfClass.isNotEmpty() && endOfClass.isNotEmpty()
                && dayOfWeek != -1 && periodicity != -1
    }
}