package com.abdulmanov.schedule.controllers

import com.abdulmanov.schedule.dto.OneTimeClassDto
import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.OneTimeClass
import com.abdulmanov.schedule.models.Timetable
import com.abdulmanov.schedule.repositories.AppUserRepository
import com.abdulmanov.schedule.repositories.OneTimeClassRepository
import com.abdulmanov.schedule.repositories.TimetableRepository
import com.abdulmanov.schedule.security.jwt.JwtTokenProvider
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1/onetimeclass")
class OneTimeClassControllerV1(
        private val appUserRepository: AppUserRepository,
        private val timetableRepository: TimetableRepository,
        private val oneTimeClassRepository: OneTimeClassRepository,
        private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/create")
    fun create(request: HttpServletRequest, @RequestBody oneTimeClassDto:OneTimeClassDto): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)
        val timetable = getTimetable(user)

        val oneTimeClass = mapOneTimeClassDtoToDatabaseModel(timetable, oneTimeClassDto)
        val createdOneTimeClass = oneTimeClassRepository.save(oneTimeClass)

        return ResponseEntity.ok(createdOneTimeClass)
    }

    @PostMapping("/update/{id}")
    fun update(@RequestBody oneTimeClassDto:OneTimeClassDto, @PathVariable("id") oneTimeClassId: Int): ResponseEntity<Any>{
        val oldOneTimeClass = oneTimeClassRepository.findById(oneTimeClassId)

        if(oldOneTimeClass.isEmpty){
            val body = hashMapOf("status" to "error", "message" to "OneTimeClass $oneTimeClassId does not exists")
            return ResponseEntity.badRequest().body(body)
        }

        val newOneTimeClass = oldOneTimeClass.get().copy(
                nameSubject = oneTimeClassDto.nameSubject,
                nameTeacher = oneTimeClassDto.nameTeacher,
                audience = oneTimeClassDto.audience,
                typeClass = oneTimeClassDto.typeClass,
                color = oneTimeClassDto.color,
                startOfClass = oneTimeClassDto.startOfClass,
                endOfClass = oneTimeClassDto.endOfClass,
                dateOfClass = oneTimeClassDto.dateOfClass
        )

        val modifiedOneTimeClass = oneTimeClassRepository.save(newOneTimeClass)
        return ResponseEntity.ok(modifiedOneTimeClass)
    }

    @PostMapping("/delete/{id}")
    fun delete(request: HttpServletRequest, @PathVariable("id") oneTimeClassId: Int): ResponseEntity<Any>{
        val oldOneTimeClass = oneTimeClassRepository.findById(oneTimeClassId)

        if(oldOneTimeClass.isEmpty){
            val body = hashMapOf("status" to "error", "message" to "OneTimeClass $oneTimeClassId does not exists")
            return ResponseEntity.badRequest().body(body)
        }

        oneTimeClassRepository.deleteById(oneTimeClassId)

        return ResponseEntity.ok(hashMapOf("status" to "success"))
    }

    @GetMapping("/")
    fun get(request: HttpServletRequest): ResponseEntity<Any>{
        val user = jwtTokenProvider.getUser(request)
        val timetable = timetableRepository.findById(user.currentTimetableId!!).get()
        val oneTimeClasses = oneTimeClassRepository.findByTimetable(timetable)
        return ResponseEntity.ok(oneTimeClasses)
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

    private fun mapOneTimeClassDtoToDatabaseModel(timetable: Timetable, oneTimeClassDto: OneTimeClassDto): OneTimeClass{
        return OneTimeClass(
                nameSubject = oneTimeClassDto.nameSubject,
                nameTeacher = oneTimeClassDto.nameTeacher,
                audience = oneTimeClassDto.audience,
                typeClass = oneTimeClassDto.typeClass,
                color = oneTimeClassDto.color,
                startOfClass = oneTimeClassDto.startOfClass,
                endOfClass = oneTimeClassDto.endOfClass,
                dateOfClass = oneTimeClassDto.dateOfClass,
                timetable = timetable
        )
    }
}