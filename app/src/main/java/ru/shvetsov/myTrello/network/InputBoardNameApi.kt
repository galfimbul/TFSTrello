package ru.shvetsov.myTrello.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.shvetsov.myTrello.dataClasses.BoardFromTrelloAPI
import ru.shvetsov.myTrello.dataClasses.Organization

/**
 * Created by Alexander Shvetsov on 27.10.2019
 */
interface InputBoardNameApi {
    @GET("1/members/me/organizations")
    fun getListOfTeams(
        @Query("filter") filter: String,
        @Query("fields") fields: String,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<List<Organization>>


    @POST("1/boards/")
    fun addBoard(
        @Query("name") name: String,
        @Query("defaultLabels") labels: Boolean,
        @Query("defaultLists") lists: Boolean,
        @Query("idOrganization") idOrganization: String,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<BoardFromTrelloAPI>

}