package ru.shvetsov.myTrello.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.dataClasses.card.Card

/**
 * Created by Alexander Shvetsov on 27.11.2019
 */
class SearchFragmentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var recyclerList: MutableList<Card> = ArrayList()
    private var onItemClickListener: Delegate? = null // обработчик нажатий для элементов RecyclerView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CardItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.column_item,
                parent,
                false
            ), onItemClickListener
        )
    }

    override fun getItemCount(): Int {
        return recyclerList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CardItemViewHolder) {
            holder.bind(recyclerList[position])
        }
    }

    fun attachDelegate(delegate: Delegate) { // привязка прослушивателя к Activity
        this.onItemClickListener = delegate
    }

    fun setData(list: List<Card>) {
        recyclerList.clear()
        recyclerList.addAll(list)
        notifyDataSetChanged()
    }

    class CardItemViewHolder(itemView: View, private val delegate: Delegate?) : RecyclerView.ViewHolder(itemView) {
        private var mText: TextView = itemView.findViewById<View>(R.id.text) as TextView
        private var attachmentsCount: TextView = itemView.findViewById<View>(R.id.attachment_count) as TextView
        private var image: ImageView = itemView.findViewById(R.id.iv_columnItem)
        private var descExist: ImageView = itemView.findViewById(R.id.iv_desc_exist)
        private var attachmentsExist: ImageView = itemView.findViewById(R.id.iv_attachments_exist)

        fun bind(item: Card) {
            mText.text = item.name
            image.drawable.setTint(Color.GREEN)
            if (!item.attachments.isNullOrEmpty()) {
                attachmentsExist.visibility = View.VISIBLE
                attachmentsCount.text = item.attachments.size.toString()
                attachmentsCount.visibility = View.VISIBLE
            }
            if (!item.desc.isNullOrBlank()) {
                descExist.visibility = View.VISIBLE
            }
            itemView.setOnClickListener {
                delegate?.click(item)
            }
        }
    }

    interface Delegate {
        fun click(card: Card)
    }
}
