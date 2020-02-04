package ru.shvetsov.myTrello.utils

import android.graphics.Color
import kotlin.random.Random

/**
 * Created by Alexander Shvetsov on 10.10.2019
 */
/**
 * Класс для генерации различных данных
 */
class Generator {
    companion object {
        fun generateColor(): Int {
            val rnd = Random
            return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

        }

        fun generateCategory(): String {
            val array = arrayOf("Personal boards", "Work boards", "Other boards")
            val index = Random.nextInt(3)
            return array[index]
        }
    }
}