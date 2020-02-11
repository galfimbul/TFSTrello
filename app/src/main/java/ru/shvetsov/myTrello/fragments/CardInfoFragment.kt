package ru.shvetsov.myTrello.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.card_info_fragment.*
import kotlinx.android.synthetic.main.card_info_fragment.view.*
import kotlinx.android.synthetic.main.file_attachment_item.view.*
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.custom.AvatarImageView
import ru.shvetsov.myTrello.dataClasses.BoardInfo
import ru.shvetsov.myTrello.dataClasses.User
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.di.dagger.AppTest
import ru.shvetsov.myTrello.extensions.dpToPx
import ru.shvetsov.myTrello.extensions.spToPx
import ru.shvetsov.myTrello.interfaces.FragmentListener
import ru.shvetsov.myTrello.viewmodels.AddCardMembersViewModel
import ru.shvetsov.myTrello.viewmodels.CardInfoViewModel
import ru.shvetsov.myTrello.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 26.11.2019
 */
class CardInfoFragment : Fragment() {
    private var listener: FragmentListener? = null
    lateinit var card: Card
    lateinit var boardInfo: BoardInfo
    lateinit var cardId: String
    lateinit var boardName: String
    lateinit var columnName: String
    lateinit var addMembersViewModel: AddCardMembersViewModel
    lateinit var cardMembers: ArrayList<String>
    @Inject
    lateinit var factory: ViewModelProviderFactory
    @Inject
    lateinit var addMembersViewModelfactory: ViewModelProviderFactory
    private val viewModel by lazy {
        ViewModelProviders.of(this, factory).get(CardInfoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeInject()
        boardInfo = arguments?.getSerializable("board") as BoardInfo
        cardId = arguments?.getString("cardId", "")!!
        activity?.run {
            addMembersViewModel =
                ViewModelProviders.of(this, addMembersViewModelfactory)[AddCardMembersViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.card_info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCardFromServer(cardId)
        subscribeObservers()


        btn_card_info_changes.setOnClickListener {
            listener?.openCardChanges(card)
        }


    }

    private fun makeInject() {
        val component = (requireActivity().application as AppTest).appComponent.getCardInfoSubcomponent()
        component.inject(this)
    }

    private fun subscribeObservers() {
        viewModel.card.observe(this, Observer {
            if (it != null) {
                progress_card_info.visibility = View.GONE
                card = it
                boardName = card.board.name
                columnName = card.list.name
                initTextViews()
                initMembers()
            }
        })
        addMembersViewModel.cardFromServer.observe(this, Observer { result ->
            val cardMembersList: MutableList<User> = mutableListOf()
            boardInfo.members.forEach {
                if (result.idMembers.contains(it.id)) {
                    cardMembersList.add(it)
                }
            }
            if (cardMembersList.isEmpty()) {
                card.members.clear()
                initMembers()
            } else {
                card.members.clear()
                card.members.addAll(cardMembersList)
                cardMembers.clear()
                cardMembersList.forEach {
                    cardMembers.add(it.id)
                }

                initMembers()
            }
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            showError(errorMessage)
        })

    }

    private fun showError(errorMessage: Int) {
        Toast.makeText(requireContext(), getString(errorMessage), Toast.LENGTH_SHORT).show()
    }

    private fun initMembers() {
        ll_card_info_members.removeAllViews()
        if (card.board.idOrganization == null) {
            return
        }
        val width = activity!!.dpToPx(50).toInt()
        val height = activity!!.dpToPx(50).toInt()
        val membersIcon = createMembersIcon(width, height)
        ll_card_info_members.addView(membersIcon)

        val addMemberImageView = createAvatarImageView(width, height)
        addMemberImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_add_white_24dp, activity!!.theme))
        addMemberImageView.setOnClickListener {
            clickOnPlus()
        }
        if (card.members.isNotEmpty()) {
            card.members.forEach {
                val imageView = createAvatarImageView(width, height)
                if (it.avatarHash != null) {
                    val avatarSource = getString(R.string.avatar_url, it.avatarHash)
                    Glide.with(imageView).load(avatarSource).into(imageView)

                } else {
                    imageView.setInitials(it.initials)
                }
                ll_card_info_members.addView(imageView)
            }
        }
        ll_card_info_members.addView(addMemberImageView)
        hor_scroll_card_info_members.visibility = View.VISIBLE
        ll_card_info_members.visibility = View.VISIBLE
        cardMembers = card.idMembers as ArrayList
    }

    private fun createMembersIcon(width: Int, height: Int): ImageView {
        val imageView = ImageView(activity!!)
        imageView.setImageDrawable(resources.getDrawable(R.drawable.ic_add_member, activity!!.theme))
        imageView.layoutParams = LinearLayout.LayoutParams(width, height).apply {
            setMargins(activity!!.dpToPx(8).toInt())
        }
        return imageView
    }

    private fun createAvatarImageView(width: Int, height: Int): AvatarImageView {
        val imageView = AvatarImageView(activity!!)
        imageView.layoutParams = LinearLayout.LayoutParams(width, height).apply {
            setMargins(activity!!.dpToPx(8).toInt())
        }
        return imageView
    }

    private fun initTextViews() {
        toolbar.tv_toolbar_title.text = card.name
        toolbar.tv_toolbar_subtitle.text = getString(R.string.card_info_fragment_subtitle, boardName, columnName)
        tv_card_info_desc.text = card.desc
        tv_card_info_attachments.text = getString(R.string.card_info_fragment_attachment_title)
        if (card.attachments.isNotEmpty()) {
            initAttachments()
        }
    }

    private fun clickOnPlus() {
        val boardId = boardInfo.id
        val fragmentManager = childFragmentManager
        val addMembers = AddMembersDialogFragment.newInstance(cardId, cardMembers, boardId)
        addMembers.show(fragmentManager, "add_members_fragment")
    }

    private fun initAttachments() {
        val list = card.attachments
        list.forEach { attachment ->
            if (attachment.previews.isNotEmpty()) {
                val imageView = ImageView(activity)
                setLayoutParameters(imageView)
                Glide.with(imageView).load(attachment.previews[3].url).into(imageView)
                ll_card_info_attachment_images.addView(imageView)
                imageView.setOnClickListener {
                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(this.id, ShowAttachmentFragment.newInstance(attachment.url))
                        .addToBackStack(null)
                        .commit()
                }

            } else {
                ll_card_info_attachment_files.addView(inflateTextView(attachment.name, attachment.url))
            }
        }
    }

    private fun inflateTextView(text: String, url: String): View {
        val view =
            LayoutInflater.from(activity).inflate(R.layout.file_attachment_item, ll_card_info_attachment_files, false)
        view.tv_attachment_info.text = text

        view.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(this.id, ShowAttachmentFragment.newInstance(url, true))
                .addToBackStack(null)
                .commit()
        }
        return view
    }

    private fun setLayoutParameters(view: View) {
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val layoutParams = LinearLayout.LayoutParams(width, height)
        layoutParams.marginStart = activity!!.dpToPx(4).toInt()
        view.layoutParams = layoutParams
        when (view) {
            is ImageView -> {
                view.scaleType = ImageView.ScaleType.FIT_XY
            }
            is TextView -> {
                view.textSize = activity!!.spToPx(16)
                view.setTextColor(Color.BLACK)
                layoutParams.topMargin = activity!!.dpToPx(4).toInt()
                layoutParams.bottomMargin = activity!!.dpToPx(4).toInt()
            }
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

    companion object {
        fun newInstance(cardId: String, boardInfo: BoardInfo): Fragment {
            val cardInfoFragment = CardInfoFragment()
            val args = Bundle()
            args.putString("cardId", cardId)
            args.putSerializable("board", boardInfo)
            cardInfoFragment.arguments = args
            return cardInfoFragment
        }
    }
}