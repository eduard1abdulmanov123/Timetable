package com.abdulmanov.schedule.repositories

import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.Note
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface NoteRepository: JpaRepository<Note, Int> {

    fun findByUser(user: AppUser): List<Note>

    @Query(value = "SELECT * FROM ${Note.TABLE_NAME} WHERE ${Note.COLUMN_ID} = :id", nativeQuery = true)
    fun findNoteById(@Param("id") id: Int): Note
}