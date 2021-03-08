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
        var link: String = "",

        @Column(name = COLUMN_DATE_UPDATE)
        val dateUpdate: String = "",

        @Column(name = COLUMN_TYPE_WEEK)
        val typeWeek: Int = 0,

        @Column(name = COLUMN_TIME_ZONE)
        val timeZone: String = "",

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "timetable")
        val multipleClasses: List<MultipleClass> = listOf(),

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "timetable")
        val oneTimeClasses: List<OneTimeClass> = listOf()
) {

    companion object{
        const val TABLE_NAME = "timetable"

        const val COLUMN_ID = "timetable_id"
        const val COLUMN_CREATOR_USERNAME = "timetable_creator"
        const val COLUMN_LINK = "timetable_link"
        const val COLUMN_DATE_UPDATE = "timetable_date_update"
        const val COLUMN_TYPE_WEEK = "timetable_type_week"
        const val COLUMN_TIME_ZONE = "timetable_time_zone"
    }
}