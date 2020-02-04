package ru.shvetsov.myTrello.dataClasses

import com.google.gson.annotations.SerializedName
import ru.shvetsov.myTrello.dataClasses.card.Card

/**
 * Created by Alexander Shvetsov on 28.10.2019
 */
class BoardFromTrelloAPI(
    val id: String,
    val name: String,
    val organization: Organization?,
    @SerializedName("lists")
    val columns: List<Column>,
    @SerializedName("cards")
    val cards: List<Card>,
    val idOrganization: String?
)