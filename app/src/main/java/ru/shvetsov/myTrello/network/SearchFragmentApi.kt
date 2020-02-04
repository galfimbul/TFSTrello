package ru.shvetsov.myTrello.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.shvetsov.myTrello.dataClasses.BoardInfo

/**
 * Created by Alexander Shvetsov on 28.11.2019
 */
interface SearchFragmentApi {
    @GET("1/boards/{id}/")
    fun getListOfCards(
        @Path("id") id: String,
        @Query("cards") cards: String,
        @Query("card_fields") cardFields: String,
        @Query("card_attachments") cardAttachments: Boolean,
        @Query("members") membersInvited: String,
        @Query("member_fields") memberFields: String,
        @Query("fields") fields: String,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<BoardInfo>
}