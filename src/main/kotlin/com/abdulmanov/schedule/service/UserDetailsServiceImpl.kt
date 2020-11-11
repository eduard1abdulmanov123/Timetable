package com.abdulmanov.schedule.service

import com.abdulmanov.schedule.repositories.AppUserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
        private val appUserRepository: AppUserRepository
): UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val appUser = appUserRepository.findByUsername(username) ?: throw UsernameNotFoundException("$username don't exists")
        return User(appUser.username, appUser.password, listOf())
    }
}