package com.abdulmanov.schedule.notifications

import com.abdulmanov.schedule.getTokens
import com.abdulmanov.schedule.models.AppUser
import com.abdulmanov.schedule.models.Timetable
import com.abdulmanov.schedule.repositories.*
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.math.round

@Component
class NotificationSchedules (
        private val notificationService: ServiceNotification,
        private val fcmTokensRepository: FcmTokensRepository,
        private val timetableRepository: TimetableRepository,
        private val multipleClassRepository: MultipleClassRepository,
        private val oneTimeClassRepository: OneTimeClassRepository,
        private val noteRepository: NoteRepository,
        private val userRepository: AppUserRepository
) {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @Scheduled(cron = "0 * * * * *")
    fun checkStartOfClasses(){
        timetableRepository.findAll().forEach {
            checkMultipleClasses(it)
            checkOneTimeClasses(it)
            checkGroupingNotes(it)
        }

        userRepository.findAll().forEach {
            if(it.currentTimetableId != null) {
                val timetable = timetableRepository.findById(it.currentTimetableId)
                checkPrivateNotes(it, timetable.get())
            }
        }
    }

    private fun checkMultipleClasses(timetable: Timetable) {
        val serverDateTimeWithOffsetTenMinutes = getServerDateTimeWithOffsetTenMinutes(timetable)

        val multipleClass = multipleClassRepository.findByTimetable(timetable).find {
            (it.periodicity == 0 || it.periodicity == getTypeWeekForDate(timetable, serverDateTimeWithOffsetTenMinutes))
                    && (it.dayOfWeek == serverDateTimeWithOffsetTenMinutes.dayOfWeek.value)
                    && (serverDateTimeWithOffsetTenMinutes.toString() !in it.canceledClasses.split(";"))
                    && (it.startOfClass == timeFormatter.format(serverDateTimeWithOffsetTenMinutes))
        }

        if(multipleClass != null){
            notificationService.sendNotifications(
                    title = multipleClass.nameSubject,
                    message = "Через десять минут начнется ${multipleClass.typeClass} по ${multipleClass.nameSubject}",
                    tokens = timetable.getTokens(userRepository, fcmTokensRepository)
            )
        }
    }

    private fun checkOneTimeClasses(timetable: Timetable) {
        val serverDateTimeWithOffsetTenMinutes = getServerDateTimeWithOffsetTenMinutes(timetable)

        val oneTimeClass = oneTimeClassRepository.findByTimetable(timetable).find {
            (it.dateOfClass == dateFormatter.format(serverDateTimeWithOffsetTenMinutes))
                    && (it.startOfClass == timeFormatter.format(serverDateTimeWithOffsetTenMinutes))
        }

        if(oneTimeClass != null){
            notificationService.sendNotifications(
                    title = oneTimeClass.nameSubject,
                    message = "Через десять минут начнется ${oneTimeClass.typeClass} по ${oneTimeClass.nameSubject}",
                    tokens = timetable.getTokens(userRepository, fcmTokensRepository)
            )
        }
    }

    private fun checkGroupingNotes(timetable: Timetable){
        val serverDateTimeWithOffsetTenMinutes = getServerDateTimeWithOffsetTenMinutes(timetable)
        val creatorUser = userRepository.findByUsername(timetable.creatorUsername)!!

        val note = noteRepository.findByUser(creatorUser).find {
            it.visibility
                    && (it.date == dateFormatter.format(serverDateTimeWithOffsetTenMinutes))
                    && (it.time == timeFormatter.format(serverDateTimeWithOffsetTenMinutes))
        }

        if(note != null) {
            notificationService.sendNotifications(
                    title = "Напоминание!",
                    message = "Через десять минут заметка (${note.content}) истечет",
                    tokens = timetable.getTokens(userRepository, fcmTokensRepository)
            )
        }
    }

    private fun checkPrivateNotes(user: AppUser, timetable: Timetable){
        val serverDateTimeWithOffsetTenMinutes = getServerDateTimeWithOffsetTenMinutes(timetable)

        val note = noteRepository.findByUser(user).find {
            !it.visibility
                    && (it.date == dateFormatter.format(serverDateTimeWithOffsetTenMinutes))
                    && (it.time == timeFormatter.format(serverDateTimeWithOffsetTenMinutes))
        }

        if(note != null) {
            notificationService.sendNotifications(
                    title = "Напоминание!",
                    message = "Через десять минут заметка (${note.content}) истечет",
                    tokens = listOf(fcmTokensRepository.findById(user.username).get().token)
            )
        }
    }

    private fun getServerDateTimeWithOffsetTenMinutes(timetable: Timetable): OffsetDateTime {
        val timetableUTC = getTimetableUTC(timetable)
        val zoneOffset = ZoneOffset.ofHours(timetableUTC)
        return OffsetDateTime.now(ZoneOffset.UTC)
                .withOffsetSameInstant(zoneOffset)
                .plusMinutes(10)
    }

    private fun getTimetableUTC(timetable: Timetable): Int {
        return timetable.timeZone
                .split(" ")
                .first()
                .substring(4)
                .toInt()
    }

    fun getTypeWeekForDate(timetable: Timetable, date: OffsetDateTime): Int {
        var startTypeWeek = timetable.typeWeek
        var startUpdateDate = LocalDate.parse(timetable.dateUpdate)

        while (round(startUpdateDate.dayOfYear.toDouble()/7) != round(date.dayOfYear.toDouble()/7)){
            startUpdateDate = startUpdateDate.plusWeeks(1)
            startTypeWeek = if(startTypeWeek == 1) 2 else 1
        }

        return startTypeWeek
    }
}