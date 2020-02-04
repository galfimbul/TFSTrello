package ru.shvetsov.myTrello.interfaces

import ru.shvetsov.myTrello.dataClasses.Board
import ru.shvetsov.myTrello.dataClasses.BoardInfo
import ru.shvetsov.myTrello.dataClasses.card.Card

/**
 * Created by Alexander Shvetsov on 11.10.2019
 */
interface FragmentListener {
    fun clickOnBoardName(board: Board)
    fun itemIsAddedInList(board: Board) // указываем Activity что нужно создать фрагмент с доской
    fun getToken() // уведомляем Activity что токен успешно получен
    fun openCardInfo(cardId: String, boardInfo: BoardInfo)
    fun openCardChanges(card: Card)
    fun openSearchFragment(id: String)
    fun openAuthFragment()
}