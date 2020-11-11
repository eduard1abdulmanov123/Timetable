package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.repositories.AppUserRepository
import com.abdulmanov.schedule.security.SecurityConstants
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
    fun singUp(@RequestBody appUser: AppUser): ResponseEntity<HashMap<String, Any>>{
        return try {
            if(userRepository.existsByUsername(appUser.username)){
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }

            saveUserToDatabase(appUser)
            authenticate(appUser)
            generateAnswerWithJwtToken(appUser)
        }catch (e: AuthenticationException){
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @PostMapping("/sing-in")
    fun singIn(@RequestBody appUser: AppUser): ResponseEntity<HashMap<String, Any>> {
        return try{
            if(!userRepository.existsByUsername(appUser.username)){
                return ResponseEntity(HttpStatus.BAD_REQUEST)
            }

            authenticate(appUser)
            generateAnswerWithJwtToken(appUser)
        }catch (e: AuthenticationException){
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    private fun saveUserToDatabase(appUser: AppUser){
        val user = AppUser(username = appUser.username, password = passwordEncoder.encode(appUser.password))
        userRepository.save(user)
    }

    private fun authenticate(appUser: AppUser){
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(appUser.username, appUser.password)
        authenticationManager.authenticate(usernamePasswordAuthenticationToken)
    }

    private fun generateAnswerWithJwtToken(appUser: AppUser): ResponseEntity<HashMap<String, Any>>{
        val token = jwtTokenProvider.createToken(appUser.username)
        return ResponseEntity.ok(hashMapOf<String, Any>("username" to appUser.username, "token" to token))
    }
}