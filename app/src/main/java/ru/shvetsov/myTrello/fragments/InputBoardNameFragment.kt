package ru.shvetsov.myTrello.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.dataClasses.Organization
import ru.shvetsov.myTrello.di.dagger.AppTest
import ru.shvetsov.myTrello.viewmodels.InputBoardNameViewModel
import ru.shvetsov.myTrello.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 10.10.2019
 */
class InputBoardNameFragment : DialogFragment() {
    private lateinit var selection: String
    lateinit var category: ArrayList<String> // Пустой список который придет из ретрофита
    lateinit var teamsList: List<Organization>
    lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var viewModel: InputBoardNameViewModel
    @Inject
    lateinit var factory: ViewModelProviderFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val component = (requireActivity().application as AppTest).appComponent.getInputBoardNameSubcomponent()
        component.inject(this)
        viewModel = activity?.run {
            ViewModelProviders
                .of(this, factory)
                .get(InputBoardNameViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        val spref = activity!!.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val token = spref.getString("access_token", "")
        category = ArrayList()
        arrayAdapter = ArrayAdapter(activity, android.R.layout.simple_list_item_single_choice, category)
        setListToCheckBox()
        viewModel.loadData(token!!)
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(parentFragment!!.context)
        val view = LayoutInflater.from(this.context).inflate(R.layout.input_board_name_fragment, null)
        val editText: EditText = view.findViewById(R.id.et_input_board_name)
        with(builder) {
            setTitle(getString(R.string.input_board_name_fragment_title))
            setView(view)
            setSingleChoiceItems(
                arrayAdapter, -1
            ) { _, which ->
                selection = category[which]
            }
            setPositiveButton(getString(R.string.input_board_name_fragment_positive_btn)) { dialog, _ ->
                sendBackResult(editText, selection)
                dialog.dismiss()
            }
            setNeutralButton(getString(R.string.input_board_name_fragment_neutral_btn)) { dialog, _ ->
                dialog.dismiss()
            }
        }
        editText.requestFocus()
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                    sendBackResult(editText, selection)
                dismiss()
            }
            return@setOnEditorActionListener true
        }
        val dialog = builder.create()
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) // показываем клавиатуру
        return dialog
    }

    private fun setListToCheckBox() {
        viewModel.getCategoryList().observe(this, Observer {
            arrayAdapter.clear()
            it.add(0, getString(R.string.input_baord_name_fragment_personal_boards_category))
            arrayAdapter.addAll(it)
        })
        viewModel.getTeamList().observe(this, Observer {
            teamsList = it
        })

    }

    private fun sendBackResult(editText: EditText, selection: String) {
        viewModel.addBoardToServer(editText.text.toString(), selection)
    }

    companion object {
        fun newInstance(): DialogFragment {
            return InputBoardNameFragment()
        }
    }


}
