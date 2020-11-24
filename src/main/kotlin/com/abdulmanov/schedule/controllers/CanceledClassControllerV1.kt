package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.createBadRequest
import com.abdulmanov.schedule.createSuccess
import com.abdulmanov.schedule.dto.CanceledClassDto
import com.abdulmanov.schedule.security.jwt.JwtTokenProvider
import com.abdulmanov.schedule.service.CanceledClassService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/canceled_class")
class CanceledClassControllerV1(
        private val canceledClassService: CanceledClassService,
        private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/create/{multiple_class_id}")
    fun create(
            request: HttpServletRequest,
            @PathVariable("multiple_class_id") multipleClassId: Int,
            @RequestBody canceledClassDto: CanceledClassDto
    ):ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)

        return try {
            val createdCanceledClass = canceledClassService.create(user, multipleClassId, canceledClassDto)
            ResponseEntity.ok(createdCanceledClass)
        }catch (e: Exception){
            e.createBadRequest()
        }
    }

    @PostMapping("/delete/{id}")
    fun delete(
            request: HttpServletRequest,
            @PathVariable("id") canceledClassedId: Int
    ): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)

        return try{
            canceledClassService.delete(user,canceledClassedId)
            createSuccess()
        }catch (e:Exception){
            e.createBadRequest()
        }
    }
}