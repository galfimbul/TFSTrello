package ru.shvetsov.myTrello.viewmodels

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.dataClasses.Board
import ru.shvetsov.myTrello.dataClasses.BoardInfo
import ru.shvetsov.myTrello.dataClasses.Column
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.repositories.SingleBoardFragmentRepository
import ru.shvetsov.myTrello.utils.SingleLiveEvent
import ru.shvetsov.myTrello.utils.notifyObserver
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Created by Alexander Shvetsov on 03.11.2019
 */
class SingleBoardViewModel @Inject constructor(val repository: SingleBoardFragmentRepository) : ViewModel() {
    lateinit var token: String
    private val _cardFromServer = MutableLiveData<Card>()
    val cardFromServer: LiveData<Card>
        get() = _cardFromServer

    private val _columnFromServer = MutableLiveData<Column>()
    val columnFromServer: LiveData<Column>
        get() = _columnFromServer

    var uniqId = 100L

    private val _message = SingleLiveEvent<Int>()
    val message: SingleLiveEvent<Int>
        get() = _message

    private val _listOfColumns = MutableLiveData<ArrayList<Column>>()
    val listOfColumns: LiveData<ArrayList<Column>>
        get() = _listOfColumns

    private val _mapOfColumns = MutableLiveData<MutableMap<Column, MutableList<Card>>>()
    val mapOfColumns: LiveData<MutableMap<Column, MutableList<Card>>>
        get() = _mapOfColumns

    private val disposableBag = CompositeDisposable()

    private val _columnNameHasChanged = MutableLiveData<Boolean>()
    val columnNameHasChanged: LiveData<Boolean>
        get() = _columnNameHasChanged

    var hasInitialized = false

    lateinit var boardFromServer: BoardInfo

    var defaultQueryMap: MutableMap<Column, MutableList<Card>> = mutableMapOf()


    init {
        _listOfColumns.value = ArrayList()
        _mapOfColumns.value = mutableMapOf()
    }

    fun getBoardDetailsFromServer(id: String) {

        if (!hasInitialized) {
            val getBoardDetailsRequest = repository.getBoardDetails(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ board ->
                    _listOfColumns.value!!.addAll(board.lists)
                    _listOfColumns.notifyObserver()
                    val map = mutableMapOf<Column, MutableList<Card>>()
                    board.lists.forEach { column ->
                        map[column] = arrayListOf()
                        board.cards.forEach { card ->
                            if (column.id == card.idList) {
                                map[column]!!.add(card)
                                card.uniqIdForAdapter = uniqId
                                card.color = Color.GREEN
                                uniqId++
                            }
                        }
                    }
                    boardFromServer = board
                    defaultQueryMap.putAll(map)
                    _mapOfColumns.value = map
                }, {
                    _message.value = R.string.single_board_view_model_get_board_details_failed
                })
            disposableBag.add(getBoardDetailsRequest)
            hasInitialized = true
        }
    }

    fun addCardOnServer(name: String, pos: String, idList: String) {
        val addCardToServerRequest = repository.addCard(name, pos, idList)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ item ->
                val column = _mapOfColumns.value!!.keys.find { it.id == item.idList }
                item!!.uniqIdForAdapter = uniqId
                uniqId++
                item.color = Color.GREEN
                _message.value = R.string.single_board_view_model_card_add_success
                _mapOfColumns.value!![column]!!.add(0, item)
                _cardFromServer.value = item
                updateQueryMap()
            }, {
                _message.value = R.string.single_boadr_view_model_add_card_on_server_fail
            })
        disposableBag.add(addCardToServerRequest)
    }

    fun addColumnToServer(name: String, board: Board) {
        val addColumnToServerRequest =
            repository.addColumn(name, board)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ column ->
                    _mapOfColumns.value!![column] = arrayListOf()
                    _mapOfColumns.notifyObserver()
                    _listOfColumns.value!!.add(column)
                    _listOfColumns.notifyObserver()
                    _columnFromServer.value = column
                    updateQueryMap()
                }, {
                    _message.value = R.string.single_board_view_model_add_column_to_server_failed
                })
        disposableBag.add(addColumnToServerRequest)
    }

    private fun updateQueryMap() {
        defaultQueryMap.clear()
        defaultQueryMap.putAll(_mapOfColumns.value!!)
    }

    fun changeColumnPositionOnServer(oldColumnId: String, newColumnPos: Float, oldPosition: Int, newPosition: Int) {
        val changeColumnPositionOnServerRequest =
            repository.moveColumn(oldColumnId, newColumnPos)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Collections.swap(_listOfColumns.value, oldPosition, newPosition)
                    _message.value = R.string.single_board_view_model_change_column_position_success
                }, {
                    _message.value = R.string.single_board_view_model_change_column_position_failed
                })
        disposableBag.add(changeColumnPositionOnServerRequest)
    }

    private fun removeColumn(index: Int) {
        _mapOfColumns.value!!.remove(_listOfColumns.value!![index])
        _mapOfColumns.notifyObserver()
        _listOfColumns.value!!.removeAt(index)
        _listOfColumns.notifyObserver()
        _message.value = R.string.single_board_view_model_remove_column_success
        updateQueryMap()
    }

    fun addColumnToArchive(index: Int) {
        val columnId = _listOfColumns.value!![index].id
        val addColumnToArchiveRequest = repository.addColumnToArchive(columnId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                removeColumn(index)
            }, {
                _message.value = R.string.single_board_view_model_failed_to_archive_column
            })
        disposableBag.add(addColumnToArchiveRequest)
    }

    fun changeItemPositionOnServer(
        idCardForDrag: String,
        fromColumn: Int,
        fromRow: Int,
        toColumn: Int,
        toRow: Int
    ) {
        val pos: Float
        val idColumnTo = _listOfColumns.value!![toColumn]
        val idColumnFrom = _listOfColumns.value!![fromColumn]
        val map = _mapOfColumns.value
        if (fromColumn == toColumn) {
            when (toRow) {
                0 -> {
                    pos = map!![idColumnFrom]!![0].pos.toFloat() / 2f
                }
                map!![idColumnFrom]!!.size - 1 -> {
                    pos = map[idColumnFrom]!![map[idColumnFrom]!!.size - 1].pos.toFloat() * 2f
                }
                else -> {
                    val insertedPos: Float
                    val previousPos: Float
                    if (fromRow > toRow) {
                        insertedPos = map[idColumnFrom]!![toRow].pos.toFloat()
                        previousPos = map[idColumnFrom]!![toRow - 1].pos.toFloat()
                        pos = (insertedPos + previousPos) / 2
                    } else {
                        insertedPos = map[idColumnFrom]!![toRow + 1].pos.toFloat()
                        previousPos = map[idColumnFrom]!![toRow].pos.toFloat()
                        pos = (insertedPos + previousPos) / 2
                    }
                }
            }

        } else {
            pos = when (toRow) {
                0 -> if (map!![idColumnTo]!!.isNotEmpty()) map[idColumnTo]!![0].pos.toFloat() / 2f else 16384f
                map!![idColumnTo]!!.size -> map[idColumnTo]!![map[idColumnTo]!!.size - 1].pos.toFloat()
                else -> {
                    val previousPos = map[idColumnTo]!![toRow - 1].pos.toFloat()
                    val nextPos = map[idColumnTo]!![toRow].pos.toFloat()
                    (nextPos + previousPos) / 2
                }
            }
        }
        val changeItemPositionOnServerRequest = repository.moveCard(
            idCardForDrag,
            idColumnTo.id,
            pos
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (idColumnFrom == idColumnTo) {
                    Collections.swap(_mapOfColumns.value!![idColumnFrom], fromRow, toRow)
                    _mapOfColumns.value!![idColumnFrom]!![toRow] = it
                    _mapOfColumns.notifyObserver()

                } else {
                    _mapOfColumns.value!![idColumnFrom]!!.removeAt(fromRow)
                    _mapOfColumns.value!![idColumnTo]!!.add(toRow, it)
                    _mapOfColumns.notifyObserver()
                }
                updateQueryMap()

            }, {
                _message.value = R.string.single_board_view_model_change_item_position_failed
            })
        disposableBag.add(changeItemPositionOnServerRequest)
    }

    fun changeColumnNameOnServer(newName: String, columnId: String) {
        _columnNameHasChanged.value = false
        val changeColumnNameRequest = repository.changeColumnName(columnId, newName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _columnNameHasChanged.value = true
            }, {
                _message.value = R.string.singe_board_view_model_change_column_name_failed
            })
        disposableBag.add(changeColumnNameRequest)
    }

    fun handleSearchQuery(query: String) {
        val map = mutableMapOf<Column, MutableList<Card>>()
        map.putAll(defaultQueryMap)
        if (query.isNullOrBlank()) {
            _mapOfColumns.value = defaultQueryMap
            _mapOfColumns.notifyObserver()
            return
        }
        val iterator = map.iterator()
        while (iterator.hasNext()) {
            val column = iterator.next()
            val list = column.value.filter { it.name.contains(query) }
            column.setValue(list.toMutableList())
            if (column.value.isEmpty()) {
                iterator.remove()
            }
        }
        _mapOfColumns.value = map
        _mapOfColumns.notifyObserver()
        _mapOfColumns.value = map
    }

    override fun onCleared() {
        super.onCleared()
        disposableBag.clear()
    }
}