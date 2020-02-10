package ru.shvetsov.myTrello.fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.list_of_boards_fragment.*
import kotlinx.android.synthetic.main.list_of_boards_fragment.view.*
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.adapters.ListOfBoardsAdapter
import ru.shvetsov.myTrello.dataClasses.Board
import ru.shvetsov.myTrello.di.dagger.MyApp
import ru.shvetsov.myTrello.interfaces.FragmentListener
import ru.shvetsov.myTrello.viewmodels.InputBoardNameViewModel
import ru.shvetsov.myTrello.viewmodels.ListOfBoardsViewModel
import ru.shvetsov.myTrello.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class ListOfBoardsFragment : Fragment() {

    private var listener: FragmentListener? = null // создаем интерфейс для общения с Activity

    @Inject
    lateinit var adapter: ListOfBoardsAdapter

    var hasInitialized = false
    lateinit var list: MutableList<Board>
    private lateinit var inputNameViewModel: InputBoardNameViewModel

    @Inject
    lateinit var inputNameFactory: ViewModelProviderFactory
    @Inject
    lateinit var factory: ViewModelProviderFactory
    private val listOfBoardsViewModel by lazy {
        ViewModelProviders.of(this, factory).get(ListOfBoardsViewModel::class.java)
    }
    @Inject
    lateinit var spref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val component = (requireActivity().application as MyApp).appComponent.getListOfBoardsSubcomponent()
        component.inject(this)

        activity?.run {
            inputNameViewModel = ViewModelProviders.of(this, inputNameFactory)[InputBoardNameViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        retainInstance = true
        inputNameViewModel.getBoardFromServer().observe(this, Observer {
            listOfBoardsViewModel.addBoardToList(it)
            Log.d("M_MainActivity", "${it.boardName} has created")
            listener?.itemIsAddedInList(it)
            inputNameViewModel.dispBag.clear()
        })
        listOfBoardsViewModel.token = spref.getString("access_token", "")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_of_boards_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.apply {
            title = "Доски"
            setTitleTextColor(Color.WHITE)
        }

        listOfBoardsViewModel.getListOfBoards().observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty()) {
                listOfBoardsViewModel.submitEmptyList()
            } else {
                list = it.toMutableList()
                adapter.submitList(list)

            }

            if (!hasInitialized)
                hasInitialized = true
            progress_singleBoard.visibility = View.GONE
        })

        listOfBoardsViewModel.getError().observe(this, Observer {
            if (it.contains("4")) {
                spref.edit {
                    remove("access_token")
                    listener?.openAuthFragment()
                }

            }
        })

        checkOrientation()
        rv_list_of_boards.adapter = adapter
        val touchHandler = ItemTouchHelper(
            ru.shvetsov.myTrello.ItemTouchHelper(
                adapter,
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )
        )
        adapter.attachDelegate(object : ListOfBoardsAdapter.ListOfBoardsItemOnClickListener {
            override fun click(view: View) {
                val index = rv_list_of_boards.getChildAdapterPosition(view)
                listener?.clickOnBoardName(list[index])
            }
        })
        adapter.attachItemMoveListener(object : ListOfBoardsAdapter.ItemMoveListener {

            override fun itemDismiss(board: Board) {
                deleteItem(board)
            }

            override fun itemMoved(fromPosition: Int, toPosition: Int) {
                listOfBoardsViewModel.itemMoved(fromPosition, toPosition)
            }
        })
        if (!hasInitialized)
            listOfBoardsViewModel.loadData()
        progress_singleBoard.visibility = View.VISIBLE

        touchHandler.attachToRecyclerView(rv_list_of_boards)

        view.fab.setOnClickListener {
            clickOnFab()
        }
    }

    private fun checkOrientation() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val layoutManager = GridLayoutManager(context, 2)
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.getItemViewType(position) == 1) {
                        2
                    } else 1
                }
            }
            rv_list_of_boards.layoutManager = layoutManager
        } else
            rv_list_of_boards.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }

    private fun deleteItem(board: Board) {
        listOfBoardsViewModel.itemDismiss(board)
        val index = listOfBoardsViewModel.removeBoardIndex
        Snackbar.make(
            rv_list_of_boards,
            getString(R.string.list_of_boards_fragment_snackbar_text),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.snackbar_reverse_action_text)) {
                listOfBoardsViewModel.restoreItemOnServer(board, board.category, index)
            }
            .show()
        listOfBoardsViewModel.deleteBoardFromServer(board.id)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("M_ListOfBoards", "OnDestroyView")
        adapter.dispBag.clear()
        rv_list_of_boards.adapter = null
        listOfBoardsViewModel.dispBag.clear()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement FragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun clickOnFab() {
        val fragmentManager = childFragmentManager
        val inputBoardNameFragment = InputBoardNameFragment.newInstance()
        inputBoardNameFragment.show(fragmentManager, "input_board_name")
    }

    companion object {
        fun newInstance(): ListOfBoardsFragment {
            return ListOfBoardsFragment()
        }
    }

}
