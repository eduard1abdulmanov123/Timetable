package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.createBadRequest
import com.abdulmanov.schedule.dto.TimetableInfoDto
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
    fun create(request: HttpServletRequest, @RequestBody timetableInfoDto: TimetableInfoDto): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)

        return try{
            val createdTimetable = timetableService.create(user, timetableInfoDto)
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

    @PostMapping("/changeTypeWeek")
    fun changeTypeWeek(request: HttpServletRequest, @RequestBody timetableInfoDto: TimetableInfoDto): ResponseEntity<Any> {
        val user = jwtTokenProvider.getUser(request)

        return try {
            val timetable = timetableService.changeTypeWeek(user, timetableInfoDto)
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
            when (e.message) {
                TimetableService.EMPTY_TIMETABLE_ERROR -> {
                    e.createBadRequest("empty_timetable")
                }
                TimetableService.USER_IS_NOT_CONNECT -> {
                    e.createBadRequest("user_is_not_connect")
                }
                else -> {
                    e.createBadRequest()
                }
            }
        }
    }

    @PostMapping("/removeAll")
    fun removeAll(request: HttpServletRequest): ResponseEntity<Any> {
        val user = jwtTokenProvider.getUser(request)

        return try {
            timetableService.removeAll(user)
            ResponseEntity.ok("success")
        }catch (e:Exception){
            e.createBadRequest()
        }
    }
}