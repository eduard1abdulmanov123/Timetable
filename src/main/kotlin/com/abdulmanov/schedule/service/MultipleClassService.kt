package com.abdulmanov.schedule.service

import com.abdulmanov.schedule.*
import com.abdulmanov.schedule.dto.MultipleClassDto
import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.MultipleClass
import com.abdulmanov.schedule.repositories.AppUserRepository
import com.abdulmanov.schedule.repositories.FcmTokensRepository
import com.abdulmanov.schedule.repositories.MultipleClassRepository
import com.abdulmanov.schedule.repositories.TimetableRepository
import org.springframework.stereotype.Service
import java.lang.StringBuilder

@Service
class MultipleClassService(
        private val serviceNotification: ServiceNotification,
        private val timetableRepository: TimetableRepository,
        private val multipleClassRepository: MultipleClassRepository,
        private val userRepository: AppUserRepository,
        private val fcmTokensRepository: FcmTokensRepository
) {

    fun create(user: AppUser, multipleClassDto: MultipleClassDto): MultipleClass {
        val timetable = timetableRepository.findById(user.currentTimetableId!!)

        when {
            timetable.isEmpty -> {
                throw Exception("Расписание не существует!")
            }
            !user.checkRightsEditTimetable(timetable.get()) -> {
                throw Exception("У вас нет прав на редактирование данного расписания!")
            }
            !multipleClassDto.isAllFieldsNotEmpty() -> {
                throw Exception("Не все поля заполнены!")
            }
        }

        val multipleClass = MultipleClass(
                nameSubject = multipleClassDto.nameSubject,
                nameTeacher = multipleClassDto.nameTeacher,
                audience = multipleClassDto.audience,
                typeClass = multipleClassDto.typeClass,
                startOfClass = multipleClassDto.startOfClass,
                endOfClass = multipleClassDto.endOfClass,
                dayOfWeek = multipleClassDto.dayOfWeek,
                periodicity = multipleClassDto.periodicity,
                canceledClasses = multipleClassDto.canceledClasses,
                timetable = timetable.get()
        )

        return multipleClassRepository.save(multipleClass).apply {
            serviceNotification.sendNotifications(
                    title = "Новое занятие",
                    message = "Название: $nameSubject($typeClass)\n" +
                            "Преподаватель: $nameTeacher\n" +
                            "Aудитория: $audience\n" +
                            "День недели: ${getFullTitleDayOfWeekForNumber(dayOfWeek)}\n" +
                            "Время: $startOfClass - $endOfClass",
                    tokens = timetable.get().getTokens(userRepository,fcmTokensRepository)
            )
        }
    }

    fun update(user: AppUser, multipleClassId: Int, multipleClassDto: MultipleClassDto): MultipleClass {
        val timetable = timetableRepository.findById(user.currentTimetableId!!)
        val multipleClass = multipleClassRepository.findById(multipleClassId)

        when {
            multipleClass.isEmpty -> {
                throw Exception("Данного занятия не существует!")
            }
            !user.checkRightsEditTimetable(multipleClass.get().timetable) -> {
                throw Exception("У вас нет прав на редактирование данного расписания!")
            }
            !multipleClassDto.isAllFieldsNotEmpty() -> {
                throw Exception("Не все поля заполнены!")
            }
        }

        val oldMultipleClass = multipleClass.get()

        val updatedMultipleClass = multipleClass.get().copy(
                nameSubject = multipleClassDto.nameSubject,
                nameTeacher = multipleClassDto.nameTeacher,
                audience = multipleClassDto.audience,
                typeClass = multipleClassDto.typeClass,
                startOfClass = multipleClassDto.startOfClass,
                endOfClass = multipleClassDto.endOfClass,
                dayOfWeek = multipleClassDto.dayOfWeek,
                periodicity = multipleClassDto.periodicity,
                canceledClasses = multipleClassDto.canceledClasses
        )

        val message = StringBuilder().run {
            if(oldMultipleClass.audience != updatedMultipleClass.audience){
                append("Изменилась аудитория занятия: ${updatedMultipleClass.audience}\n")
            }
            if(oldMultipleClass.startOfClass != updatedMultipleClass.startOfClass || oldMultipleClass.endOfClass != updatedMultipleClass.endOfClass){
                append("Изменилось время занятия: ${updatedMultipleClass.startOfClass} - ${updatedMultipleClass.endOfClass}\n")
            }
            if(oldMultipleClass.dayOfWeek != updatedMultipleClass.dayOfWeek){
                append("Изменился день недели занятия: ${getFullTitleDayOfWeekForNumber(updatedMultipleClass.dayOfWeek)}\n")
            }
            if(oldMultipleClass.canceledClasses != updatedMultipleClass.canceledClasses){
                append("${getMessageForCanceledClasses(oldMultipleClass.canceledClasses, updatedMultipleClass.canceledClasses)}\n")
            }
            toString()
        }

        return multipleClassRepository.save(updatedMultipleClass).apply {
            serviceNotification.sendNotifications(
                    title = "Изменения в занятии",
                    message = "Название: $nameSubject($typeClass)\n$message",
                    tokens = timetable.get().getTokens(userRepository,fcmTokensRepository)
            )
        }
    }

    fun delete(user: AppUser, multipleClassId: Int) {
        val timetable = timetableRepository.findById(user.currentTimetableId!!)
        val multipleClass = multipleClassRepository.findById(multipleClassId)

        when {
            multipleClass.isEmpty -> {
                throw Exception("Данного занятия не существует")
            }
            !user.checkRightsEditTimetable(multipleClass.get().timetable) -> {
                throw Exception("У вас нет прав на редактирование данного расписания")
            }
        }

        multipleClassRepository.delete(multipleClass.get())

        serviceNotification.sendNotifications(
                title = "Удалено занятие",
                message = "Название: ${multipleClass.get().nameSubject}(${multipleClass.get().typeClass})",
                tokens = timetable.get().getTokens(userRepository,fcmTokensRepository)
        )
    }

    fun getMessageForCanceledClasses(oldCanceledClassesStr: String, newCanceledClassesStr: String): String {
        val oldCanceledClasses = oldCanceledClassesStr.split(";")
        val newCanceledClasses = newCanceledClassesStr.split(";")

        val addedCanceledClasses = mutableListOf<String>()
        val deletedCanceledClasses = mutableListOf<String>()

        oldCanceledClasses.forEach {
            if (it !in newCanceledClasses && it.isNotEmpty()) {
                deletedCanceledClasses.add(it)
            }
        }

        newCanceledClasses.forEach {
            if (it !in oldCanceledClasses && it.isNotEmpty()) {
                addedCanceledClasses.add(it)
            }
        }

        val sb = StringBuilder()

        if (addedCanceledClasses.isNotEmpty()) {
            sb.append("Были отменены следующие даты занятия:\n")
            addedCanceledClasses.forEach {
                sb.append("${getFormattedDate(it)}\n")
            }
        }

        if (deletedCanceledClasses.isNotEmpty()) {
            sb.append("Были возвращены следующие даты занятий:\n")
            deletedCanceledClasses.forEach {
                sb.append("${getFormattedDate(it)}\n")
            }
        }

        return sb.toString()
    }
}