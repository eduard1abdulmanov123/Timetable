package com.abdulmanov.schedule.service

import com.abdulmanov.schedule.dto.NoteDto
import com.abdulmanov.schedule.getFormattedDate
import com.abdulmanov.schedule.getTokens
import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.Note
import com.abdulmanov.schedule.repositories.AppUserRepository
import com.abdulmanov.schedule.repositories.FcmTokensRepository
import com.abdulmanov.schedule.repositories.NoteRepository
import com.abdulmanov.schedule.repositories.TimetableRepository
import org.springframework.stereotype.Service
import java.lang.StringBuilder

@Service
class NoteService(
        private val serviceNotification: ServiceNotification,
        private val noteRepository: NoteRepository,
        private val timetableRepository: TimetableRepository,
        private val appUserRepository: AppUserRepository,
        private val fcmTokensRepository: FcmTokensRepository
) {
    fun create(user: AppUser, noteDto: NoteDto): Note {
        val timetable = timetableRepository.findById(user.currentTimetableId!!)

        when {
            !noteDto.isAllFieldsNotEmpty() -> {
                throw Exception("Не все поля заполнены!")
            }
        }

        val note = Note(
                content = noteDto.content,
                date = noteDto.date,
                time = noteDto.time,
                visibility = noteDto.visibility,
                user = user
        )

        return noteRepository.save(note).apply {
            if(visibility){
                serviceNotification.sendNotifications(
                        title = "Новое заметка",
                        message = "Описание: $content\n" +
                                "Дата: ${getFormattedDate(date)}\n" +
                                "Время: $time" ,
                        tokens = timetable.get().getTokens(appUserRepository,fcmTokensRepository)
                )
            }
        }
    }

    fun update(user: AppUser, noteId: Int, noteDto: NoteDto): Note {
        val timetable = timetableRepository.findById(user.currentTimetableId!!)
        val note = noteRepository.findById(noteId)

        when {
            note.isEmpty -> {
                throw Exception("Данной заметки не существует")
            }
            note.get().user != user -> {
                throw Exception("Нет доступа к данной заметки")
            }
            !noteDto.isAllFieldsNotEmpty() -> {
                throw Exception("Не все поля заполнены!")
            }
        }

        val oldNote = note.get()

        val updateNote = note.get().copy(
                content = noteDto.content,
                date = noteDto.date,
                time = noteDto.time,
                visibility = noteDto.visibility
        )

        val message = StringBuilder().run {
            if(oldNote.date != updateNote.date){
                append("Изменилась дата заметки: ${getFormattedDate(updateNote.date)}\n")
            }
            if(oldNote.time != updateNote.time){
                append("Изменилось время заметки: ${updateNote.time}\n")
            }else{
                append(updateNote.content)
            }
        }

        return noteRepository.save(updateNote).apply {
            if(visibility){
                serviceNotification.sendNotifications(
                        title = "Изменения в заметки",
                        message = "Описание: ${content}\n$message",
                        tokens = timetable.get().getTokens(appUserRepository,fcmTokensRepository)
                )
            }
        }
    }

    fun delete(user: AppUser, noteId: Int) {
        val timetable = timetableRepository.findById(user.currentTimetableId!!)
        val note = noteRepository.findById(noteId)

        when {
            note.isEmpty -> {
                throw Exception("Данной заметки не существует")
            }
            note.get().user != user -> {
                throw Exception("Нет доступа к данной заметки")
            }
        }
        
        noteRepository.delete(note.get())

        if(note.get().visibility){
            serviceNotification.sendNotifications(
                    title = "Земетка была удалена",
                    message = note.get().content,
                    tokens = timetable.get().getTokens(appUserRepository,fcmTokensRepository)
            )
        }
    }

    fun get(user:AppUser): List<Note> {
        return noteRepository.findByUser(user)
    }

    fun getTimetableOwnerNotes(user: AppUser): List<Note> {
        if(user.currentTimetableId == null)
            throw Exception("Данный пользователь не подключен к таблице")

        val timetable = timetableRepository.findById(user.currentTimetableId)
        val ownerUser = appUserRepository.findByUsername(timetable.get().creatorUsername)

        return noteRepository.findByUser(ownerUser!!).filter { it.visibility || user.username == timetable.get().creatorUsername}
    }
}