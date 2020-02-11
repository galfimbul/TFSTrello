package ru.shvetsov.myTrello.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.repositories.CardInfoFragmentRepository
import ru.shvetsov.myTrello.utils.SingleLiveEvent
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 29.11.2019
 */
class CardInfoViewModel @Inject constructor(var repository: CardInfoFragmentRepository) : ViewModel() {
    private val cardInfoFragmentDisposables = CompositeDisposable()
    private val _card = MutableLiveData<Card>()
    val card: LiveData<Card>
        get() = _card
    private val _error = SingleLiveEvent<Int>()
    val error: SingleLiveEvent<Int>
        get() = _error

    fun getCardFromServer(id: String) {
        if (_card.value == null) {
            val cardFromServerResult = repository.getCard(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _card.value = it
                }, {
                    _error.value = R.string.card_info_view_model_get_card_from_server_error
                })
            cardInfoFragmentDisposables.add(cardFromServerResult)
        }
        cardInfoFragmentDisposables.add(
            repository.getCard(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _card.value = it
                }, {
                    _error.value = R.string.card_info_view_model_get_card_from_server_error
                })

        )
    }

    override fun onCleared() {
        super.onCleared()
        cardInfoFragmentDisposables.clear()
    }
}