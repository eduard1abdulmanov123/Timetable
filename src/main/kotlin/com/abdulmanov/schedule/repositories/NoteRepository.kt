package com.abdulmanov.schedule.repositories

import com.abdulmanov.schedule.models.Note
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoteRepository: JpaRepository<Note, Int> {
}