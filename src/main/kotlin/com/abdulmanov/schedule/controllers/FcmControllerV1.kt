package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.createBadRequest
import com.abdulmanov.schedule.createSuccess
import com.abdulmanov.schedule.dto.FcmTokenDto
import com.abdulmanov.schedule.dto.NoteDto
import com.abdulmanov.schedule.models.FcmToken
import com.abdulmanov.schedule.repositories.FcmTokensRepository
import com.abdulmanov.schedule.security.jwt.JwtTokenProvider
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/fcm")
class FcmControllerV1(
        private val fcmTokensRepository: FcmTokensRepository,
        private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/add_token")
    fun addToken(
            request: HttpServletRequest,
            @RequestBody fcmTokenDto: FcmTokenDto
    ): ResponseEntity<Any> {
        val user = jwtTokenProvider.getUser(request)

        return try{
            val fcmToken = FcmToken(username = user.username, token = fcmTokenDto.token)
            fcmTokensRepository.save(fcmToken)
            createSuccess()
        }catch (e:Exception){
            e.createBadRequest()
        }
    }
}