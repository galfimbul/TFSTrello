package ru.shvetsov.myTrello.dataClasses.card

import ru.shvetsov.myTrello.dataClasses.BoardFromTrelloAPI
import ru.shvetsov.myTrello.dataClasses.Column
import ru.shvetsov.myTrello.dataClasses.User
import java.io.Serializable

data class Card(
    val id: String,
    val name: String,
    val pos: String,
    val idList: String,
    val desc: String,
    val idMembers: List<String>,
    val attachments: List<CardAttachments>,
    val members: MutableList<User>,
    val board: BoardFromTrelloAPI,
    val list: Column,
    var color: Int,
    var uniqIdForAdapter: Long
) : Serializable