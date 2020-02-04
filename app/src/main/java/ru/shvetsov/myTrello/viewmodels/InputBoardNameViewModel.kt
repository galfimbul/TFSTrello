package ru.shvetsov.myTrello.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.shvetsov.myTrello.dataClasses.Board
import ru.shvetsov.myTrello.dataClasses.Organization
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.CONSUMER_KEY
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.INPUT_BOARD_NAME_VIEW_MODEL_FIELDS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.INPUT_BOARD_NAME_VIEW_MODEL_FILTER
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.PERSONAL_BOARDS
import ru.shvetsov.myTrello.network.InputBoardNameApi
import ru.shvetsov.myTrello.utils.Converter
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 03.11.2019
 */
class InputBoardNameViewModel @Inject constructor(val retrofit: InputBoardNameApi) : ViewModel() {
    private val categoryList = MutableLiveData<MutableList<String>>()
    private val teamList = MutableLiveData<List<Organization>>()
    private lateinit var apiToken: String
    private var boardFromServer = MutableLiveData<Board>()
    val dispBag = CompositeDisposable()

    fun getCategoryList(): LiveData<MutableList<String>> = categoryList
    fun getTeamList(): LiveData<List<Organization>> = teamList
    fun getBoardFromServer(): LiveData<Board> = boardFromServer

    fun loadData(token: String) {
        apiToken = token
        val loadDataRequest =
            retrofit.getListOfTeams(
                INPUT_BOARD_NAME_VIEW_MODEL_FILTER,
                INPUT_BOARD_NAME_VIEW_MODEL_FIELDS,
                CONSUMER_KEY,
                apiToken
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val listOfNames = mutableListOf<String>()
                    it.forEach { organization ->
                        listOfNames.add(organization.displayName)
                    }
                    categoryList.value = listOfNames
                    teamList.value = it
                }, {
                    //Toast.makeText(activity, "FAIL TO MAKE REQUEST", Toast.LENGTH_LONG).show()
                })
        dispBag.add(loadDataRequest)
    }

    fun addBoardToServer(boardName: String, team: String) {
        val organizationName: String
        organizationName = if (team == PERSONAL_BOARDS) "" else {
            val organization = teamList.value!!.filter { it.displayName == team }
            organization[0].name
        }
        Log.d("M_InputBoardNameViewM", "$boardName start adding")
        val addBoardRequest =
            retrofit.addBoard(
                boardName, true, false,
                organizationName, CONSUMER_KEY, apiToken
            )
                .map { Converter.convertBoardFromAPIToBoard(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ board ->
                    board.category = team
                    boardFromServer.value = board

                }, {
                    Log.d("M_InputBoardNameView", "попал в ошибку")
                })
        dispBag.add(addBoardRequest)
    }
}