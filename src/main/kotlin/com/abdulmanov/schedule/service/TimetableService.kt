package com.abdulmanov.schedule.service

import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.Timetable
import com.abdulmanov.schedule.repositories.*
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class TimetableService(
        private val appUserRepository: AppUserRepository,
        private val timetableRepository: TimetableRepository,
        private val oneTimeClassRepository: OneTimeClassRepository,
        private val multipleClassRepository: MultipleClassRepository,
        private val canceledClassRepository: CanceledClassRepository
) {

    fun create(user: AppUser): Timetable{
        val defaultTimetable = createDefaultTimetable(user)
        val createdTimetable = timetableRepository.save(defaultTimetable)

        attachUserToTimetable(user, createdTimetable)

        return defaultTimetable
    }

    fun join(user: AppUser, timetableId: Int): Timetable {
        val timetable = timetableRepository.findById(timetableId)

        if(timetable.isEmpty){
            throw Exception("Данное расписание не создано или удалено")
        }

        when {
            user.currentTimetableId == null -> {
                attachUserToTimetable(user, timetable.get())
            }
            user.currentTimetableId != timetableId -> {
                val userTimetable = timetableRepository.findById(user.currentTimetableId)

                if(user.username == userTimetable.get().creatorUsername){
                    oneTimeClassRepository.deleteAll(userTimetable.get().oneTimeClasses)
                    userTimetable.get().multipleClasses.forEach {
                        canceledClassRepository.deleteAll(it.canceledClasses)
                    }
                    multipleClassRepository.deleteAll(userTimetable.get().multipleClasses)
                    timetableRepository.delete(userTimetable.get())
                }

                attachUserToTimetable(user, timetable.get())
            }
        }

        return timetable.get()
    }

    fun get(user: AppUser):Timetable {
        if(user.currentTimetableId == null){
            throw Exception("Данный пользователь не подключен к расписанию")
        }

        val timetable = timetableRepository.findById(user.currentTimetableId)

        if(timetable.isEmpty){
            throw Exception("Данное расписание не создано или удалено")
        }

        return timetable.get()
    }

    private fun attachUserToTimetable(user:AppUser, timetable: Timetable){
        val updatedUser = user.copy(currentTimetableId = timetable.id)
        appUserRepository.save(updatedUser)
    }

    private fun createDefaultTimetable(user: AppUser): Timetable{
        return Timetable(
                creatorUsername = user.username,
                dateCreated = Calendar.getInstance().timeInMillis,
                weekNumber = 0
        )
    }
}