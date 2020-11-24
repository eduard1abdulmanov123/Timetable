package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.createBadRequest
import com.abdulmanov.schedule.createSuccess
import com.abdulmanov.schedule.dto.OneTimeClassDto
import com.abdulmanov.schedule.security.jwt.JwtTokenProvider
import com.abdulmanov.schedule.service.OneTimeClassService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/onetime_class")
class OneTimeClassControllerV1(
        private val oneTimeClassService: OneTimeClassService,
        private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/create")
    fun create(
            request: HttpServletRequest,
            @RequestBody oneTimeClassDto:OneTimeClassDto
    ): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)

        return try {
            val createdOneTimeClass = oneTimeClassService.create(user, oneTimeClassDto)
            ResponseEntity.ok(createdOneTimeClass)
        }catch (e: Exception){
            e.createBadRequest()
        }
    }

    @PostMapping("/update/{id}")
    fun update(
            request: HttpServletRequest,
            @PathVariable("id") oneTimeClassId: Int,
            @RequestBody oneTimeClassDto:OneTimeClassDto
    ): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)

        return try {
            val modifiedOneTimeClass = oneTimeClassService.update(user, oneTimeClassId, oneTimeClassDto)
            ResponseEntity.ok(modifiedOneTimeClass)
        }catch (e: Exception){
            e.createBadRequest()
        }
    }

    @PostMapping("/delete/{id}")
    fun delete(
            request: HttpServletRequest,
            @PathVariable("id") oneTimeClassId: Int
    ): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)

        return try {
            oneTimeClassService.delete(user, oneTimeClassId)
            createSuccess()
        }catch (e: Exception){
            e.createBadRequest()
        }
    }
}