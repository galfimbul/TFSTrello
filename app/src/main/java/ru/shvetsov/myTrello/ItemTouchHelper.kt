package ru.shvetsov.myTrello

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.shvetsov.myTrello.adapters.ListOfBoardsAdapter

/**
 * Created by Alexander Shvetsov on 20.10.2019
 */
class ItemTouchHelper(val adapter: ListOfBoardsAdapter, dragDirs: Int, swipeDirs: Int) :
    ItemTouchHelper.Callback() {
    private val iconBounds = Rect()
    private val bgRect = RectF()
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val dragFlags = dragDirs
    val swipeFlags = swipeDirs
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {

        return if (viewHolder is ListOfBoardsAdapter.ListOfBoardsItemViewHolder) {
            makeMovementFlags(dragFlags, swipeFlags)

        } else {
            makeFlag(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.START)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        /**
         * Если свайпаем вправо, будет рисоваться картинка, а так же затемняться background айтема
         */
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView
            val icon = itemView.resources.getDrawable(R.drawable.ic_delete_black_24dp, itemView.context.theme)
            if (dX < 0)
                drawText(canvas, itemView, dX)
            else {
                drawBackground(canvas, itemView, dX)
                drawIcon(canvas, itemView, dX, icon)
            }
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawIcon(canvas: Canvas, itemView: View, dX: Float, icon: Drawable) {
        val iconSize = itemView.resources.getDimensionPixelSize(R.dimen.icon_size)
        val space = itemView.resources.getDimensionPixelSize(R.dimen.spacing_normal_16)

        val margin = (itemView.bottom - itemView.top - iconSize) / 2
        with(iconBounds) {
            /**
             * задаем границы иконки за экраном, чтобы обеспечить нормальный отступ от границы
             * рисуем иконку с заданными границами
             */
            left = itemView.left + dX.toInt() - space - iconSize
            top = itemView.top + margin
            right = itemView.left + dX.toInt()
            bottom = itemView.bottom - margin
        }
        icon.bounds = iconBounds
        icon.draw(canvas)

    }

    private fun drawBackground(canvas: Canvas, itemView: View, dX: Float) {

        with(bgRect) {
            left = itemView.left.toFloat()
            top = itemView.top.toFloat()
            right = itemView.left + dX
            bottom = itemView.bottom.toFloat()
        }
        with(bgPaint) {
            color = Color.LTGRAY
        }
        canvas.drawRect(bgRect, bgPaint)
    }

    private fun drawText(canvas: Canvas, itemView: View, dX: Float) {
        val space = itemView.resources.getDimensionPixelSize(R.dimen.spacing_normal_16)
        with(bgRect) {
            left = itemView.right.toFloat() + dX
            top = itemView.top.toFloat()
            right = itemView.right.toFloat()
            bottom = itemView.bottom.toFloat()
        }
        with(bgPaint) {
            color = Color.RED
            textSize = itemView.resources.getDimensionPixelSize(R.dimen.icon_size).toFloat()
        }
        canvas.drawRect(bgRect, bgPaint)
        bgPaint.color = Color.WHITE
        canvas.drawText(
            itemView.resources.getString(R.string.text_on_swipe),
            bgRect.left + space,
            bgRect.centerY() - (bgPaint.descent() + bgPaint.ascent()) / 2,
            bgPaint
        )
    }

    interface ItemTouchHelperAdapter {

        fun onItemMove(fromPosition: Int, toPosition: Int)

        fun onItemDismiss(position: Int)
    }

}