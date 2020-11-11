package com.abdulmanov.schedule.security.jwt

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException

data class JwtAuthenticationException(
        val status: HttpStatus,
        override val message: String?
): AuthenticationException(message)