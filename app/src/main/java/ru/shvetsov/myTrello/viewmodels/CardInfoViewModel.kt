package ru.shvetsov.myTrello.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.shvetsov.myTrello.dataClasses.TrelloConstants
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.CARD_INFO_VIEW_MODEL_BOARD_FIELDS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.CARD_INFO_VIEW_MODEL_LIST_FIELDS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.CARD_INFO_VIEW_MODEL_MEMBER_FIELDS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.CONSUMER_KEY
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.network.CardInfoApi
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 29.11.2019
 */
class CardInfoViewModel @Inject constructor(val retrofit: CardInfoApi, spref: SharedPreferences) : ViewModel() {
    val card = MutableLiveData<Card>()
    val token = spref.getString(TrelloConstants.ACCESS_TOKEN_KEY, "")!!
    fun getCardInfo(): LiveData<Card> = card
    fun getCardFromServer(id: String) {
        if (card.value == null) {
            val result = retrofit.getCardInfo(
                id,
                TrelloConstants.CARD_INFO_VIEW_MODEL_FIELDS,
                true,
                CARD_INFO_VIEW_MODEL_MEMBER_FIELDS,
                true,
                true,
                CARD_INFO_VIEW_MODEL_BOARD_FIELDS,
                true,
                CARD_INFO_VIEW_MODEL_LIST_FIELDS,
                CONSUMER_KEY,
                token
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    card.value = it
                }, {

                })
        }

    }
}