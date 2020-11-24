package com.abdulmanov.schedule

import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.Timetable
import org.springframework.http.ResponseEntity
import java.lang.Exception

fun AppUser.checkRightsEditTimetable(timetable: Timetable):Boolean{
    return username == timetable.creatorUsername
}

fun Exception.createBadRequest(): ResponseEntity<Any>{
    val response = hashMapOf("status" to "error", "message" to message)
    return ResponseEntity.badRequest().body(response)
}

fun createSuccess(): ResponseEntity<Any>{
    return ResponseEntity.ok(hashMapOf("status" to "success"))
}