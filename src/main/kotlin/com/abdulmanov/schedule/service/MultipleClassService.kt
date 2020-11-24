package com.abdulmanov.schedule.service

import com.abdulmanov.schedule.checkRightsEditTimetable
import com.abdulmanov.schedule.dto.MultipleClassDto
import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.MultipleClass
import com.abdulmanov.schedule.repositories.CanceledClassRepository
import com.abdulmanov.schedule.repositories.MultipleClassRepository
import com.abdulmanov.schedule.repositories.TimetableRepository
import org.springframework.stereotype.Service

@Service
class MultipleClassService(
        private val timetableService: TimetableService,
        private val timetableRepository: TimetableRepository,
        private val multipleClassRepository: MultipleClassRepository,
        private val canceledClassRepository: CanceledClassRepository
) {

    fun create(user: AppUser, multipleClassDto: MultipleClassDto): MultipleClass {
        if(user.currentTimetableId == null){
            timetableService.create(user)
        }

        val timetable = timetableRepository.findById(user.currentTimetableId!!)

        if(timetable.isEmpty){
            throw Exception("Расписание не существует")
        }

        if(!user.checkRightsEditTimetable(timetable.get())){
            throw Exception("У вас нет прав на редактирование данного расписания")
        }

        val multipleClass = MultipleClass(
                nameSubject = multipleClassDto.nameSubject,
                nameTeacher = multipleClassDto.nameTeacher,
                audience = multipleClassDto.audience,
                typeClass = multipleClassDto.typeClass,
                color = multipleClassDto.color,
                startOfClass = multipleClassDto.startOfClass,
                endOfClass = multipleClassDto.endOfClass,
                dayOfWeek = multipleClassDto.dayOfWeek,
                periodicity = multipleClassDto.periodicity,
                timetable = timetable.get()
        )

        return multipleClassRepository.save(multipleClass)
    }

    fun update(user: AppUser, multipleClassId: Int, multipleClassDto: MultipleClassDto): MultipleClass {
        val multipleClass = multipleClassRepository.findById(multipleClassId)

        if(multipleClass.isEmpty){
            throw Exception("Данного занятия не существует")
        }

        if(!user.checkRightsEditTimetable(multipleClass.get().timetable)){
            throw Exception("У вас нет прав на редактирование данного расписания")
        }

        val updatedMultipleClass = multipleClass.get().copy(
                nameSubject = multipleClassDto.nameSubject,
                nameTeacher = multipleClassDto.nameTeacher,
                audience = multipleClassDto.audience,
                typeClass = multipleClassDto.typeClass,
                color = multipleClassDto.color,
                startOfClass = multipleClassDto.startOfClass,
                endOfClass = multipleClassDto.endOfClass,
                dayOfWeek = multipleClassDto.dayOfWeek,
                periodicity = multipleClassDto.periodicity
        )

        return multipleClassRepository.save(updatedMultipleClass)
    }

    fun delete(user: AppUser, multipleClassId: Int) {
        val multipleClass = multipleClassRepository.findById(multipleClassId)

        if(multipleClass.isEmpty){
            throw Exception("Данного занятия не существует")
        }

        if(!user.checkRightsEditTimetable(multipleClass.get().timetable)){
            throw Exception("У вас нет прав на редактирование данного расписания")
        }

        if(multipleClass.get().canceledClasses.isNotEmpty()) {
            canceledClassRepository.deleteAll(multipleClass.get().canceledClasses)
        }

        multipleClassRepository.delete(multipleClass.get())
    }

}