package ru.shvetsov.myTrello.dataClasses

import java.io.Serializable

/**
 * Created by Alexander Shvetsov on 10.10.2019
 */

/**
 * содержит параметры доски
 */
data class Board(
    val id: String,
    val boardName: String? = null,
    val columns: List<Column>?,
    val color: Int? = null,
    var category: String,
    val categoryId: String?,
    val viewType: Int
) : Serializable