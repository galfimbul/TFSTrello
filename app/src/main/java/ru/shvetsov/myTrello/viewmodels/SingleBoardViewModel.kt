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
import ru.shvetsov.myTrello.utils.notifyObserver
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Created by Alexander Shvetsov on 03.11.2019
 */
class SingleBoardViewModel @Inject constructor(val repository: SingleBoardFragmentRepository) : ViewModel() {
    lateinit var token: String
    private val cardFromServer = MutableLiveData<Card>()
    private val columnFromServer = MutableLiveData<Column>()
    var uniqId = 100L
    private val message = MutableLiveData<Int>()
    private val listOfColumns = MutableLiveData<ArrayList<Column>>()
    private val mapOfColumns = MutableLiveData<MutableMap<Column, MutableList<Card>>>()
    private val disposableBag = CompositeDisposable()
    val columnNameHasChanged = MutableLiveData<Boolean>()
    var hasInitialized = false
    lateinit var boardFromServer: BoardInfo
    var defaultQueryMap: MutableMap<Column, MutableList<Card>> = mutableMapOf()


    init {
        listOfColumns.value = ArrayList()
        mapOfColumns.value = mutableMapOf()
    }

    fun getCardFromServer(): LiveData<Card> = cardFromServer

    fun getColumnFromServer(): LiveData<Column> = columnFromServer
    fun getMessage(): LiveData<Int> = message
    fun getListOfColumns(): LiveData<ArrayList<Column>> = listOfColumns
    fun getMapOfColumns(): LiveData<MutableMap<Column, MutableList<Card>>> = mapOfColumns

    fun getBoardDetailsFromServer(id: String) {

        if (!hasInitialized) {
            val getBoardDetailsRequest = repository.getBoardDetails(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ board ->
                    listOfColumns.value!!.addAll(board.lists)
                    listOfColumns.notifyObserver()
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
                    mapOfColumns.value = map
                }, {
                    message.value = R.string.single_board_view_model_get_board_details_failed
                })
            disposableBag.add(getBoardDetailsRequest)
            hasInitialized = true
        }
    }

    fun addCardOnServer(name: String, pos: String, idList: String) {
        val addCardToServerRequest = repository.addCard(name, pos, idList)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ item ->
                val column = mapOfColumns.value!!.keys.find { it.id == item.idList }
                item!!.uniqIdForAdapter = uniqId
                uniqId++
                item.color = Color.GREEN
                message.value = R.string.single_board_view_model_card_add_success
                mapOfColumns.value!![column]!!.add(0, item)
                cardFromServer.value = item
                updateQueryMap()
            }, {
                message.value = R.string.single_boadr_view_model_add_card_on_server_fail
            })
        disposableBag.add(addCardToServerRequest)
    }

    fun addColumnToServer(name: String, board: Board) {
        val addColumnToServerRequest =
            repository.addColumn(name, board)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ column ->
                    mapOfColumns.value!![column] = arrayListOf()
                    mapOfColumns.notifyObserver()
                    listOfColumns.value!!.add(column)
                    listOfColumns.notifyObserver()
                    columnFromServer.value = column
                    updateQueryMap()
                }, {
                    message.value = R.string.single_board_view_model_add_column_to_server_failed
                })
        disposableBag.add(addColumnToServerRequest)
    }

    private fun updateQueryMap() {
        defaultQueryMap.clear()
        defaultQueryMap.putAll(mapOfColumns.value!!)
    }

    fun changeColumnPositionOnServer(oldColumnId: String, newColumnPos: Float, oldPosition: Int, newPosition: Int) {
        val changeColumnPositionOnServerRequest =
            repository.moveColumn(oldColumnId, newColumnPos)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Collections.swap(listOfColumns.value, oldPosition, newPosition)
                    message.value = R.string.single_board_view_model_change_column_position_success
                }, {
                    message.value = R.string.single_board_view_model_change_column_position_failed
                })
        disposableBag.add(changeColumnPositionOnServerRequest)
    }

    private fun removeColumn(index: Int) {
        mapOfColumns.value!!.remove(listOfColumns.value!![index])
        mapOfColumns.notifyObserver()
        listOfColumns.value!!.removeAt(index)
        listOfColumns.notifyObserver()
        message.value = R.string.single_board_view_model_remove_column_success
        updateQueryMap()
    }

    fun addColumnToArchive(index: Int) {
        val columnId = listOfColumns.value!![index].id
        val addColumnToArchiveRequest = repository.addColumnToArchive(columnId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                removeColumn(index)
            }, {
                message.value = R.string.single_board_view_model_failed_to_archive_column
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
        val idColumnTo = listOfColumns.value!![toColumn]
        val idColumnFrom = listOfColumns.value!![fromColumn]
        val map = mapOfColumns.value
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
                    Collections.swap(mapOfColumns.value!![idColumnFrom], fromRow, toRow)
                    mapOfColumns.value!![idColumnFrom]!![toRow] = it
                    mapOfColumns.notifyObserver()

                } else {
                    mapOfColumns.value!![idColumnFrom]!!.removeAt(fromRow)
                    mapOfColumns.value!![idColumnTo]!!.add(toRow, it)
                    mapOfColumns.notifyObserver()
                }
                updateQueryMap()

            }, {
                message.value = R.string.single_board_view_model_change_item_position_failed
            })
        disposableBag.add(changeItemPositionOnServerRequest)
    }

    fun changeColumnNameOnServer(newName: String, columnId: String) {
        columnNameHasChanged.value = false
        val changeColumnNameRequest = repository.changeColumnName(columnId, newName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                columnNameHasChanged.value = true
            }, {
                message.value = R.string.singe_board_view_model_change_column_name_failed
            })
        disposableBag.add(changeColumnNameRequest)
    }

    fun handleSearchQuery(query: String) {
        val map = mutableMapOf<Column, MutableList<Card>>()
        map.putAll(defaultQueryMap)
        if (query.isNullOrBlank()) {
            mapOfColumns.value = defaultQueryMap
            mapOfColumns.notifyObserver()
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
        mapOfColumns.value = map
        mapOfColumns.notifyObserver()
        mapOfColumns.value = map
    }

    override fun onCleared() {
        super.onCleared()
        disposableBag.clear()
    }
}