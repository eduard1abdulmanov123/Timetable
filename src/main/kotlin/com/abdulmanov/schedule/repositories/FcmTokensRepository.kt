package com.abdulmanov.schedule.repositories

import com.abdulmanov.schedule.models.FcmToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FcmTokensRepository: JpaRepository<FcmToken, String> {
}