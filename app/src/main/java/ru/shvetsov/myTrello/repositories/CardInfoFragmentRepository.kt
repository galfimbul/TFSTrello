package ru.shvetsov.myTrello.repositories

import io.reactivex.Single
import ru.shvetsov.myTrello.dataClasses.TrelloConstants
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.network.CardInfoApi
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 14.12.2019
 */
class CardInfoFragmentRepository @Inject constructor(
    val retrofit: CardInfoApi,
    val token: String
) {
    fun getCard(id: String): Single<Card> {
        return retrofit.getCardInfo(
            id,
            TrelloConstants.CARD_INFO_VIEW_MODEL_FIELDS,
            true,
            TrelloConstants.CARD_INFO_VIEW_MODEL_MEMBER_FIELDS,
            true,
            true,
            TrelloConstants.CARD_INFO_VIEW_MODEL_BOARD_FIELDS,
            true,
            TrelloConstants.CARD_INFO_VIEW_MODEL_LIST_FIELDS,
            TrelloConstants.CONSUMER_KEY,
            token
        )
    }


}