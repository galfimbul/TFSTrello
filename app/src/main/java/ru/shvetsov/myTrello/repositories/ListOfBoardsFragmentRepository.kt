package ru.shvetsov.myTrello.repositories

import io.reactivex.Single
import ru.shvetsov.myTrello.dataClasses.BoardFromTrelloAPI
import ru.shvetsov.myTrello.dataClasses.TrelloConstants
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.CONSUMER_KEY
import ru.shvetsov.myTrello.network.ListOfBoardsApi
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 14.12.2019
 */
class ListOfBoardsFragmentRepository @Inject constructor(
    var retrofit: ListOfBoardsApi,
    var token: String

) {
    fun loadListOfBoards(): Single<List<BoardFromTrelloAPI>> {
        return retrofit.getListOfBoards(
            "all",
            "id,name,desc",
            "name,displayName",
            true,
            "all",
            CONSUMER_KEY,
            token
        )

    }

    fun restoreBoard(boardName: String, organizationName: String): Single<BoardFromTrelloAPI> {
        return retrofit.addBoard(
            boardName,
            true,
            false,
            organizationName,
            TrelloConstants.CONSUMER_KEY,
            token
        )

    }

    fun deleteBoard(id: String): Single<String> {
        return retrofit.deleteBoard(
            id,
            TrelloConstants.CONSUMER_KEY,
            token
        )
    }
}