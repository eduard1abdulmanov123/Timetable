package com.abdulmanov.schedule.service

import com.abdulmanov.schedule.checkRightsEditTimetable
import com.abdulmanov.schedule.dto.MultipleClassDto
import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.MultipleClass
import com.abdulmanov.schedule.repositories.MultipleClassRepository
import com.abdulmanov.schedule.repositories.TimetableRepository
import org.springframework.stereotype.Service

@Service
class MultipleClassService(
        private val timetableRepository: TimetableRepository,
        private val multipleClassRepository: MultipleClassRepository
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

        return multipleClassRepository.save(multipleClass)
    }

    fun update(user: AppUser, multipleClassId: Int, multipleClassDto: MultipleClassDto): MultipleClass {
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

        return multipleClassRepository.save(updatedMultipleClass)
    }

    fun delete(user: AppUser, multipleClassId: Int) {
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
    }

}