package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.dto.UserDto
import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.repositories.AppUserRepository
import com.abdulmanov.schedule.security.jwt.JwtTokenProvider
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.naming.AuthenticationException

@RestController
@RequestMapping("/api/v1/auth")
class AuthControllerV1(
        private val authenticationManager: AuthenticationManager,
        private val userRepository: AppUserRepository,
        private val jwtTokenProvider: JwtTokenProvider,
        private val passwordEncoder: PasswordEncoder
){
    @PostMapping("/sing-up")
    fun singUp(@RequestBody userDto: UserDto): ResponseEntity<HashMap<String, Any?>>{
        return try {
            if(userRepository.existsByUsername(userDto.username)){
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }

            val appUser = saveUserToDatabase(userDto)

            authenticate(userDto.username, userDto.password)
            generateAnswerWithJwtToken(appUser)
        }catch (e: AuthenticationException){
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @PostMapping("/sing-in")
    fun singIn(@RequestBody userDto: UserDto): ResponseEntity<HashMap<String, Any?>> {
        return try{
            if(!userRepository.existsByUsername(userDto.username)){
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }

            val appUser = userRepository.findByUsername(userDto.username)!!

            authenticate(userDto.username, userDto.password)
            generateAnswerWithJwtToken(appUser)
        }catch (e: AuthenticationException){
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    private fun saveUserToDatabase(userDto: UserDto): AppUser{
        val user = AppUser(username = userDto.username, password = passwordEncoder.encode(userDto.password))
        return userRepository.save(user)
    }

    private fun authenticate(username: String, password: String){
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(username, password)
        authenticationManager.authenticate(usernamePasswordAuthenticationToken)
    }

    private fun generateAnswerWithJwtToken(appUser: AppUser): ResponseEntity<HashMap<String, Any?>>{
        val token = jwtTokenProvider.createToken(appUser.username)
        return ResponseEntity.ok(
                hashMapOf(
                        "username" to appUser.username,
                        "token" to token,
                        "currentTimetableId" to appUser.currentTimetableId
                )
        )
    }
}