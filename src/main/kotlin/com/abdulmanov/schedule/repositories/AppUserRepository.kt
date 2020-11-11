package com.abdulmanov.schedule.repositories

import com.abdulmanov.schedule.models.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AppUserRepository : JpaRepository<AppUser, String> {

    @Query("SELECT * FROM ${AppUser.TABLE_NAME} WHERE ${AppUser.COLUMN_USERNAME} = :username", nativeQuery = true)
    fun findByUsername(@Param("username") username: String): AppUser?

    @Query("SELECT EXISTS (SELECT * FROM ${AppUser.TABLE_NAME} WHERE ${AppUser.COLUMN_USERNAME} = :username)", nativeQuery = true)
    fun existsByUsername(@Param("username") username: String): Boolean
}