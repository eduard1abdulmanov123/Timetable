package com.abdulmanov.schedule.controllers

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/schedule")
class ScheduleControllerV1 {

    @RequestMapping("/")
    fun getSchedule():String{
        return "dasdadadadadadadad"
    }
}