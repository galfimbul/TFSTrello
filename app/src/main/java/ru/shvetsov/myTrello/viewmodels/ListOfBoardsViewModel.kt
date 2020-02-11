package ru.shvetsov.myTrello.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.shvetsov.myTrello.adapters.EMPTY_ITEM_VIEWTYPE
import ru.shvetsov.myTrello.adapters.HEADER_VIEWTYPE
import ru.shvetsov.myTrello.dataClasses.Board
import ru.shvetsov.myTrello.dataClasses.TrelloConstants
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.LIST_OF_BOARDS_VIEW_MODEL_EMPTY_LIST_BOARD_NAME
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.LIST_OF_BOARDS_VIEW_MODEL_EMPTY_LIST_CATEGORY
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.LIST_OF_BOARDS_VIEW_MODEL_EMPTY_LIST_ID
import ru.shvetsov.myTrello.network.ListOfBoardsApi
import ru.shvetsov.myTrello.repositories.ListOfBoardsFragmentRepository
import ru.shvetsov.myTrello.utils.Converter
import ru.shvetsov.myTrello.utils.SingleLiveEvent
import java.util.*
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 03.11.2019
 */

class ListOfBoardsViewModel @Inject constructor(val retrofit: ListOfBoardsApi, val token: String) : ViewModel() {
    private val mapOfBoards: MutableMap<String, MutableList<Board>> = mutableMapOf()
    private val _listOfBoards = MutableLiveData<List<Board>>()
    val listOfBoards: LiveData<List<Board>>
        get() = _listOfBoards
    private var itemId = 100 // Id айтемов
    private val _error = SingleLiveEvent<String>()
    val error: SingleLiveEvent<String>
        get() = _error
    var removeBoardIndex: Int = 0
    val dispBag = CompositeDisposable()
    @Inject
    lateinit var repository: ListOfBoardsFragmentRepository

    fun loadData() {
        if (!_listOfBoards.value.isNullOrEmpty()) {
            mapOfBoards.clear()
        }
        val loadListOfBoardsRequest =
            repository.loadListOfBoards()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        it.map { list -> Converter.convertBoardFromAPIToBoard(list) }
                            .forEach { board ->
                                setData(board)
                            }
                        _listOfBoards.value = mapOfBoards.values.flatten()
                    }, {
                        _error.value = it.localizedMessage

                    }
                )
        dispBag.add(loadListOfBoardsRequest)
    }

    private fun setData(item: Board) {
        // Если в нужной нам категории нет айтемов или она пуста
        if (mapOfBoards[item.category].isNullOrEmpty()) {
            val list = mutableListOf<Board>()
            // создаем хедер
            val header = Board(
                id = (itemId + 1).toString(),
                boardName = item.category,
                columns = arrayListOf(),
                color = null,
                category = item.category,
                categoryId = null,
                viewType = HEADER_VIEWTYPE
            )
            itemId++
            itemId++
            //добавляем хедер и элемент в список
            list.add(header)
            list.add(item)
            //сетим список в мапу в нужную категорию
            mapOfBoards[item.category] = list
        } else {
            mapOfBoards[item.category]!!.add(item)
        }
    }

    fun itemDismiss(board: Board) {
        removeBoardIndex = mapOfBoards[board.category]!!.indexOf(board)
        mapOfBoards[board.category]!!.remove(board)
        if (mapOfBoards[board.category]!!.size == 1) {
            mapOfBoards[board.category]!!.clear()
        }
        _listOfBoards.value = mapOfBoards.values.flatten()
    }

    private fun restoreItem(board: Board, index: Int) {
        if (mapOfBoards[board.category]!!.isEmpty()) {
            val header = Board(
                id = (itemId + 1).toString(), boardName = board.category, columns = null, color = null,
                category = board.category, categoryId = null, viewType = HEADER_VIEWTYPE
            )
            itemId++
            mapOfBoards[board.category]!!.add(header)
            mapOfBoards[board.category]!!.add(board)
        } else {
            mapOfBoards[board.category]!!.add(index, board)
        }
        _listOfBoards.value = mapOfBoards.values.flatten()
    }

    fun restoreItemOnServer(board: Board, team: String, index: Int) {
        val organizationName: String = if (team == TrelloConstants.PERSONAL_BOARDS) "" else {
            board.categoryId!!
        }
        val restoreBoardRequest =
            repository.restoreBoard(board.boardName.orEmpty(), organizationName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    restoreItem(board, index)
                }, {
                    _error.value = it.localizedMessage
                })
        dispBag.add(restoreBoardRequest)
    }

    fun deleteBoardFromServer(id: String) {
        val deleteBoardRequest =
            repository.deleteBoard(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, {
                    _error.value = it.localizedMessage
                })
        dispBag.add(deleteBoardRequest)
    }

    fun itemMoved(fromPosition: Int, toPosition: Int) {
        val listFromMap = mapOfBoards.values.flatten()
        val fromItem = listFromMap[fromPosition]
        val toItem = listFromMap[toPosition]
        /**
         * если итемы свапаются внутри одной категории
         */
        if (fromItem.category == toItem.category) {
            val list = mapOfBoards[fromItem.category]
            Collections.swap(list, list!!.indexOf(fromItem), list.indexOf(toItem))
        }
        /**
         * если итем свапается с хедером
         */
        if (toItem.viewType == HEADER_VIEWTYPE) {
            val list = mapOfBoards.values.flatten()
            if (fromPosition > toPosition) {
                mapOfBoards[fromItem.category]!!.remove(fromItem)
                if (mapOfBoards[fromItem.category]!!.size == 1) {
                    mapOfBoards[fromItem.category]!!.clear()
                }
                fromItem.category = list[toPosition - 1].category
                mapOfBoards[list[toPosition - 1].category]!!.add(fromItem)
            } else {
                mapOfBoards[fromItem.category]!!.remove(fromItem)
                if (mapOfBoards[fromItem.category]!!.size == 1) {
                    mapOfBoards[fromItem.category]!!.clear()
                }
                fromItem.category = list[toPosition + 1].category
                mapOfBoards[list[toPosition + 1].category]!!.add(1, fromItem)
            }
        }
        _listOfBoards.value = mapOfBoards.values.flatten()
    }

    fun submitEmptyList() {
        _listOfBoards.value = listOf(
            Board(
                id = LIST_OF_BOARDS_VIEW_MODEL_EMPTY_LIST_ID,
                boardName = LIST_OF_BOARDS_VIEW_MODEL_EMPTY_LIST_BOARD_NAME,
                viewType = EMPTY_ITEM_VIEWTYPE,
                category = LIST_OF_BOARDS_VIEW_MODEL_EMPTY_LIST_CATEGORY,
                categoryId = null,
                columns = null
            )
        )
    }

    fun addBoardToList(board: Board) {
        setData(board)
        _listOfBoards.value = mapOfBoards.values.flatten()
    }
}