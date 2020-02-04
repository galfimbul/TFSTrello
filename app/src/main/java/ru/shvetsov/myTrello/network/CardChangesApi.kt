package ru.shvetsov.myTrello.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.shvetsov.myTrello.dataClasses.card.CardAction

/**
 * Created by Alexander Shvetsov on 28.11.2019
 */
interface CardChangesApi {
    @GET("1/cards/{id}/actions")
    fun getListOfActions(
        @Path("id") id: String,
        @Query("filter") filter: String,
        @Query("display") display: Boolean,
        @Query("fields") fields: String,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<List<CardAction>>
}