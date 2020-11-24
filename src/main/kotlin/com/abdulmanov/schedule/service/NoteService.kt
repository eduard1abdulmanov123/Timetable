package com.abdulmanov.schedule.service

import com.abdulmanov.schedule.dto.NoteDto
import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.Note
import com.abdulmanov.schedule.repositories.AppUserRepository
import com.abdulmanov.schedule.repositories.NoteRepository
import com.abdulmanov.schedule.repositories.TimetableRepository
import org.springframework.stereotype.Service

@Service
class NoteService(
        private val noteRepository: NoteRepository,
        private val timetableRepository: TimetableRepository,
        private val appUserRepository: AppUserRepository
) {
    fun create(user: AppUser, noteDto: NoteDto): Note {
        val note = Note(
                content = noteDto.content,
                date = noteDto.date,
                visibility = noteDto.visibility,
                user = user
        )
        return noteRepository.save(note)
    }

    fun update(user: AppUser, noteId: Int, noteDto: NoteDto): Note {
        val note = noteRepository.findById(noteId)

        if(note.isEmpty){
            throw Exception("Данной заметки не существует")
        }

        if(note.get().user != user){
            throw Exception("Нет доступа к данной заметки")
        }

        val updateNote = note.get().copy(
                content = noteDto.content,
                date = noteDto.date,
                visibility = noteDto.visibility
        )

        return noteRepository.save(updateNote)
    }

    fun delete(user: AppUser, noteId: Int) {
        val note = noteRepository.findById(noteId)

        if(note.isEmpty){
            throw Exception("Данной заметки не существует")
        }

        if(note.get().user != user){
            throw Exception("Нет доступа к данной заметки")
        }

        noteRepository.delete(note.get())
    }

    fun get(user:AppUser): List<Note> {
        return noteRepository.findByUser(user)
    }

    fun getTimetableOwnerNotes(user: AppUser): List<Note> {
        if(user.currentTimetableId == null)
            throw Exception("Данный пользователь не подключен к таблице")

        val timetable = timetableRepository.findById(user.currentTimetableId)
        val ownerUser = appUserRepository.findByUsername(timetable.get().creatorUsername)

        return noteRepository.findByUser(ownerUser!!)
    }
}