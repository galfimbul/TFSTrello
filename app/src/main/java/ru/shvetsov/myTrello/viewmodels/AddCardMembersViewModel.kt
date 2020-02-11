package ru.shvetsov.myTrello.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.ADD_CARD_MEMBERS_MEMBERS_FIELDS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.ADD_CARD_MEMBERS_VIEW_MODEL_BOARD_FIELDS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.CONSUMER_KEY
import ru.shvetsov.myTrello.dataClasses.User
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.network.AddMembersApi
import ru.shvetsov.myTrello.utils.SingleLiveEvent
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 28.11.2019
 */
class AddCardMembersViewModel @Inject constructor(val retrofit: AddMembersApi) : ViewModel() {
    lateinit var token: String
    private val _boardMembersList = MutableLiveData<List<User>>()
    val boardMembersList: LiveData<List<User>>
        get() = _boardMembersList
    //карточка после манипуляций на сервере
    private val _cardFromServer = MutableLiveData<Card>()
    val cardFromServer: LiveData<Card>
        get() = _cardFromServer

    private val _error = SingleLiveEvent<Int>()
    val error: SingleLiveEvent<Int>
        get() = _error

    private val disposablesBag = CompositeDisposable()


    fun loadMembers(boardId: String, apiToken: String) {
        token = apiToken
        val fields = ADD_CARD_MEMBERS_VIEW_MODEL_BOARD_FIELDS
        val membersFields = ADD_CARD_MEMBERS_MEMBERS_FIELDS
        val result =
            retrofit.getMembersOfBoard(boardId, fields, "all", membersFields, CONSUMER_KEY, token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _boardMembersList.value = it.members
                }, {
                    _boardMembersList.value = emptyList()
                })
        disposablesBag.add(result)
    }

    fun addMembersToCard(cardId: String, membersId: String) {
        val result = retrofit.addSelectedMembersToCard(cardId, membersId, CONSUMER_KEY, token)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _cardFromServer.value = it
            }, {
                _error.value = R.string.add_card_members_view_model_add_members_to_card_error
            })
        disposablesBag.add(result)
    }

    override fun onCleared() {
        super.onCleared()
        disposablesBag.clear()
    }
}