package com.abdulmanov.schedule.models

import com.abdulmanov.schedule.models.Note.Companion.TABLE_NAME
import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = TABLE_NAME)
data class Note @JvmOverloads constructor(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = COLUMN_ID)
        val id: Int = -1,

        @Column(name = COLUMN_CONTENT)
        val content: String = "",

        @Column(name = COLUMN_DATE)
        val date: Long = 0,

        @Column(name = COLUMN_VISIBILITY)
        val visibility: Boolean = false,

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = AppUser.COLUMN_USERNAME, nullable = false)
        val user: AppUser
) {

    companion object{
        const val TABLE_NAME = "note"

        const val COLUMN_ID = "note_id"
        const val COLUMN_CONTENT = "note_content"
        const val COLUMN_DATE = "note_date"
        const val COLUMN_VISIBILITY = "note_visibility"
    }
}