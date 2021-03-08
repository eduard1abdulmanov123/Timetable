package com.abdulmanov.schedule.service

import com.abdulmanov.schedule.checkRightsEditTimetable
import com.abdulmanov.schedule.dto.OneTimeClassDto
import com.abdulmanov.schedule.getFormattedDate
import com.abdulmanov.schedule.getTokens
import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.OneTimeClass
import com.abdulmanov.schedule.notifications.ServiceNotification
import com.abdulmanov.schedule.repositories.AppUserRepository
import com.abdulmanov.schedule.repositories.FcmTokensRepository
import com.abdulmanov.schedule.repositories.OneTimeClassRepository
import com.abdulmanov.schedule.repositories.TimetableRepository
import org.springframework.stereotype.Service
import java.lang.StringBuilder

@Service
class OneTimeClassService(
        private val serviceNotification: ServiceNotification,
        private val timetableRepository: TimetableRepository,
        private val userRepository: AppUserRepository,
        private val oneTimeClassRepository: OneTimeClassRepository,
        private val fcmTokensRepository: FcmTokensRepository
) {

    fun create(user: AppUser, oneTimeClassDto: OneTimeClassDto): OneTimeClass {
        val timetable = timetableRepository.findById(user.currentTimetableId!!)

        when {
            timetable.isEmpty -> {
                throw Exception("Расписание не существует")
            }
            !user.checkRightsEditTimetable(timetable.get()) -> {
                throw Exception("У вас нет прав на редактирование данного расписания")
            }
            !oneTimeClassDto.isAllFieldsNotEmpty() -> {
                throw Exception("Не все поля заполнены!")
            }
        }

        val oneTimeClass = OneTimeClass(
                nameSubject = oneTimeClassDto.nameSubject,
                nameTeacher = oneTimeClassDto.nameTeacher,
                audience = oneTimeClassDto.audience,
                typeClass = oneTimeClassDto.typeClass,
                startOfClass = oneTimeClassDto.startOfClass,
                endOfClass = oneTimeClassDto.endOfClass,
                dateOfClass = oneTimeClassDto.dateOfClass,
                timetable = timetable.get()
        )

        return oneTimeClassRepository.save(oneTimeClass).apply{
            serviceNotification.sendNotifications(
                    title = "Новое занятие",
                    message = "Название: $nameSubject($typeClass)\n" +
                            "Преподаватель: $nameTeacher\n" +
                            "Aудитория: $audience\n" +
                            "Дата занятия: ${getFormattedDate(dateOfClass)}\n" +
                            "Время: $startOfClass - $endOfClass",
                    tokens = timetable.get().getTokens(userRepository,fcmTokensRepository)
            )
        }
    }

    fun update(user: AppUser, oneTimeClassId: Int, oneTimeClassDto: OneTimeClassDto): OneTimeClass {
        val timetable = timetableRepository.findById(user.currentTimetableId!!)
        val oneTimeClass = oneTimeClassRepository.findById(oneTimeClassId)

        when {
            oneTimeClass.isEmpty -> {
                throw Exception("Данного занятия не существует")
            }
            !user.checkRightsEditTimetable(oneTimeClass.get().timetable) -> {
                throw Exception("У вас нет прав на редактирование данного расписания")
            }
            !oneTimeClassDto.isAllFieldsNotEmpty() -> {
                throw Exception("Не все поля заполнены!")
            }
        }

        val oldOneTimeClass = oneTimeClass.get()

        val updatedOneTimeClass = oneTimeClass.get().copy(
                nameSubject = oneTimeClassDto.nameSubject,
                nameTeacher = oneTimeClassDto.nameTeacher,
                audience = oneTimeClassDto.audience,
                typeClass = oneTimeClassDto.typeClass,
                startOfClass = oneTimeClassDto.startOfClass,
                endOfClass = oneTimeClassDto.endOfClass,
                dateOfClass = oneTimeClassDto.dateOfClass
        )

        val message = StringBuilder().run {
            if(oldOneTimeClass.audience != updatedOneTimeClass.audience){
                append("Изменилась аудитория занятия: ${updatedOneTimeClass.audience}\n")
            }
            if(oldOneTimeClass.startOfClass != updatedOneTimeClass.startOfClass || oldOneTimeClass.endOfClass != updatedOneTimeClass.endOfClass){
                append("Изменилось время занятия: ${updatedOneTimeClass.startOfClass} - ${updatedOneTimeClass.endOfClass}\n")
            }
            if(oldOneTimeClass.dateOfClass != updatedOneTimeClass.dateOfClass){
                append("Изменилась дата занятия: ${getFormattedDate(updatedOneTimeClass.dateOfClass)}\n")
            }
            toString()
        }

        return oneTimeClassRepository.save(updatedOneTimeClass).apply {
            serviceNotification.sendNotifications(
                    title = "Изменения в занятии$nameSubject($typeClass)",
                    message = "Название:$nameSubject($typeClass)\n$message",
                    tokens = timetable.get().getTokens(userRepository,fcmTokensRepository)
            )
        }
    }

    fun delete(user: AppUser, oneTimeClassId: Int) {
        val timetable = timetableRepository.findById(user.currentTimetableId!!)
        val oneTimeClass = oneTimeClassRepository.findById(oneTimeClassId)

        when {
            oneTimeClass.isEmpty -> {
                throw Exception("Данного занятия не существует")
            }
            !user.checkRightsEditTimetable(oneTimeClass.get().timetable) -> {
                throw Exception("У вас нет прав на редактирование данного расписания")
            }
        }

        oneTimeClassRepository.delete(oneTimeClass.get())

        serviceNotification.sendNotifications(
                title = "Удалено занятие",
                message = "Название: ${oneTimeClass.get().nameSubject}(${oneTimeClass.get().typeClass})",
                tokens = timetable.get().getTokens(userRepository,fcmTokensRepository)
        )
    }
}