package com.abdulmanov.schedule.repositories

import com.abdulmanov.schedule.models.Timetable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TimetableRepository: JpaRepository<Timetable, Int>