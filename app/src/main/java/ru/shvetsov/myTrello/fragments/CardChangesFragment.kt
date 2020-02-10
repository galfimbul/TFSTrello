package ru.shvetsov.myTrello.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_changes_fragment.*
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.adapters.CardChangesAdapter
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.di.dagger.MyApp
import ru.shvetsov.myTrello.viewmodels.CardChangesViewModel
import ru.shvetsov.myTrello.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 26.11.2019
 */
class CardChangesFragment : Fragment() {
    lateinit var card: Card
    @Inject
    lateinit var adapter: CardChangesAdapter

    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var spref: SharedPreferences
    private val viewModel by lazy {
        ViewModelProviders.of(this, factory).get(CardChangesViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        makeInject()
        viewModel.apiToken = spref.getString("access_token", "")!!
        super.onCreate(savedInstanceState)
        card = arguments?.getSerializable("card") as Card
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.card_changes_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_card_changes_title.text = getString(R.string.card_changes_fragment_title, card.name)
        subscribeObservers()
        viewModel.loadActions(card)
        rv_card_changes.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rv_card_changes.adapter = adapter
    }

    private fun makeInject() {
        val component = (requireActivity().application as MyApp).appComponent.getCardChangesSubcomponent()
        component.inject(this)
    }

    private fun subscribeObservers() {
        viewModel.getActionsList().observe(this, Observer {
            progress_cardChanges.visibility = View.GONE
            if (it.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.card_changes_fragment_rv_list_empty_toast_text),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                adapter.setData(it)
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.dispBag.clear()
    }

    companion object {
        fun newInstance(card: Card): Fragment {
            val cardChangesFragment = CardChangesFragment()
            val args = Bundle()
            args.putSerializable("card", card)
            cardChangesFragment.arguments = args
            return cardChangesFragment
        }
    }
}