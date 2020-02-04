package ru.shvetsov.myTrello.dataClasses.card

import java.io.Serializable
import java.util.*

/**
 * Created by Alexander Shvetsov on 28.11.2019
 */
data class CardAttachments(
    val date: Date,
    val name: String,
    val previews: List<Preview>,
    val url: String,
    val id: String
) : Serializable

data class Preview(
    val id: String,
    val url: String,
    val height: Int,
    val width: Int
) : Serializable
