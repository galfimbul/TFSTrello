package ru.shvetsov.myTrello.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.custom.AvatarImageView
import ru.shvetsov.myTrello.utils.mappers.CardChangesUiModel

/**
 * Created by Alexander Shvetsov on 27.11.2019
 */
class CardChangesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val recyclerList: MutableList<CardChangesUiModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CardChangesItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_action_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return recyclerList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CardChangesItemViewHolder) {
            holder.bind(recyclerList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setData(list: List<CardChangesUiModel>) {
        recyclerList.clear()
        recyclerList.addAll(list)
        notifyDataSetChanged()
    }

    class CardChangesItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val info: TextView = itemView.findViewById(R.id.tv_action_info)
        private val dateView: TextView = itemView.findViewById(R.id.tv_action_date)
        private val imageView: ImageView = itemView.findViewById(R.id.iv_card_action_item)
        private val avatar: AvatarImageView = itemView.findViewById(R.id.iv_creator_avatar)

        fun bind(item: CardChangesUiModel) {
            dateView.text = item.dateText
            if (item.avatarHash.isEmpty()) {
                avatar.setInitials(item.initials)
            } else {
                val avatarUrl = avatar.resources.getString(R.string.avatar_url, item.avatarHash)
                Glide.with(avatar).load(avatarUrl).into(avatar)
            }
            if (item.hasImage) {
                Glide.with(imageView).load(item.previewUrl).into(imageView)
                imageView.visibility = View.VISIBLE
            }

            if (item.listBeforeName != null) {
                info.text = info.resources.getString(
                    item.infoResourceId, item.editorName,
                    item.listBeforeName, item.listAfterName
                )
                return
            }

            if (item.memberText != null) {
                info.text = info.resources.getString(
                    item.infoResourceId, item.editorName,
                    item.memberText
                )
                return
            }

            if (item.fileName.isNotEmpty()) {
                info.text = info.resources.getString(
                    item.infoResourceId, item.editorName,
                    item.fileName
                )
            } else {
                info.text = info.resources.getString(
                    item.infoResourceId, item.editorName
                )
            }
        }


    }
}
