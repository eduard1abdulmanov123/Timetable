package com.abdulmanov.schedule.models

import com.abdulmanov.schedule.models.AppUser.Companion.TABLE_NAME
import javax.persistence.*

@Entity
@Table(name = TABLE_NAME)
data class AppUser @JvmOverloads constructor(
        @Id
        @Column(name = COLUMN_USERNAME)
        val username: String = "",

        @Column(name = COLUMN_PASSWORD)
        val password: String = "",

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
        val notes: Set<Note> = hashSetOf()
){

    companion object{
        const val TABLE_NAME = "app_users"

        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
    }
}