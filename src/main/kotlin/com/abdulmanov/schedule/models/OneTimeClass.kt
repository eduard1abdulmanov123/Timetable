package com.abdulmanov.schedule.models

import com.abdulmanov.schedule.models.OneTimeClass.Companion.TABLE_NAME
import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = TABLE_NAME)
data class OneTimeClass @JvmOverloads constructor(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = COLUMN_ID)
        val id: Int = -1,

        @Column(name = COLUMN_NAME_SUBJECT)
        val nameSubject: String = "",

        @Column(name = COLUMN_NAME_TEACHER)
        val nameTeacher: String = "",

        @Column(name = COLUMN_AUDIENCE)
        val audience: String = "",

        @Column(name = COLUMN_TYPE_CLASS)
        val typeClass: String = "",

        @Column(name = COLUMN_START_OF_CLASS)
        val startOfClass: String = "",

        @Column(name = COLUMN_END_OF_CLASS)
        val endOfClass: String = "",

        @Column(name = COLUMN_DATE_OF_CLASS)
        val dateOfClass: String = "",

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = Timetable.COLUMN_ID, nullable = false)
        val timetable: Timetable
) {

    companion object{
        const val TABLE_NAME = "one_time_class"

        const val COLUMN_ID = "id"
        const val COLUMN_NAME_SUBJECT = "name_subject"
        const val COLUMN_NAME_TEACHER = "name_teacher"
        const val COLUMN_AUDIENCE = "audience"
        const val COLUMN_TYPE_CLASS = "type_class"
        const val COLUMN_START_OF_CLASS = "start_of_class"
        const val COLUMN_END_OF_CLASS = "end_of_class"
        const val COLUMN_DATE_OF_CLASS = "date_of_class"
    }
}