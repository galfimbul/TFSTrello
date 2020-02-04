package ru.shvetsov.myTrello.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.shvetsov.myTrello.dataClasses.BoardInfo
import ru.shvetsov.myTrello.dataClasses.TrelloConstants
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.network.SearchFragmentApi
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 28.11.2019
 */
class SearchFragmentViewModel @Inject constructor(val retrofit: SearchFragmentApi) : ViewModel() {
    lateinit var apiToken: String
    private val actionsList = MutableLiveData<List<Card>>()
    val dispBag = CompositeDisposable()
    lateinit var boardInfo: BoardInfo
    lateinit var defaultList: List<Card>

    fun getCardsList(): LiveData<List<Card>> = actionsList

    fun loadCards(id: String) {
        val fields = "id,name,pos,idList,desc"
        val membersFields = "id,avatarHash,avatarUrl,initials,fullName,username"
        val result = retrofit.getListOfCards(
            id = id,
            cards = "open",
            cardFields = fields,
            cardAttachments = true,
            membersInvited = "all",
            memberFields = membersFields, fields = "", key = TrelloConstants.CONSUMER_KEY, token = apiToken
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                boardInfo = it
                actionsList.value = it.cards
                defaultList = it.cards
            }, {
                actionsList.value = emptyList()
            })
        dispBag.add(result)
    }

    fun handleSearch(s: String) {
        if (s.isEmpty())
            actionsList.value = defaultList
        else {
            actionsList.value = defaultList.filter { it.name.contains(s) }
        }
    }
}