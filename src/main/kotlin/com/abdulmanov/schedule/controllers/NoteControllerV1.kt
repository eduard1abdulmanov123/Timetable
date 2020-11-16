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
    fun saveNote(request: HttpServletRequest, @RequestBody noteDto: NoteDto): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)
        val note = Note(content = noteDto.content, date = noteDto.date, visibility = noteDto.visibility, user = user)
        val noteFromDatabase = noteRepository.save(note)
        return ResponseEntity.ok(noteFromDatabase)
    }

    @PostMapping("/update/{id}")
    fun updateNote(@RequestBody noteDto: NoteDto, @PathVariable("id") noteId: String): ResponseEntity<Any>{
        val note = noteRepository.findNoteById(noteId.toInt())
        val newNote = note.copy(content = noteDto.content, date = noteDto.date, visibility = noteDto.visibility)
        noteRepository.save(newNote)
        return ResponseEntity.ok(hashMapOf("status" to "success"))
    }

    @PostMapping("/delete/{id}")
    fun deleteNote(@PathVariable("id") noteId: String): ResponseEntity<Any>{
        noteRepository.deleteById(noteId.toInt())
        return ResponseEntity.ok(hashMapOf("status" to "success"))
    }

    @GetMapping("/")
    fun getNotes(request: HttpServletRequest): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)
        val notes = noteRepository.findByUser(user)
        return ResponseEntity.ok(notes)
    }

}