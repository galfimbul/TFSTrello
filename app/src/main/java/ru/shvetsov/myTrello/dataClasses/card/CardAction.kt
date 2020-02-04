package ru.shvetsov.myTrello.dataClasses.card

import ru.shvetsov.myTrello.dataClasses.User
import java.util.*

data class CardAction(
    val id: String,
    val idMemberCreator: String,
    val type: String,
    val memberCreator: User,
    val date: Date,
    val display: CardDisplay
)
