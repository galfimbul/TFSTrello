package ru.shvetsov.myTrello.repositories

import io.reactivex.Single
import ru.shvetsov.myTrello.dataClasses.Board
import ru.shvetsov.myTrello.dataClasses.BoardInfo
import ru.shvetsov.myTrello.dataClasses.Column
import ru.shvetsov.myTrello.dataClasses.TrelloConstants
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.SINGLE_BOARD_VIEW_MODEL_CARDS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.SINGLE_BOARD_VIEW_MODEL_CARD_FIELDS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.SINGLE_BOARD_VIEW_MODEL_CARD_MEMBER_FIELDS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.SINGLE_BOARD_VIEW_MODEL_LISTS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.SINGLE_BOARD_VIEW_MODEL_LISTS_FIELDS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.SINGLE_BOARD_VIEW_MODEL_MEMBERS_INVITED
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.SINGLE_BOARD_VIEW_MODEL_MEMBER_FIELDS
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.network.SingleBoardApi
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 14.12.2019
 */
class SingleBoardFragmentRepository @Inject constructor(val retrofit: SingleBoardApi, val token: String) {
    fun getBoardDetails(id: String): Single<BoardInfo> {
        return retrofit.getBoardDetails(
            id,
            SINGLE_BOARD_VIEW_MODEL_CARDS,
            SINGLE_BOARD_VIEW_MODEL_CARD_FIELDS,
            true,
            SINGLE_BOARD_VIEW_MODEL_LISTS,
            SINGLE_BOARD_VIEW_MODEL_LISTS_FIELDS,
            SINGLE_BOARD_VIEW_MODEL_MEMBERS_INVITED,
            SINGLE_BOARD_VIEW_MODEL_MEMBER_FIELDS,
            true,
            SINGLE_BOARD_VIEW_MODEL_CARD_MEMBER_FIELDS,
            TrelloConstants.CONSUMER_KEY,
            token
        )
    }

    fun addCard(name: String, pos: String, idList: String): Single<Card> {
        return retrofit.addCard(name, pos, idList, TrelloConstants.CONSUMER_KEY, token)
    }

    fun addColumn(name: String, board: Board): Single<Column> {
        return retrofit.addColumn(
            name,
            board.id,
            TrelloConstants.SINGLE_BOARD_VIEW_MODEL_POS,
            TrelloConstants.CONSUMER_KEY,
            token
        )
    }

    fun moveColumn(oldColumnId: String, newColumnPos: Float): Single<Column> {
        return retrofit.moveColumn(oldColumnId, newColumnPos, TrelloConstants.CONSUMER_KEY, token)
    }

    fun addColumnToArchive(columnId: String): Single<Column> {
        return retrofit.addColumnToArchive(columnId, true, TrelloConstants.CONSUMER_KEY, token)
    }

    fun moveCard(idCardForDrag: String, idColumnTo: String, pos: Float): Single<Card> {
        return retrofit.moveCard(
            idCardForDrag,
            idColumnTo,
            pos,
            TrelloConstants.CONSUMER_KEY,
            token
        )
    }

    fun changeColumnName(newName: String, columnId: String): Single<Column> {
        return retrofit.changeColumnName(columnId, newName, TrelloConstants.CONSUMER_KEY, token)
    }


}