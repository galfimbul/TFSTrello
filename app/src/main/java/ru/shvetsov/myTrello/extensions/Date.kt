package ru.shvetsov.myTrello.extensions

import java.text.SimpleDateFormat
import java.util.*


const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR


fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.shortFormat(): String {
    val pattern = if (this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)

}

fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY
    return day1 == day2


}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time
    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    var value: Long = (date.time - this.time)
    var result = ""
    var inFuture = false
    if (value < 0) {
        value = -value
        inFuture = true
    }
    when (value / SECOND) {
        in 0..1 -> return "только что"
        in 1..45 -> result = "несколько секунд"
        in 45..75 -> result = "минуту"
        in 75..45 * 60 ->
            when (value / MINUTE % 10) {
                1L -> if (value / MINUTE % 100 != 11L) {
                    result = "${(value / MINUTE)} минуту"
                }
                2L, 3L, 4L -> if (value / MINUTE % 100 !in 12L..14L) {
                    result = "${(value / MINUTE)} минуты"
                }
                else -> result = "${(value / MINUTE)} минут"
            }
        in 45 * 60..75 * 60 -> result = "час"
        in 75 * 60..22 * HOUR / SECOND -> when (value / HOUR % 10) {
            1L -> if (value / HOUR % 100 != 11L) {
                result = "${(value / HOUR)} час"
            }
            2L, 3L, 4L -> result = if (value / HOUR % 100 !in 12L..14L) {
                "${(value / HOUR)} часа"
            } else {
                "${(value / HOUR)} часов"
            }
            else -> result = "${(value / HOUR)} часов"
        }
        in 22 * HOUR / SECOND..26 * HOUR / SECOND -> result = "день"
        in 26 * HOUR / SECOND..360 * DAY / SECOND -> when (value / DAY % 10) {
            1L -> if (value / DAY % 100 != 11L) {
                result = "${(value / DAY)} день"
            }
            2L, 3L, 4L -> if (value / DAY % 100 !in 12L..14L) {
                result = "${(value / DAY)} дня"
            } else {
                result = "${(value / DAY)} дней"
            }
            else -> result = "${(value / DAY)} дней"
        }
    }
    when (inFuture) {
        true -> if (value / DAY <= 360) {
            return "через $result"
        } else {
            return "более чем через год"
        }
        false -> if (value / DAY <= 360) {
            return "$result назад"
        } else {
            return "более года назад"
        }
    }

}

enum class TimeUnits {
    SECOND,
    MINUTE,
    HOUR,
    DAY;

    fun plural(value: Int): String {
        when (this) {
            SECOND -> when (value % 10) {
                1 -> return "$value секунду"
                2, 3, 4 -> return "$value секунды"
                else -> return "$value секунд"
            }
            MINUTE -> when (value % 10) {
                1 -> return "$value минуту"
                2, 3, 4 -> return "$value минуты"
                else -> return "$value минут"
            }
            HOUR -> when (value % 10) {
                1 -> return "$value час"
                2, 3, 4 -> return "$value часа"
                else -> return "$value часов"
            }
            DAY -> when (value % 10) {
                1 -> return "$value день"
                2, 3, 4 -> return "$value дня"
                else -> return "$value дней"
            }
        }
    }
}

fun TimeUnits.plural(value: Int): String {
    when (this) {
        TimeUnits.SECOND -> when (value % 10) {
            1 -> return "$value секунду"
            2, 3, 4 -> return "$value секунды"
            else -> return "$value секунд"
        }
        TimeUnits.MINUTE -> when (value % 10) {
            1 -> return "$value минуту"
            2, 3, 4 -> return "$value минуты"
            else -> return "$value минут"
        }
        TimeUnits.HOUR -> when (value % 10) {
            1 -> return "$value час"
            2, 3, 4 -> return "$value часа"
            else -> return "$value часов"
        }
        TimeUnits.DAY -> when (value % 10) {
            1 -> return "$value день"
            2, 3, 4 -> return "$value дня"
            else -> return "$value дней"
        }
    }
}