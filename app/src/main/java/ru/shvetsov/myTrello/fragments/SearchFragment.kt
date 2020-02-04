package ru.shvetsov.myTrello.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.search_fragment.*
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.adapters.SearchFragmentAdapter
import ru.shvetsov.myTrello.dataClasses.BoardInfo
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.extensions.hideKeyboard
import ru.shvetsov.myTrello.extensions.showKeyboard
import ru.shvetsov.myTrello.interfaces.FragmentListener
import ru.shvetsov.myTrello.viewmodels.SearchFragmentViewModel
import ru.shvetsov.myTrello.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class SearchFragment : DaggerFragment() {

    private var listener: FragmentListener? = null // создаем интерфейс для общения с Activity
    lateinit var listOfCards: MutableList<Card>
    lateinit var boardId: String
    lateinit var boardInfo: BoardInfo
    @Inject
    lateinit var adapter: SearchFragmentAdapter

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var spref: SharedPreferences
    private val viewModel by lazy {
        ViewModelProviders.of(this, factory).get(SearchFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.apiToken = spref.getString("access_token", "")!!
        super.onCreate(savedInstanceState)
        boardId = arguments!!.getString("boardId", "")
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        viewModel.loadCards(boardId)
        rv_search_fragment.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rv_search_fragment.adapter = adapter
        adapter.attachDelegate(object : SearchFragmentAdapter.Delegate {
            override fun click(card: Card) {
                listener?.openCardInfo(card.id, boardInfo)
            }
        })
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.handleSearch(s.toString())
            }

        })
        et_search.requestFocus()
        activity!!.showKeyboard()
        et_search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                et_search.clearFocus()
                this.hideKeyboard()
            }
            return@setOnEditorActionListener true
        }
    }

    private fun subscribeObservers() {
        viewModel.getCardsList().observe(this, Observer {
            if (it == null) {
                Toast.makeText(activity, "List is empty", Toast.LENGTH_SHORT).show()
            } else {
                boardInfo = viewModel.boardInfo
                adapter.setData(it)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.dispBag.clear()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement FragmentListener")
        }
    }

    companion object {
        fun newInstance(id: String): Fragment {
            val searchFragment = SearchFragment()
            val args = Bundle()
            args.putString("boardId", id)
            searchFragment.arguments = args
            return searchFragment
        }
    }
}


