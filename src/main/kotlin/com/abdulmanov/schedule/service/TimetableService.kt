package com.abdulmanov.schedule.service

import com.abdulmanov.schedule.dto.TimetableInfoDto
import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.Timetable
import com.abdulmanov.schedule.repositories.*
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

    fun create(user: AppUser, timetableInfoDto: TimetableInfoDto? = null): Timetable{
        val defaultTimetable = createDefaultTimetable(user, timetableInfoDto)

        val createdTimetable = timetableRepository.save(defaultTimetable).run {
            val updatedTimetable = copy(link = "/api/v1/timetable/join/${id}")
            timetableRepository.save(updatedTimetable)
        }

        determineFateOldTimetable(user, createdTimetable)

        return createdTimetable
    }

    fun join(user: AppUser, timetableId: Int): Timetable {
        val timetable = timetableRepository.findById(timetableId)

        if(timetable.isEmpty){
            throw Exception(USER_IS_NOT_CONNECT)
        }

        determineFateOldTimetable(user, timetable.get())

        return timetable.get()
    }

    fun changeTypeWeek(user: AppUser, timetableInfoDto: TimetableInfoDto): Timetable{
        if(user.currentTimetableId == null){
            throw Exception(USER_IS_NOT_CONNECT)
        }

        val timetable = timetableRepository.findById(user.currentTimetableId)

        if(timetable.get().creatorUsername != user.username){
            throw Exception(NO_ACCESS_TO_TIMETABLE)
        }

        val updateTimetable = timetable.get().copy(
                typeWeek = timetableInfoDto.typeWeek,
                dateUpdate = Calendar.getInstance().timeInMillis
        )

        return timetableRepository.save(updateTimetable)
    }

    fun get(user: AppUser):Timetable {
        if(user.currentTimetableId == null){
            throw Exception(USER_IS_NOT_CONNECT)
        }

        val timetable = timetableRepository.findById(user.currentTimetableId)

        if(timetable.isEmpty){
            throw Exception(EMPTY_TIMETABLE_ERROR)
        }

        return timetable.get()
    }

    private fun determineFateOldTimetable(user: AppUser, timetable: Timetable){
        when {
            user.currentTimetableId == null -> {
                attachUserToTimetable(user, timetable)
            }
            user.currentTimetableId != timetable.id -> {
                val userTimetable = timetableRepository.findById(user.currentTimetableId)

                if(user.username == userTimetable.get().creatorUsername){
                    oneTimeClassRepository.deleteAll(userTimetable.get().oneTimeClasses)
                    userTimetable.get().multipleClasses.forEach {
                        canceledClassRepository.deleteAll(it.canceledClasses)
                    }
                    multipleClassRepository.deleteAll(userTimetable.get().multipleClasses)
                    timetableRepository.delete(userTimetable.get())

                    clearBindingToTimetable(userTimetable.get())
                }

                attachUserToTimetable(user, timetable)
            }
        }
    }

    private fun clearBindingToTimetable(timetable: Timetable){
        val updatedUsers = appUserRepository.findByCurrentTimetableId(timetable.id)
                .map {it.copy(currentTimetableId = null)}

        appUserRepository.saveAll(updatedUsers)
    }

    private fun attachUserToTimetable(user:AppUser, timetable: Timetable){
        val updatedUser = user.copy(currentTimetableId = timetable.id)
        appUserRepository.save(updatedUser)
    }

    private fun createDefaultTimetable(user: AppUser, timetableInfoDto: TimetableInfoDto?): Timetable{
        return Timetable(
                creatorUsername = user.username,
                dateUpdate = Calendar.getInstance().timeInMillis,
                typeWeek = timetableInfoDto?.typeWeek ?: 1
        )
    }

    companion object{
        const val NO_ACCESS_TO_TIMETABLE = "Нет доступа к данному расписанию"
        const val USER_IS_NOT_CONNECT = "Данный пользователь не подключен к расписанию"
        const val EMPTY_TIMETABLE_ERROR = "Данное расписание не создано или удалено"
    }
}