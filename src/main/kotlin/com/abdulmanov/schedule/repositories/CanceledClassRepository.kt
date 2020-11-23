package com.abdulmanov.schedule.repositories

import com.abdulmanov.schedule.models.CanceledClass
import org.springframework.data.jpa.repository.JpaRepository

interface CanceledClassRepository : JpaRepository<CanceledClass, Int>