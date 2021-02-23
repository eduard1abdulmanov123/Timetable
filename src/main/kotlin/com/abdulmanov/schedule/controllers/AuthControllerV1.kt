package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.createBadRequest
import com.abdulmanov.schedule.dto.RefreshTokenDto
import com.abdulmanov.schedule.dto.UserDto
import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.repositories.AppUserRepository
import com.abdulmanov.schedule.security.jwt.JwtTokenProvider
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
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
    fun singUp(@RequestBody userDto: UserDto): ResponseEntity<Any>{
        return try {
            if(userRepository.existsByUsername(userDto.username)){
                throw Exception("Пользователь с таким логином уже существует")
            }

            val appUser = saveUserToDatabase(userDto)

            authenticate(userDto.username, userDto.password)
            generateAnswerWithJwtToken(appUser)
        }catch (e: Exception){
            e.createBadRequest()
        }
    }

    @PostMapping("/sing-in")
    fun singIn(@RequestBody userDto: UserDto): ResponseEntity<Any> {
        return try{
            if(!userRepository.existsByUsername(userDto.username)){
                throw Exception("Пользователя с таким логином не существует")
            }

            val appUser = userRepository.findByUsername(userDto.username)!!

            authenticate(userDto.username, userDto.password)
            generateAnswerWithJwtToken(appUser)
        }catch (e: Exception){
            e.createBadRequest()
        }
    }

    @PostMapping("/refreshToken")
    fun refreshToken(@RequestBody refreshTokenDto: RefreshTokenDto): ResponseEntity<Any>{
        return try {
            val username = jwtTokenProvider.getUsername(refreshTokenDto.refreshToken)

            if(jwtTokenProvider.validateToken(refreshTokenDto.refreshToken)){
                generateAnswerRefreshJwtToken(username)
            }else{
                throw Exception(REFRESH_TOKEN_IS_NOT_VALIDATE)
            }
        } catch(e: Exception) {
            e.createBadRequest()
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

    private fun generateAnswerWithJwtToken(appUser: AppUser): ResponseEntity<Any>{
        val token = jwtTokenProvider.createToken(appUser.username)
        val refreshToken = jwtTokenProvider.createRefreshToken(appUser.username)
        return ResponseEntity.ok(
                hashMapOf(
                        "username" to appUser.username,
                        "token" to token,
                        "refreshToken" to refreshToken,
                        "currentTimetableId" to appUser.currentTimetableId
                )
        )
    }

    private fun generateAnswerRefreshJwtToken(username: String): ResponseEntity<Any> {
        val token = jwtTokenProvider.createToken(username)
        val refreshToken = jwtTokenProvider.createRefreshToken(username)
        return ResponseEntity.ok(
                hashMapOf(
                        "token" to token,
                        "refreshToken" to refreshToken
                )
        )
    }

    companion object {
        private const val REFRESH_TOKEN_IS_NOT_VALIDATE = "Refresh Token не валиден"
    }
}