package com.abdulmanov.schedule.models

import com.abdulmanov.schedule.models.CanceledClass.Companion.TABLE_NAME
import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = TABLE_NAME)
data class CanceledClass @JvmOverloads constructor(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = COLUMN_ID)
        val id: Int = -1,

        @Column(name = COLUMN_DATE)
        val date: Long = -1,

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = MultipleClass.COLUMN_ID, nullable = false)
        val multipleClass: MultipleClass
){

    companion object{
        const val TABLE_NAME = "canceled_class"

        const val COLUMN_ID = "canceled_class_id"
        const val COLUMN_DATE = "date"
    }
}