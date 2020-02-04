package ru.shvetsov.myTrello.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.woxthebox.draglistview.DragItemAdapter
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.dataClasses.card.Card

class ItemAdapter(
    private val list: MutableList<Card>,
    private val mLayoutId: Int,
    val mGrabHandleId: Int,
    var mDragOnLongPress: Boolean,
    private var onItemClickListener: ItemAdapterOnClickListener? = null
) : DragItemAdapter<Card, ItemAdapter.ViewHolder>() {
    init {
        itemList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(mLayoutId, parent, false)
        return ViewHolder(view, onItemClickListener)
    }

    override fun getUniqueItemId(position: Int): Long {
        return list[position].uniqIdForAdapter
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = mItemList[position]
        holder.bind(list[position])
        super.onBindViewHolder(holder, position)

    }

    fun attachDelegate(listener: ItemAdapterOnClickListener) {
        this.onItemClickListener = listener
    }

    inner class ViewHolder(itemView: View, private val listener: ItemAdapterOnClickListener?) :
        DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {
        private var mText: TextView = itemView.findViewById<View>(R.id.text) as TextView
        private var attachmentsCount: TextView = itemView.findViewById<View>(R.id.attachment_count) as TextView
        private var image: ImageView = itemView.findViewById(R.id.iv_columnItem)
        private var descExist: ImageView = itemView.findViewById(R.id.iv_desc_exist)
        private var attachmentsExist: ImageView = itemView.findViewById(R.id.iv_attachments_exist)
        fun bind(item: Card) {
            mText.text = item.name
            image.drawable.setTint(item.color)
            if (!item.attachments.isNullOrEmpty()) {
                attachmentsExist.visibility = View.VISIBLE
                attachmentsCount.text = item.attachments.size.toString()
                attachmentsCount.visibility = View.VISIBLE
            }
            if (!item.desc.isNullOrBlank()) {
                descExist.visibility = View.VISIBLE
            }
        }


        override fun onItemClicked(view: View?) {
            listener?.click(list[adapterPosition])

        }
    }

    interface ItemAdapterOnClickListener { // интерфейс для обработки нажатий на элементы списка
        fun click(card: Card)
    }
}