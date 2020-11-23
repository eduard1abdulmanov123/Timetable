package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.dto.MultipleClassDto
import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.MultipleClass
import com.abdulmanov.schedule.models.Timetable
import com.abdulmanov.schedule.repositories.AppUserRepository
import com.abdulmanov.schedule.repositories.MultipleClassRepository
import com.abdulmanov.schedule.repositories.TimetableRepository
import com.abdulmanov.schedule.security.jwt.JwtTokenProvider
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/multipleclass")
class MultipleClassControllerV1(
        private val appUserRepository: AppUserRepository,
        private val timetableRepository: TimetableRepository,
        private val multipleClassRepository: MultipleClassRepository,
        private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/create")
    fun create(request: HttpServletRequest, @RequestBody multipleClassDto: MultipleClassDto): ResponseEntity<Any> {
        val user = jwtTokenProvider.getUser(request)
        val timetable = getTimetable(user)

        val multipleClass = mapMultipleClassDtoToDatabaseModel(timetable, multipleClassDto)
        val createdMultipleClass = multipleClassRepository.save(multipleClass)

        return ResponseEntity.ok(createdMultipleClass)
    }

    @PostMapping("/update/{id}")
    fun update(@RequestBody multipleClassDto: MultipleClassDto, @PathVariable("id") multipleClassId: Int): ResponseEntity<Any> {
        val oldMultipleClass = multipleClassRepository.findById(multipleClassId)

        if(oldMultipleClass.isEmpty){
            val body = hashMapOf("status" to "error", "message" to "OldMultipleClass $multipleClassId does not exists")
            return ResponseEntity.badRequest().body(body)
        }

        val newMultipleClass = oldMultipleClass.get().copy(
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

        val modifiedMultipleClass = multipleClassRepository.save(newMultipleClass)
        return ResponseEntity.ok(modifiedMultipleClass)
    }

    @PostMapping("/delete/{id}")
    fun delete(request: HttpServletRequest, @PathVariable("id") multipleClassId: Int): ResponseEntity<Any> {
        val oldMultipleClass = multipleClassRepository.findById(multipleClassId)

        if(oldMultipleClass.isEmpty){
            val body = hashMapOf("status" to "error", "message" to "MultipleClass $multipleClassId does not exists")
            return ResponseEntity.badRequest().body(body)
        }

        multipleClassRepository.deleteById(multipleClassId)

        return ResponseEntity.ok(hashMapOf("status" to "success"))
    }

    @GetMapping("/")
    fun get(request: HttpServletRequest): ResponseEntity<Any> {
        val user = jwtTokenProvider.getUser(request)
        val timetable = timetableRepository.findById(user.currentTimetableId!!).get()
        val multipleClasses = multipleClassRepository.findByTimetable(timetable)
        return ResponseEntity.ok(multipleClasses)
    }

    private fun getTimetable(user: AppUser): Timetable {
        return if(user.currentTimetableId == null) {
            val timetable = Timetable(creatorUsername = user.username, dateCreated = Calendar.getInstance().timeInMillis, weekNumber = 0)
            val createdTimetable = timetableRepository.save(timetable)

            val newUser = user.copy(currentTimetableId = createdTimetable.id)
            appUserRepository.save(newUser)

            createdTimetable
        }else{
            timetableRepository.findById(user.currentTimetableId).get()
        }
    }

    private fun mapMultipleClassDtoToDatabaseModel(timetable: Timetable, multipleClassDto:MultipleClassDto): MultipleClass {
        return MultipleClass(
                nameSubject = multipleClassDto.nameSubject,
                nameTeacher = multipleClassDto.nameTeacher,
                audience = multipleClassDto.audience,
                typeClass = multipleClassDto.typeClass,
                color = multipleClassDto.color,
                startOfClass = multipleClassDto.startOfClass,
                endOfClass = multipleClassDto.endOfClass,
                dayOfWeek = multipleClassDto.dayOfWeek,
                periodicity = multipleClassDto.periodicity,
                timetable = timetable
        )
    }
}