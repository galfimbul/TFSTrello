package ru.shvetsov.myTrello.dataClasses

import ru.shvetsov.myTrello.dataClasses.card.Card
import java.io.Serializable

/**
 * Created by Alexander Shvetsov on 02.11.2019
 */
data class BoardInfo(
    val id: String,
    val name: String,
    val lists: List<Column>,
    val cards: List<Card>,
    val members: List<User>
) : Serializable