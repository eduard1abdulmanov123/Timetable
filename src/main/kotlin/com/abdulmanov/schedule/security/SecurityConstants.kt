package com.abdulmanov.schedule.security

interface SecurityConstants {

    companion object{
        const val AUTHORIZATION_HEADER = "Authorization"
        const val EXPIRATION_TIME = 86_400_000L
        const val SECRET_KEY = "scheduleSecretKey"
        const val JWT_INVALID_MESSAGE = "JWT token is expired or invalid"

        const val SING_IN = "/api/v1/auth/sing-in"
        const val SING_UP = "/api/v1/auth/sing-up"
    }
}