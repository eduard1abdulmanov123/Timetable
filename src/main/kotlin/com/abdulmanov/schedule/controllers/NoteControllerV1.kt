package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.dto.NoteDto
import com.abdulmanov.schedule.models.Note
import com.abdulmanov.schedule.repositories.NoteRepository
import com.abdulmanov.schedule.security.jwt.JwtTokenProvider
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/notes")
class NoteControllerV1(
        private val noteRepository: NoteRepository,
        private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/save")
    fun save(request: HttpServletRequest, @RequestBody noteDto: NoteDto): ResponseEntity<Any>{
        val note = Note(
                content = noteDto.content,
                date = noteDto.date,
                visibility = noteDto.visibility,
                user = jwtTokenProvider.getUser(request)
        )
        val noteFromDatabase = noteRepository.save(note)
        return ResponseEntity.ok(noteFromDatabase)
    }
}