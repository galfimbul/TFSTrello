package ru.shvetsov.myTrello.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.dataClasses.Board
import ru.shvetsov.myTrello.dataClasses.Organization
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.CONSUMER_KEY
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.INPUT_BOARD_NAME_VIEW_MODEL_FIELDS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.INPUT_BOARD_NAME_VIEW_MODEL_FILTER
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.PERSONAL_BOARDS
import ru.shvetsov.myTrello.network.InputBoardNameApi
import ru.shvetsov.myTrello.utils.Converter
import ru.shvetsov.myTrello.utils.SingleLiveEvent
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 03.11.2019
 */
class InputBoardNameViewModel @Inject constructor(val retrofit: InputBoardNameApi, val apiToken: String) : ViewModel() {
    private val _categoryList = MutableLiveData<MutableList<String>>()
    val categoryList: LiveData<MutableList<String>>
        get() = _categoryList
    private val _teamList = MutableLiveData<List<Organization>>()
    val teamList: LiveData<List<Organization>>
        get() = _teamList
    private var _boardFromServer = MutableLiveData<Board>()
    val boardFromServer: LiveData<Board>
        get() = _boardFromServer
    private val _error = SingleLiveEvent<Int>()
    val error: SingleLiveEvent<Int>
        get() = _error
    private val disposablesBag = CompositeDisposable()


    fun loadData() {
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
                    _categoryList.value = listOfNames
                    _teamList.value = it
                }, {
                    _error.value = R.string.input_board_name_view_model_load_organization_failed
                })
        disposablesBag.add(loadDataRequest)
    }

    fun addBoardToServer(boardName: String, team: String) {
        val organizationName: String
        organizationName = if (team == PERSONAL_BOARDS) "" else {
            val organization = _teamList.value!!.filter { it.displayName == team }
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
                    _boardFromServer.value = board

                }, {
                    _error.value = R.string.input_board_name_view_model_add_board_error
                })
        disposablesBag.add(addBoardRequest)
    }

    override fun onCleared() {
        super.onCleared()
        disposablesBag.clear()
    }
}