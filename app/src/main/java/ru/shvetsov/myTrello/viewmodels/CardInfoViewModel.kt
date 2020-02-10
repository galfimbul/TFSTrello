package ru.shvetsov.myTrello.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.repositories.CardInfoFragmentRepository
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 29.11.2019
 */
class CardInfoViewModel @Inject constructor(var repository: CardInfoFragmentRepository) : ViewModel() {
    private val cardInfoFragmentDisposables = CompositeDisposable()
    private val card = MutableLiveData<Card>()
    private val error = MutableLiveData<Int>()
    fun getCardInfo(): LiveData<Card> = card
    fun getError(): LiveData<Int> = error

    fun getCardFromServer(id: String) {
        if (card.value == null) {
            val cardFromServerResult = repository.getCard(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    card.value = it
                }, {
                    error.value = R.string.card_info_view_model_get_card_from_server_error
                })
            cardInfoFragmentDisposables.add(cardFromServerResult)
        }
    }

    override fun onCleared() {
        super.onCleared()
        cardInfoFragmentDisposables.clear()
    }
}