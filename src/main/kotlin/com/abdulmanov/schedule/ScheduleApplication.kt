package com.abdulmanov.schedule

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import java.io.FileInputStream


@SpringBootApplication
@EnableScheduling
@EnableAsync
class ScheduleApplication

fun main(args: Array<String>) {
	val serviceAccount = FileInputStream("src/main/resources/timetable-b260b-firebase-adminsdk-5rle7-7d150ffa2b.json")

	val options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(serviceAccount))
			.build()

	FirebaseApp.initializeApp(options)

	runApplication<ScheduleApplication>(*args)
}
