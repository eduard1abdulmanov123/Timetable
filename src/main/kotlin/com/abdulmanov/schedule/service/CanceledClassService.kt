package com.abdulmanov.schedule.service

import com.abdulmanov.schedule.checkRightsEditTimetable
import com.abdulmanov.schedule.dto.CanceledClassDto
import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.CanceledClass
import com.abdulmanov.schedule.repositories.CanceledClassRepository
import com.abdulmanov.schedule.repositories.MultipleClassRepository
import org.springframework.stereotype.Service

@Service
class CanceledClassService(
        private val multipleClassRepository: MultipleClassRepository,
        private val canceledClassRepository: CanceledClassRepository
) {

    fun create(user: AppUser, multipleClassId: Int, canceledClassDto: CanceledClassDto): CanceledClass{
        val multipleClass = multipleClassRepository.findById(multipleClassId)

        if(multipleClass.isEmpty){
            throw Exception("Не существует такого повторяющегося задания")
        }

        if(!user.checkRightsEditTimetable(multipleClass.get().timetable)){
            throw Exception("У вас нет прав на редактирование данного расписания")
        }

        val canceledClass = CanceledClass(
                date = canceledClassDto.date,
                multipleClass = multipleClass.get()
        )

        return canceledClassRepository.save(canceledClass)
    }

    fun delete(user: AppUser, canceledClassedId: Int){
        val canceledClass = canceledClassRepository.findById(canceledClassedId)
        val timetable = canceledClass.get().multipleClass.timetable

        if(!user.checkRightsEditTimetable(timetable)){
            throw Exception("У вас нет прав на редактирование данного расписания")
        }

        if(canceledClass.isEmpty){
            throw Exception("Отмененная дата занятия не существует")
        }

        canceledClassRepository.deleteById(canceledClassedId)
    }
}