package ru.shvetsov.myTrello.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.shvetsov.myTrello.ItemTouchHelper
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.dataClasses.Board
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Alexander Shvetsov on 10.10.2019
 */
const val HEADER_VIEWTYPE = 1
const val NORMAL_ITEM_VIEWTYPE = 2
const val EMPTY_ITEM_VIEWTYPE = 3 // ViewType для пустых данных

class ListOfBoardsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelper.ItemTouchHelperAdapter {

    private var recyclerList: MutableList<Board> = ArrayList()

    private var onItemClickListener: ListOfBoardsItemOnClickListener? =
        null // обработчик нажатий для элементов RecyclerView

    private var onItemMoveListener: ItemMoveListener? = null // обработчик нажатий для элементов RecyclerView
    val dispBag = CompositeDisposable()

    fun attachDelegate(itemOnClickListener: ListOfBoardsItemOnClickListener) { // привязка прослушивателя к Activity
        this.onItemClickListener = itemOnClickListener
    }

    fun attachItemMoveListener(listener: ItemMoveListener) {
        this.onItemMoveListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER_VIEWTYPE -> ListOfBoardsCategoryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_of_boards_category_item, parent, false)
            )
            NORMAL_ITEM_VIEWTYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.board_item, parent, false)
                view.setOnClickListener {
                    onItemClickListener?.click(it)
                }
                ListOfBoardsItemViewHolder(view)
            }
            EMPTY_ITEM_VIEWTYPE -> ListOfBoardsEmptyViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.empty_recycler_layout, parent, false)
            )
            else -> throw Exception("Unsupported viewHolderType")
        }

    }

    override fun getItemCount(): Int {
        return recyclerList.size
    }

    fun submitList(newList: List<Board>) {
        val oldList = recyclerList
        val rxList = Single.just(newList)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .map { DiffUtil.calculateDiff(BoardItemDiffCallback(oldList, it)) }
            .subscribe({
                recyclerList = newList.toMutableList()
                it.dispatchUpdatesTo(this)

            }, {

            })
        dispBag.add(rxList)
    }

    override fun getItemViewType(position: Int): Int {
        return when (recyclerList[position].viewType) {
            HEADER_VIEWTYPE -> HEADER_VIEWTYPE
            NORMAL_ITEM_VIEWTYPE -> NORMAL_ITEM_VIEWTYPE
            EMPTY_ITEM_VIEWTYPE -> EMPTY_ITEM_VIEWTYPE
            else -> throw Exception("Unsupported viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ListOfBoardsItemViewHolder ->
                holder.bind(recyclerList[position])
            is ListOfBoardsCategoryViewHolder -> {
                holder.bind(recyclerList[position])
            }
            is ListOfBoardsEmptyViewHolder -> {
                holder.bind(recyclerList[position])
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (toPosition == 0) {
            return
        }
        if (fromPosition < toPosition) {
            if (recyclerList[toPosition].viewType == HEADER_VIEWTYPE &&
                recyclerList[fromPosition - 1].viewType == HEADER_VIEWTYPE
            ) {
                Collections.swap(recyclerList, fromPosition, toPosition)
                notifyItemMoved(fromPosition, toPosition)
                recyclerList.removeAt(fromPosition - 1)
                notifyItemRemoved(fromPosition - 1)
                onItemMoveListener!!.itemMoved(fromPosition, toPosition)
            } else {
                Collections.swap(recyclerList, fromPosition, toPosition)
                notifyItemMoved(fromPosition, toPosition)
                onItemMoveListener!!.itemMoved(fromPosition, toPosition)
            }
        }
        if (fromPosition > toPosition) {
            if (recyclerList[fromPosition - 1].viewType == HEADER_VIEWTYPE
                && (fromPosition == recyclerList.size - 1 || recyclerList[fromPosition + 1].viewType == HEADER_VIEWTYPE)
            ) {
                Collections.swap(recyclerList, fromPosition, toPosition)
                notifyItemMoved(fromPosition, toPosition)
                recyclerList.removeAt(fromPosition)
                notifyItemRemoved(fromPosition)
                onItemMoveListener!!.itemMoved(fromPosition, toPosition)
            } else {
                Collections.swap(recyclerList, fromPosition, toPosition)
                notifyItemMoved(fromPosition, toPosition)
                onItemMoveListener!!.itemMoved(fromPosition, toPosition)
            }
        }
    }

    override fun onItemDismiss(position: Int) {
        val item = recyclerList[position]
        if (recyclerList[position - 1].viewType == HEADER_VIEWTYPE &&
            (position + 1 == recyclerList.size || recyclerList[position + 1].viewType == HEADER_VIEWTYPE)
        ) {
            recyclerList.removeAt(position - 1)
            recyclerList.remove(item)
            notifyItemRangeRemoved(position - 1, 2)
            submitList(recyclerList)
        } else {
            recyclerList.remove(item)
            notifyItemRemoved(position)
        }
        removeFromMapAndShowSnackBar(item)
    }

    private fun removeFromMapAndShowSnackBar(item: Board) {
        onItemMoveListener!!.itemDismiss(item)
    }

    class ListOfBoardsItemViewHolder(
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        private val text: TextView = itemView.findViewById(R.id.tv_boardName)
        private val image: ImageView = itemView.findViewById(R.id.iv_boardColor)

        /**
         * устанавливаем данные в элемент списка
         * и вешаем на него обработчик нажатий
         */
        fun bind(board: Board) {
            text.text = board.boardName
            image.setBackgroundColor(board.color!!)
        }
    }

    class ListOfBoardsCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.tv_category_name)
        fun bind(board: Board) {
            textView.text = board.boardName
        }
    }

    class ListOfBoardsEmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.tv_empty)
        fun bind(board: Board) {
            textView.text = board.boardName
        }
    }

    class BoardItemDiffCallback(
        var oldBoardList: List<Board>,
        var newBoardList: List<Board>
    ) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldBoardList[oldItemPosition].id == newBoardList[newItemPosition].id
        }

        override fun getOldListSize(): Int {
            return oldBoardList.size
        }

        override fun getNewListSize(): Int {
            return newBoardList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldBoardList[oldItemPosition] == newBoardList[newItemPosition]
        }
    }

    interface ListOfBoardsItemOnClickListener { // интерфейс для обработки нажатий на элементы списка
        fun click(view: View)
    }

    interface ItemMoveListener {
        fun itemDismiss(board: Board)
        fun itemMoved(fromPosition: Int, toPosition: Int)
    }
}