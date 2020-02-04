package ru.shvetsov.myTrello.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import ru.shvetsov.myTrello.dataClasses.BoardInfo
import ru.shvetsov.myTrello.dataClasses.card.Card

/**
 * Created by Alexander Shvetsov on 28.11.2019
 */
interface AddMembersApi {
    @GET("1/boards/{id}")
    fun getMembersOfBoard(
        @Path("id") id: String,
        @Query("fields") fields: String,
        @Query("members") members: String,
        @Query("member_fields") memberFields: String,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<BoardInfo>

    @PUT("1/cards/{id}/")
    fun addSelectedMembersToCard(
        @Path("id") id: String,
        @Query("idMembers") idMembers: String,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<Card>
}