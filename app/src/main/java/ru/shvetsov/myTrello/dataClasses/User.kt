package ru.shvetsov.myTrello.dataClasses

import java.io.Serializable

data class User(
    val id: String,
    val avatarHash: String?,
    val avatarSource: String?,
    val fullName: String?,
    val initials: String,
    val username: String
) : Serializable