package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.createBadRequest
import com.abdulmanov.schedule.createSuccess
import com.abdulmanov.schedule.dto.MultipleClassDto
import com.abdulmanov.schedule.security.jwt.JwtTokenProvider
import com.abdulmanov.schedule.service.MultipleClassService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/multiple_class")
class MultipleClassControllerV1(
        private val multipleClassService: MultipleClassService,
        private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/create")
    fun create(
            request: HttpServletRequest, 
            @RequestBody multipleClassDto: MultipleClassDto
    ): ResponseEntity<Any> {
        val user = jwtTokenProvider.getUser(request)
        
        return try {
            val createdMultipleClass = multipleClassService.create(user, multipleClassDto)
            ResponseEntity.ok(createdMultipleClass)
        }catch (e: Exception){
            e.createBadRequest()
        }
    }

    @PostMapping("/update/{id}")
    fun update(
            request: HttpServletRequest,
            @PathVariable("id") multipleClassId: Int,
            @RequestBody multipleClassDto: MultipleClassDto
    ): ResponseEntity<Any> {
        val user = jwtTokenProvider.getUser(request)

        return try {
            val modifiedMultipleClass = multipleClassService.update(user, multipleClassId, multipleClassDto)
            ResponseEntity.ok(modifiedMultipleClass)
        }catch (e: Exception){
            e.createBadRequest()
        }
    }

    @PostMapping("/delete/{id}")
    fun delete(
            request: HttpServletRequest,
            @PathVariable("id") multipleClassId: Int
    ): ResponseEntity<Any> {
        val user = jwtTokenProvider.getUser(request)

        return try {
            multipleClassService.delete(user, multipleClassId)
            createSuccess()
        }catch (e: Exception){
            e.createBadRequest()
        }
    }
}