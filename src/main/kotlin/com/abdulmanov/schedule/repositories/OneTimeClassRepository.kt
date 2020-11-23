package com.abdulmanov.schedule.repositories

import com.abdulmanov.schedule.models.OneTimeClass
import com.abdulmanov.schedule.models.Timetable
import org.springframework.data.jpa.repository.JpaRepository

interface OneTimeClassRepository : JpaRepository<OneTimeClass, Int>{

    fun findByTimetable(timetable: Timetable): List<OneTimeClass>
}