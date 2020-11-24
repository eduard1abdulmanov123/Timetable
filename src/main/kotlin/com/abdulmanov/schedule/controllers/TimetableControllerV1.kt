package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.createBadRequest
import com.abdulmanov.schedule.security.jwt.JwtTokenProvider
import com.abdulmanov.schedule.service.TimetableService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/timetable")
class TimetableControllerV1(
        private val timetableService: TimetableService,
        private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/create")
    fun create(request: HttpServletRequest): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)

        return try{
            val createdTimetable = timetableService.create(user)
            ResponseEntity.ok(createdTimetable)
        }catch (e:Exception){
            e.createBadRequest()
        }
    }

    @PostMapping("/join/{id}")
    fun join(request: HttpServletRequest, @PathVariable("id") timetableId: Int): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)

        return try {
            val timetable = timetableService.join(user, timetableId)
            ResponseEntity.ok(timetable)
        }catch (e:Exception){
            e.createBadRequest()
        }
    }

    @RequestMapping("/")
    fun get(request: HttpServletRequest):ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)

        return try {
            val timetable = timetableService.get(user)
            ResponseEntity.ok(timetable)
        }catch (e:Exception){
            e.createBadRequest()
        }
    }
}