package com.agelousis.payments.custom.itemTouchHelper

import android.content.Context
import android.graphics.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.custom.enumerations.SwipeAction
import com.agelousis.payments.main.enumerations.SwipeItemType

typealias SwipeActionBlock = (swipeAction: SwipeAction, position: Int) -> Unit
typealias SwipePredicateBlock = (viewHolder: RecyclerView.ViewHolder) -> Boolean
class SwipeItemTouchHelper(private val context: Context, private val swipeItemType: SwipeItemType, private val swipePredicateBlock: SwipePredicateBlock, private val swipeActionBlock: SwipeActionBlock): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {

    private val paint = Paint()

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = true

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        swipeActionBlock(if (direction == ItemTouchHelper.END) SwipeAction.LEFT else SwipeAction.RIGHT, viewHolder.adapterPosition)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val icon: Bitmap
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView
            val height = itemView.bottom.toFloat() - itemView.top.toFloat()
            val width = height / 3
            if (dX > 0) {
                paint.color = swipeItemType.getColors(context = context)[0]
                val background =
                    RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                c.drawRect(background, paint)
                icon = swipeItemType.getIcons(context = context).getOrNull(index = 0) ?: return
                val iconDest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width, itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
                c.drawBitmap(icon, null, iconDest, paint)
            } else {
                paint.color = swipeItemType.getColors(context = context)[1]
                val background = RectF(
                    itemView.right.toFloat() + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                c.drawRect(background, paint)
                icon = swipeItemType.getIcons(context = context)[1] ?: return
                val iconDest = RectF(itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                c.drawBitmap(icon, null, iconDest, paint)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return if (swipePredicateBlock(viewHolder))
            super.getSwipeDirs(recyclerView, viewHolder)
        else
            0

    }

}