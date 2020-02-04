package ru.shvetsov.myTrello.dataClasses

import java.io.Serializable

/**
 * Created by Alexander Shvetsov on 29.10.2019
 */
data class Column(
    val id: String,
    val name: String,
    val pos: Float
) : Serializable