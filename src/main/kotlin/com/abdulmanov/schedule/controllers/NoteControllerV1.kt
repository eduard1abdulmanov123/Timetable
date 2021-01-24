package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.createBadRequest
import com.abdulmanov.schedule.createSuccess
import com.abdulmanov.schedule.dto.NoteDto
import com.abdulmanov.schedule.security.jwt.JwtTokenProvider
import com.abdulmanov.schedule.service.NoteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/notes")
class NoteControllerV1(
        private val noteService: NoteService,
        private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/create")
    fun create(
            request: HttpServletRequest,
            @RequestBody noteDto: NoteDto
    ): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)

        return try{
            val createdNote = noteService.create(user, noteDto)
            return ResponseEntity.ok(createdNote)
        }catch (e:Exception){
            e.createBadRequest()
        }
    }

    @PostMapping("/update/{id}")
    fun update(
            request: HttpServletRequest,
            @PathVariable("id") noteId: Int,
            @RequestBody noteDto: NoteDto
    ): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)

        return try{
            val modifiedNote = noteService.update(user, noteId, noteDto)
            return ResponseEntity.ok(modifiedNote)
        }catch (e:Exception){
            e.createBadRequest()
        }
    }

    @PostMapping("/delete/{id}")
    fun delete(
            request: HttpServletRequest,
            @PathVariable("id") noteId: Int
    ): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)

        return try{
            noteService.delete(user, noteId)
            createSuccess()
        }catch (e:Exception){
            e.createBadRequest()
        }
    }

    @GetMapping("/")
    fun getNotes(request: HttpServletRequest): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)

        return try{
            val notes = noteService.get(user)
            ResponseEntity.ok(notes)
        }catch (e:Exception){
            e.createBadRequest()
        }
    }

    @GetMapping("/timetable_owner")
    fun getTimetableOwnerNotes(request: HttpServletRequest): ResponseEntity<Any>{
        val currentUser = jwtTokenProvider.getUser(request)

        return try{
            val notes = noteService.getTimetableOwnerNotes(currentUser)
            ResponseEntity.ok(notes)
        }catch (e:Exception){
            e.createBadRequest()
        }
    }
}