package ru.shvetsov.myTrello.utils

import ru.shvetsov.myTrello.adapters.NORMAL_ITEM_VIEWTYPE
import ru.shvetsov.myTrello.dataClasses.Board
import ru.shvetsov.myTrello.dataClasses.BoardFromTrelloAPI

/**
 * Created by Alexander Shvetsov on 28.10.2019
 */
class Converter {
    companion object {
        /*fun convertBoardFromAPIToBoard(list: List<BoardFromTrelloAPI>): List<Board> {
            val resultList = mutableListOf<Board>()
            list.forEach {
                resultList.add(
                    Board(
                        it.id,
                        it.name,
                        it.columns,
                        Generator.generateColor(),
                        if (it.organization == null) "Personal Boards" else it.organization.displayName,
                        NORMAL_ITEM_VIEWTYPE
                    )
                )
            }
            return resultList

        }*/
        fun convertBoardFromAPIToBoard(board: BoardFromTrelloAPI): Board {

            return Board(
                board.id,
                board.name,
                board.columns,
                Generator.generateColor(),
                if (board.organization == null) "Personal Boards" else board.organization.displayName,
                board.idOrganization,
                NORMAL_ITEM_VIEWTYPE
            )

        }
    }
}