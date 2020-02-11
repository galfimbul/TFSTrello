package ru.shvetsov.myTrello.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.member_selection_item.view.*
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.adapters.AddMembersAdapter
import ru.shvetsov.myTrello.dataClasses.User
import ru.shvetsov.myTrello.di.dagger.AppTest
import ru.shvetsov.myTrello.extensions.showError
import ru.shvetsov.myTrello.viewmodels.AddCardMembersViewModel
import ru.shvetsov.myTrello.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 10.10.2019
 */
class AddMembersDialogFragment : DialogFragment() {
    lateinit var cardMembersList: List<String>
    lateinit var adapter: AddMembersAdapter
    private lateinit var viewModel: AddCardMembersViewModel
    @Inject
    lateinit var factory: ViewModelProviderFactory
    @Inject
    lateinit var spref: SharedPreferences
    val selectedUsers = mutableListOf<User>()
    lateinit var boardId: String
    lateinit var cardId: String
    lateinit var boardMembersList: List<User>
    lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeInject()
        cardMembersList = arguments?.getStringArrayList("cardMembers") as ArrayList
        cardId = arguments?.getString("cardId", "").orEmpty()
        boardId = arguments?.getString("boardId", "").orEmpty()
        println(cardMembersList.joinToString { it })
        viewModel = activity?.run {
            ViewModelProviders
                .of(this, factory)
                .get(AddCardMembersViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val token = spref.getString("access_token", "").orEmpty()

        viewModel.loadMembers(boardId, token)
        retainInstance = true

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        adapter = AddMembersAdapter()
        val layout = LayoutInflater.from(this.context).inflate(R.layout.add_members_dialog_layout, null)
        progressBar = layout.findViewById(R.id.progress_add_members)
        val recycler = layout.findViewById<RecyclerView>(R.id.rv_add_member)
        val builder = AlertDialog.Builder(this.context)
        with(builder) {
            setTitle(getString(R.string.add_members_dialog_fragment_title))
            setMessage(getString(R.string.add_members_dialog_fragment_message))
            setView(layout)
            setNeutralButton(getString(R.string.add_members_dialog_fragment_neutral_btn)) { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton(getString(R.string.add_members_dialog_fragment_positive_btn)) { dialog, _ ->
                recycler.forEach {
                    if (it.checkbox_add_member_item.isVisible) {
                        selectedUsers.add(boardMembersList[recycler.indexOfChild(it)])
                    }
                }
                Log.d("M_AddMembersTest", "selected users count is: ${selectedUsers.size}")
                val string = StringBuilder()
                selectedUsers.forEach {
                    string.append(it.fullName).append(" ")
                }
                val result = selectedUsers.joinToString { it.id }
                Toast.makeText(
                    requireContext(),
                    getString(R.string.add_members_dialog_fragment_positive_btn_toast_text, string.trim()),
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.addMembersToCard(cardId, result)
                dialog.dismiss()
            }
        }

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        adapter.attachListener(object : AddMembersAdapter.AddMembersItemClickListener {
            override fun click(user: User, view: View) {
                if (view.checkbox_add_member_item.isVisible) {
                    view.checkbox_add_member_item.visibility = View.GONE
                } else {
                    view.checkbox_add_member_item.visibility = View.VISIBLE
                }
                Log.d("M_AddMembersTest", "selected items count is: ${selectedUsers.size}")
            }

        })
        subscribeObservers()
        return builder.create()
    }

    private fun makeInject() {
        val component = (requireActivity().application as AppTest).appComponent.getAddMemberSubcomponent()
        component.inject(this)
    }

    private fun subscribeObservers() {
        viewModel.boardMembersList.observe(this, Observer {
            boardMembersList = it
            adapter.setData(boardMembersList, cardMembersList)
            progressBar.visibility = View.GONE
        })
        viewModel.error.observe(this, Observer { errorMessage ->
            showError(errorStringId = errorMessage)
        })
    }

    companion object {
        fun newInstance(cardId: String, cardMembers: ArrayList<String>, boardId: String): DialogFragment {
            val addMembersTest = AddMembersDialogFragment()
            val args = Bundle() // bundle для параметров dialogFragment
            args.putString("cardId", cardId)
            args.putString("boardId", boardId)
            args.putStringArrayList("cardMembers", cardMembers)
            addMembersTest.arguments = args
            return addMembersTest
        }
    }
}
