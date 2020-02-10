package ru.shvetsov.myTrello.fragments

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.woxthebox.draglistview.BoardView
import kotlinx.android.synthetic.main.column_header.*
import kotlinx.android.synthetic.main.column_header.view.*
import kotlinx.android.synthetic.main.footer.view.*
import kotlinx.android.synthetic.main.single_board_fragment.*
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.adapters.ItemAdapter
import ru.shvetsov.myTrello.dataClasses.Board
import ru.shvetsov.myTrello.dataClasses.BoardInfo
import ru.shvetsov.myTrello.dataClasses.Column
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.di.dagger.AppTest
import ru.shvetsov.myTrello.extensions.hideKeyboard
import ru.shvetsov.myTrello.extensions.showKeyboard
import ru.shvetsov.myTrello.interfaces.FragmentListener
import ru.shvetsov.myTrello.viewmodels.SingleBoardViewModel
import ru.shvetsov.myTrello.viewmodels.ViewModelProviderFactory
import java.util.*
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 10.10.2019
 */

class SingleBoardFragment : Fragment() {
    private var listener: FragmentListener? = null
    private var sCreatedItems = 0
    lateinit var mBoardView: BoardView
    private var mColumns: Int = 0
    var listOfColumns: ArrayList<Column> = ArrayList()
    var mapOfColumns: MutableMap<Column, MutableList<Card>> = mutableMapOf()
    private lateinit var gestureDetector: GestureDetector
    lateinit var board: Board
    lateinit var boardInfo: BoardInfo
    lateinit var token: String
    var uniqId = 100L
    var lastAddedCard: Card? = null
    var lastAddedColumn: Column? = null
    lateinit var idCardForDrag: String
    @Inject
    lateinit var factory: ViewModelProviderFactory
    private val singleBoardViewModel by lazy {
        ViewModelProviders
            .of(this, factory)
            .get(SingleBoardViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        makeInject()
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        gestureDetector = GestureDetector(context, SimpleOnGestureListener())
        board = arguments!!.getSerializable("board") as Board
        val spref = activity!!.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        token = spref.getString("access_token", "")!!
    }

    private fun makeInject() {
        val component = (requireActivity().application as AppTest).appComponent.getSingleBoardSubcomponent()
        component.inject(this)
    }

    inner class SimpleOnGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
            if (e?.action == 1) {
                val index = mBoardView.focusedColumn
                mBoardView.removeColumn(index)
                mColumns--
                singleBoardViewModel.addColumnToArchive(index)
            }
            return super.onDoubleTapEvent(e)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.single_board_fragment, container, false)
        mBoardView = view.findViewById(R.id.boardView)
        mBoardView.setSnapToColumnsWhenScrolling(true)
        mBoardView.setSnapToColumnWhenDragging(true)
        mBoardView.setSnapDragItemToTouch(true)
        mBoardView.setSnapToColumnInLandscape(false)
        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER)
        mBoardView.setBackgroundColor(Color.rgb(0, 121, 190))
        mBoardView.setBoardListener(object : BoardView.BoardListener {
            override fun onColumnDragChangedPosition(oldPosition: Int, newPosition: Int) {
                val oldColumnId = listOfColumns[oldPosition].id

                val newColumnPos =
                    if (newPosition > oldPosition) listOfColumns[newPosition].pos + 1
                    else listOfColumns[newPosition].pos - 1
                singleBoardViewModel.changeColumnPositionOnServer(oldColumnId, newColumnPos, oldPosition, newPosition)
            }

            override fun onItemChangedColumn(oldColumn: Int, newColumn: Int) {
            }

            override fun onColumnDragStarted(position: Int) {
            }

            override fun onFocusedColumnChanged(oldColumn: Int, newColumn: Int) {
            }

            override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int) {
                //защита от фантомных срабатываний
                if (fromRow != toRow || fromColumn != toColumn)
                    singleBoardViewModel.changeItemPositionOnServer(idCardForDrag, fromColumn, fromRow, toColumn, toRow)
            }

            override fun onItemChangedPosition(oldColumn: Int, oldRow: Int, newColumn: Int, newRow: Int) {
            }

            override fun onColumnDragEnded(position: Int) {
            }

            @Suppress("UNCHECKED_CAST")
            override fun onItemDragStarted(column: Int, row: Int) {
                val itemList: List<Card> = boardView.getAdapter(column).itemList as List<Card>
                idCardForDrag = itemList[row].id
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress.visibility = View.VISIBLE
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(single_board_toolbar)
        single_board_toolbar.title = board.boardName
        single_board_toolbar.setTitleTextColor(Color.WHITE)
        fab_addColumn.setOnClickListener {
            addColumn(getString(R.string.single_board_fragment_default_column_name, (mColumns + 1).toString()))
        }

        Log.d("M_SingleBoard", "OnViewCreated")
        // устанавливаем токен для дальнейших запросов
        singleBoardViewModel.token = token

        //получаем данные о доске с сервера
        singleBoardViewModel.getBoardDetailsFromServer(board.id)

        singleBoardViewModel.getListOfColumns().observe(this, Observer {
            listOfColumns = it
        })

        singleBoardViewModel.getMapOfColumns().observe(this, Observer {
            if (it.keys.size > 0) {
                mapOfColumns = it
                // ставим значение uniqId с которого начнем считать
                uniqId = singleBoardViewModel.uniqId
                boardInfo = singleBoardViewModel.boardFromServer
                // отображаем доски, полученные с свервера
                createBoardFromBoardInfo()
                progress.visibility = View.GONE
            }

        })

        //получаем карточку с сервера при успешном добавлении и отображаем ее
        singleBoardViewModel.getCardFromServer().observe(this, Observer { card ->
            val columnIndex = listOfColumns.indexOfFirst { column ->
                column.id == card.idList
            }
            if (mBoardView.itemCount != mapOfColumns.values.flatten().size) {
                mBoardView.addItem(columnIndex, 0, card, true)
                lastAddedCard = card
            }
        })

        // то же самое для колонки
        singleBoardViewModel.getColumnFromServer().observe(this, Observer {
            if (mBoardView.columnCount != listOfColumns.size) {
                addColumnToBoard(it)
                lastAddedColumn = it
            }
        })

        // получаем сообщения из запросов для уведомления пользователя
        singleBoardViewModel.getMessage().observe(this, Observer {
            Toast.makeText(activity, getString(it), Toast.LENGTH_SHORT).show()
        })

        singleBoardViewModel.columnNameHasChanged.observe(this, Observer {
            if (it == true) {
                val header = mBoardView.getHeaderView(mBoardView.focusedColumn)
                header.column_header_name.text = header.et_singleBoard_header.text.toString()
                header.ll_singleBoard_header.visibility = View.VISIBLE
                header.ll_singleBoard_header_edit.visibility = View.GONE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.single_board_fragment_search_query_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                singleBoardViewModel.handleSearchQuery(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                singleBoardViewModel.handleSearchQuery(newText!!)
                return true
            }

        })
    }

    private fun createBoardFromBoardInfo() {
        // отображаем каждую колонку и наполняем ее карточками
        mBoardView.clearBoard()
        mColumns = 0
        mapOfColumns.keys.toList().forEach { column ->
            addColumnToBoard(column)
        }

    }

    private fun addColumnToBoard(column: Column) {
        val itemList = arrayListOf<Card>()
        itemList.addAll(mapOfColumns[column]!!)
        // создали адаптер для колонки
        val listAdapter =
            ItemAdapter(
                itemList,
                R.layout.column_item,
                R.id.item_layout, true
            )
        listAdapter.attachDelegate(object : ItemAdapter.ItemAdapterOnClickListener {
            override fun click(card: Card) {
                listener?.openCardInfo(card.id, boardInfo)
            }
        })
        val header: View = View.inflate(activity, R.layout.column_header, null)
        header.findViewById<TextView>(R.id.column_header_name).text = column.name
        createHeader(header)
        mBoardView.addColumn(listAdapter, header, header, false, LinearLayoutManager(context))
        // нашли заголовок колонки и поставили ему имя колонки как текст
        createFooter(column)
        mColumns++
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val parent = boardView.parent as ViewGroup
        parent.removeView(mBoardView)
        mColumns = 0
        sCreatedItems = 0
    }

    companion object {
        fun newInstance(board: Board): SingleBoardFragment {
            val singleBoard = SingleBoardFragment()
            val args = Bundle()
            args.putSerializable("board", board)
            singleBoard.arguments = args
            return singleBoard
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnViewCreatedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun addColumn(name: String) {
        singleBoardViewModel.addColumnToServer(name, board)
    }

    private fun createHeader(header: View) {
        // вешаем listener на карандаш
        header.iv_editColumnName.setOnClickListener {
            // скрыли layout хедера и показали его же для редактирования
            header.ll_singleBoard_header.visibility = View.GONE
            header.ll_singleBoard_header_edit.visibility = View.VISIBLE
            // засетили в editText предыдущее имя колонки, запросили фокус и показали клавиатуру
            header.et_singleBoard_header.setText(listOfColumns[mBoardView.focusedColumn].name)
            header.et_singleBoard_header.requestFocus()
            if (header.et_singleBoard_header.isFocused) {
                val inputMethodManager =
                    activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(et_singleBoard_header, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        header.iv_singleBoard_done.setOnClickListener {
            // если имя колонки успешно изменено на сервере, меняем его на экране и возвращаем обычный layout для header
            singleBoardViewModel.changeColumnNameOnServer(
                header.et_singleBoard_header.text.toString(),
                listOfColumns[mBoardView.focusedColumn].id
            )
        }

        header.setOnTouchListener { _, event ->
            return@setOnTouchListener gestureDetector.onTouchEvent(event)
        }
    }

    private fun createFooter(column: Column) {
        // создали футер
        val footer = View.inflate(activity, R.layout.footer, null)
        // получили parent ViewGroup для recycler
        val parent = mBoardView.getRecyclerView(mColumns).parent as LinearLayout
        parent.setBackgroundColor(Color.rgb(235, 236, 240))
        // получили параметры layout из библиотеки
        val parentParams = parent.layoutParams as LinearLayout.LayoutParams
        // задали вес и высоту
        parentParams.weight = 1f
        parentParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        // установили параметры recycler'у чтобы футер влез на экран
        mBoardView.getRecyclerView(mColumns).layoutParams = parentParams
        //добавили футер под recycler
        parent.addView(footer)
        val btnAddCard = footer.findViewById<TextView>(R.id.btn_singleBoard_footer)
        // листенер для кнопки футера
        btnAddCard.setOnClickListener {
            btnAddCard.visibility = View.GONE
            footer.et_singleBoard_footer.visibility = View.VISIBLE
            footer.et_singleBoard_footer.requestFocus()
            footer.et_singleBoard_footer.text.clear()
            activity!!.showKeyboard()
        }
        // листенер для клавиатуры при создании колонки
        footer.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                this.hideKeyboard()
                btnAddCard.visibility = View.VISIBLE
                footer.et_singleBoard_footer.visibility = View.GONE
            }
        }
        footer.et_singleBoard_footer.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (footer.et_singleBoard_footer.text.toString().isBlank()) {
                    Toast.makeText(activity, getString(R.string.single_board_empty_card_name_error), Toast.LENGTH_SHORT)
                        .show()
                    return@setOnEditorActionListener false
                }
                footer.et_singleBoard_footer.clearFocus()
                singleBoardViewModel.addCardOnServer(footer.et_singleBoard_footer.text.toString(), "top", column.id)
                btnAddCard.visibility = View.VISIBLE
                footer.et_singleBoard_footer.visibility = View.GONE
                this.hideKeyboard()
            }
            return@setOnEditorActionListener true
        }
    }
}

