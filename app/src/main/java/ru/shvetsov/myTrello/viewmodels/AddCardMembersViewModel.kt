package ru.shvetsov.myTrello.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.ADD_CARD_MEMBERS_MEMBERS_FIELDS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.ADD_CARD_MEMBERS_VIEW_MODEL_BOARD_FIELDS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.CONSUMER_KEY
import ru.shvetsov.myTrello.dataClasses.User
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.network.AddMembersApi
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 28.11.2019
 */
class AddCardMembersViewModel @Inject constructor(val retrofit: AddMembersApi) : ViewModel() {
    lateinit var token: String
    private val boardMembersList = MutableLiveData<List<User>>()
    //карточка после манипуляций на сервере
    private val cardFromServer = MutableLiveData<Card>()

    val dispBag = CompositeDisposable()

    fun getBoardMembersList(): LiveData<List<User>> = boardMembersList
    fun getCardFromServer(): LiveData<Card> = cardFromServer

    fun loadMembers(boardId: String, apiToken: String) {
        token = apiToken
        val fields = ADD_CARD_MEMBERS_VIEW_MODEL_BOARD_FIELDS
        val membersFields = ADD_CARD_MEMBERS_MEMBERS_FIELDS
        val result =
            retrofit.getMembersOfBoard(boardId, fields, "all", membersFields, CONSUMER_KEY, token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    boardMembersList.value = it.members
                }, {
                    boardMembersList.value = emptyList()
                })
        dispBag.add(result)
    }

    fun addMembersToCard(cardId: String, membersId: String) {
        val result = retrofit.addSelectedMembersToCard(cardId, membersId, CONSUMER_KEY, token)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                cardFromServer.value = it
            }, {

            })

    }
}