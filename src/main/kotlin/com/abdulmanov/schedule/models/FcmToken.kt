package com.abdulmanov.schedule.models

import com.abdulmanov.schedule.models.FcmToken.Companion.TABLE_NAME
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = TABLE_NAME)
data class FcmToken(
        @Id
        @Column(name = COLUMN_USERNAME)
        val username: String = "",

        @Column(name = COLUMN_TOKEN)
        val token: String = ""
) {
    companion object{
        const val TABLE_NAME = "fcm_tokens"

        const val COLUMN_USERNAME = "username"
        const val COLUMN_TOKEN = "token"
    }
}