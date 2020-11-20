package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.models.Timetable
import com.abdulmanov.schedule.repositories.AppUserRepository
import com.abdulmanov.schedule.repositories.TimetableRepository
import com.abdulmanov.schedule.security.jwt.JwtTokenProvider
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/timetable")
class TimetableControllerV1(
        private val appUserRepository: AppUserRepository,
        private val timetableRepository: TimetableRepository,
        private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/create")
    fun createTimetable(request: HttpServletRequest): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)

        val timetable = Timetable(creatorUsername = user.username, dateCreated = Calendar.getInstance().timeInMillis, weekNumber = 0)
        val createdTimetable = timetableRepository.save(timetable)

        val newUser = user.copy(currentTimetableId = createdTimetable.id)
        appUserRepository.save(newUser)

        return ResponseEntity.ok(createdTimetable)
    }

    @PostMapping("/join/{id}")
    fun joinTheTimetable(request: HttpServletRequest, @PathVariable("id") timetableId: Int): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)
        val timetable = timetableRepository.findById(timetableId)

        if(timetable.isEmpty){
            val body = hashMapOf("status" to "error", "message" to "Timetable $timetableId does not exists")
            return ResponseEntity.badRequest().body(body)
        }

        val newUser = user.copy(currentTimetableId = timetable.get().id)
        appUserRepository.save(newUser)

        return ResponseEntity.ok(timetable)
    }

    @RequestMapping("/")
    fun getSchedule():String{
        return "dasdadadadadadadad"
    }
}