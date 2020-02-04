package ru.shvetsov.myTrello.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.custom.AvatarImageView
import ru.shvetsov.myTrello.dataClasses.User

/**
 * Created by Alexander Shvetsov on 30.11.2019
 */
class AddMembersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var recyclerList: MutableList<User> = ArrayList()
    private var onItemClickListener: AddMembersItemClickListener? = null
    lateinit var adapterCardMembersList: List<String>
    private val selectedUsers: MutableList<User> = mutableListOf()

    fun attachListener(listener: AddMembersItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MemberSelectionItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.member_selection_item,
                parent,
                false
            ), onItemClickListener
        )
    }

    override fun getItemCount(): Int {
        return recyclerList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MemberSelectionItemViewHolder) {
            holder.bind(recyclerList[position], adapterCardMembersList)
        } else {
            Log.d("M_AddMembersAdapter", "Recycler Failed!!!")
        }
    }

    fun setData(list: List<User>, cardMembersList: List<String>) {
        adapterCardMembersList = cardMembersList
        recyclerList.clear()
        recyclerList.addAll(list)
        notifyDataSetChanged()
    }

    class MemberSelectionItemViewHolder(
        itemView: View,
        private val listener: AddMembersItemClickListener?
    ) :
        RecyclerView.ViewHolder(itemView) {
        private val avatar: AvatarImageView = itemView.findViewById(R.id.iv_add_member_item)
        private val memberName: TextView = itemView.findViewById(R.id.tv_add_member_item)
        private val selection: ImageView = itemView.findViewById(R.id.checkbox_add_member_item)

        fun bind(member: User, cardMembersList: List<String>) {
            if (cardMembersList.contains(member.id)) {
                selection.visibility = View.VISIBLE
            } else selection.visibility = View.GONE
            Log.d("M_AddMembersAdapter", "Recycler Bind!!!")
            if (member.avatarHash != null) {
                val avatarSource =
                    avatar.resources.getString(R.string.add_members_adapter_avatar_source, member.avatarHash)
                Glide.with(avatar).load(avatarSource).into(avatar)
            } else {
                avatar.setInitials(member.initials)
            }
            memberName.text = member.fullName
            itemView.setOnClickListener {
                listener?.click(member, itemView)
                Log.d("M_AddMembersAdapter", "click on ${member.fullName}")
            }
        }
    }

    interface AddMembersItemClickListener {
        fun click(user: User, view: View)
    }
}