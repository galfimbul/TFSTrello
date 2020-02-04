package ru.shvetsov.myTrello.network

import io.reactivex.Single
import retrofit2.http.*
import ru.shvetsov.myTrello.dataClasses.BoardInfo
import ru.shvetsov.myTrello.dataClasses.Column
import ru.shvetsov.myTrello.dataClasses.card.Card

/**
 * Created by Alexander Shvetsov on 27.10.2019
 */
interface SingleBoardApi {
    @GET("1/boards/{id}")
    fun getBoardDetails(
        @Path("id") id: String,
        @Query("cards") cards: String,
        @Query("card_fields") cardFields: String,
        @Query("card_attachments") cardAttachments: Boolean,
        @Query("lists") lists: String,
        @Query("list_fields") listsFields: String,
        @Query("members") membersInvited: String,
        @Query("member_fields") memberFields: String,
        @Query("card_members") cardMembers: Boolean,
        @Query("card_member_fields") cardMemberFields: String,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<BoardInfo>

    @POST("1/lists")
    fun addColumn(
        @Query("name") name: String,
        @Query("idBoard") idBoard: String,
        @Query("pos") pos: String,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<Column>

    @POST("1/cards")
    fun addCard(
        @Query("name") name: String,
        @Query("pos") pos: String,
        @Query("idList") idList: String,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<Card>


    @PUT("1/cards/{id}")
    fun moveCard(
        @Path("id") id: String,
        @Query("idList") idList: String,
        @Query("pos") pos: Float,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<Card>

    @PUT("1/lists/{id}")
    fun moveColumn(
        @Path("id") id: String,
        @Query("pos") pos: Float,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<Column>

    @PUT("1/lists/{id}")
    fun addColumnToArchive(
        @Path("id") id: String,
        @Query("closed") closed: Boolean,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<Column>

    @PUT("1/lists/{id}")
    fun changeColumnName(
        @Path("id") id: String,
        @Query("name") name: String,
        @Query("key") key: String,
        @Query("token") token: String
    ): Single<Column>


}