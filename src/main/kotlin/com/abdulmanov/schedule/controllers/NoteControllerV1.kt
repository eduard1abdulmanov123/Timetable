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
        val createdNote = noteRepository.save(note)
        return ResponseEntity.ok(createdNote)
    }

    @PostMapping("/update/{id}")
    fun updateNote(request: HttpServletRequest, @RequestBody noteDto: NoteDto, @PathVariable("id") noteId: Int): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)
        val oldNote = noteRepository.findById(noteId)

        if(oldNote.isEmpty){
            val body = hashMapOf("status" to "error", "message" to "Note $noteId does not exists")
            return ResponseEntity.badRequest().body(body)
        }

        if(oldNote.get().user != user){
            val body = hashMapOf("status" to "error", "message" to "You are not authorized to modify this note $noteId")
            return ResponseEntity.badRequest().body(body)
        }

        val newNote = oldNote.get().copy(content = noteDto.content, date = noteDto.date, visibility = noteDto.visibility)
        val modifiedNote = noteRepository.save(newNote)
        return ResponseEntity.ok(modifiedNote)
    }

    @PostMapping("/delete/{id}")
    fun deleteNote(request: HttpServletRequest, @PathVariable("id") noteId: Int): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)
        val oldNote = noteRepository.findById(noteId)

        if(oldNote.isEmpty){
            val body = hashMapOf("status" to "error", "message" to "Note $noteId does not exists")
            return ResponseEntity.badRequest().body(body)
        }

        if(oldNote.get().user != user){
            val body = hashMapOf("status" to "error", "message" to "You are not authorized to modify this note $noteId")
            return ResponseEntity.badRequest().body(body)
        }

        noteRepository.deleteById(noteId)

        return ResponseEntity.ok(hashMapOf("status" to "success"))
    }

    @GetMapping("/")
    fun getNotes(request: HttpServletRequest): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)
        val notes = noteRepository.findByUser(user)
        return ResponseEntity.ok(notes)
    }

}