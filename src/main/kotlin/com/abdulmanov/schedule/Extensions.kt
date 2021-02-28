package com.abdulmanov.schedule

import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.Timetable
import com.abdulmanov.schedule.repositories.AppUserRepository
import com.abdulmanov.schedule.repositories.FcmTokensRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import java.lang.Exception

fun AppUser.checkRightsEditTimetable(timetable: Timetable):Boolean{
    return username == timetable.creatorUsername
}

fun Timetable.getTokens(userRepository: AppUserRepository, fcmTokensRepository: FcmTokensRepository): List<String> {
    val users = userRepository.findByCurrentTimetableId(id).map { it.username }
    return fcmTokensRepository.findAllById(users).map { it.token }
}

fun Exception.createBadRequest(status: String = "error"): ResponseEntity<Any>{
    val response = hashMapOf("status" to status, "message" to message)
    return ResponseEntity.badRequest().body(response)
}

fun createSuccess(): ResponseEntity<Any>{
    return ResponseEntity.ok(hashMapOf("status" to "success"))
}