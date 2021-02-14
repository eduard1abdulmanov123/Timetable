package com.abdulmanov.schedule.service

import com.abdulmanov.schedule.checkRightsEditTimetable
import com.abdulmanov.schedule.dto.OneTimeClassDto
import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.OneTimeClass
import com.abdulmanov.schedule.repositories.OneTimeClassRepository
import com.abdulmanov.schedule.repositories.TimetableRepository
import org.springframework.stereotype.Service

@Service
class OneTimeClassService(
        private val timetableRepository: TimetableRepository,
        private val oneTimeClassRepository: OneTimeClassRepository
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

        return oneTimeClassRepository.save(oneTimeClass)
    }

    fun update(user: AppUser, oneTimeClassId: Int, oneTimeClassDto: OneTimeClassDto): OneTimeClass {
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

        val updatedOneTimeClass = oneTimeClass.get().copy(
                nameSubject = oneTimeClassDto.nameSubject,
                nameTeacher = oneTimeClassDto.nameTeacher,
                audience = oneTimeClassDto.audience,
                typeClass = oneTimeClassDto.typeClass,
                startOfClass = oneTimeClassDto.startOfClass,
                endOfClass = oneTimeClassDto.endOfClass,
                dateOfClass = oneTimeClassDto.dateOfClass
        )

        return oneTimeClassRepository.save(updatedOneTimeClass)
    }

    fun delete(user: AppUser, oneTimeClassId: Int) {
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
    }
}