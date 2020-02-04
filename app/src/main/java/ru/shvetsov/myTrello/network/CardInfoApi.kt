package ru.shvetsov.myTrello.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.shvetsov.myTrello.dataClasses.card.Card

/**
 * Created by Alexander Shvetsov on 28.11.2019
 */
interface CardInfoApi {
    @GET("1/cards/{id}")
    fun getCardInfo(
        @Path("id") id: String,
        @Query("fields") fields: String,
        @Query("members") members: Boolean,
        @Query("member_fields") memberFields: String,
        @Query("attachments") attachments: Boolean,
        @Query("board") board: Boolean,
        @Query("board_fields") boardFields: String,
        @Query("list") list: Boolean,
        @Query("list_fields") listFields: String,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<Card>
}