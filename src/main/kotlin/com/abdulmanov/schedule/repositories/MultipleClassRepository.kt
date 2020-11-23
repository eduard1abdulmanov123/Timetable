package com.abdulmanov.schedule.repositories

import com.abdulmanov.schedule.models.MultipleClass
import com.abdulmanov.schedule.models.Timetable
import org.springframework.data.jpa.repository.JpaRepository

interface MultipleClassRepository : JpaRepository<MultipleClass, Int> {

    fun findByTimetable(timetable: Timetable): List<MultipleClass>
}