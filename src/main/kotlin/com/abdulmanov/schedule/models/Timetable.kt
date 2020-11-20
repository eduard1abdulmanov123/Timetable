package com.abdulmanov.schedule.models

import com.abdulmanov.schedule.models.Timetable.Companion.TABLE_NAME
import javax.persistence.*

@Entity
@Table(name = TABLE_NAME)
data class Timetable @JvmOverloads constructor(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = COLUMN_ID)
        val id: Int = -1,

        @Column(name = COLUMN_CREATOR_USERNAME)
        val creatorUsername: String = "",

        @Column(name = COLUMN_LINK)
        val link: String = "",

        @Column(name = COLUMN_DATE_CREATED)
        val dateCreated: Long = 0,

        @Column(name = COLUMN_WEEK_NUMBER)
        val weekNumber: Int = 0,

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "timetable")
        val multipleClasses: Set<MultipleClass> = hashSetOf(),

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "timetable")
        val oneTimeClasses: Set<OneTimeClass> = hashSetOf()
) {

    companion object{
        const val TABLE_NAME = "timetable"

        const val COLUMN_ID = "timetable_id"
        const val COLUMN_CREATOR_USERNAME = "timetable_creator"
        const val COLUMN_DATE_CREATED = "timetable_date_created"
        const val COLUMN_LINK = "timetable_link"
        const val COLUMN_WEEK_NUMBER = "timetable_week_number"
    }
}