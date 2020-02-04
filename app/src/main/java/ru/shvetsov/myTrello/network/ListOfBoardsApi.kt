package ru.shvetsov.myTrello.network

import io.reactivex.Single
import retrofit2.http.*
import ru.shvetsov.myTrello.dataClasses.BoardFromTrelloAPI

/**
 * Created by Alexander Shvetsov on 27.10.2019
 */
interface ListOfBoardsApi {
    @GET("1/members/me/boards")
    fun getListOfBoards(
        @Query("filter") filter: String,
        @Query("fields") fields: String,
        @Query("organization_fields") organization_fields: String,
        @Query("organization") organization: Boolean,
        @Query("lists") lists: String,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<List<BoardFromTrelloAPI>>

    @POST("1/boards/")
    fun addBoard(
        @Query("name") name: String,
        @Query("defaultLabels") labels: Boolean,
        @Query("defaultLists") lists: Boolean,
        @Query("idOrganization") idOrganization: String,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<BoardFromTrelloAPI>


    @DELETE("1/boards/{id}")
    fun deleteBoard(
        @Path("id") id: String,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<String>


}