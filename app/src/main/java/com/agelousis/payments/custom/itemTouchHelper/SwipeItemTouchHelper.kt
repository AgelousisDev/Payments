package com.agelousis.payments.custom.itemTouchHelper

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.R
import com.agelousis.payments.custom.enumerations.SwipeAction
import com.agelousis.payments.main.enumerations.SwipeItemType
import com.agelousis.payments.main.ui.files.viewHolders.FileViewHolder
import com.agelousis.payments.main.ui.payments.viewHolders.BalanceOverviewViewHolder
import com.agelousis.payments.main.ui.payments.viewHolders.GroupViewHolder
import com.agelousis.payments.main.ui.payments.viewHolders.PaymentViewHolder
import com.agelousis.payments.utils.extensions.secondOrNull

typealias SwipeActionBlock = (swipeAction: SwipeAction, swipeItemType: SwipeItemType?, position: Int) -> Unit
typealias SwipePredicateBlock = (viewHolder: RecyclerView.ViewHolder) -> Boolean
class SwipeItemTouchHelper(
    private val context: Context,
    private val marginStart: Float? = null,
    private val swipePredicateBlock: SwipePredicateBlock,
    private val swipeActionBlock: SwipeActionBlock
): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {

    private val paint = Paint()

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = true

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        swipeActionBlock(
            if (direction == ItemTouchHelper.END) SwipeAction.LEFT else SwipeAction.RIGHT,
            when(viewHolder) {
                is GroupViewHolder,
                is PaymentViewHolder ->
                    SwipeItemType.CLIENT_ITEM
                is BalanceOverviewViewHolder ->
                    SwipeItemType.BALANCE_OVERVIEW_ITEM
                is FileViewHolder ->
                    SwipeItemType.PDF_ITEM
                else ->
                    null
            },
            viewHolder.adapterPosition
        )
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val swipeItemType = when(viewHolder) {
            is GroupViewHolder,
            is PaymentViewHolder ->
                SwipeItemType.CLIENT_ITEM
            is BalanceOverviewViewHolder ->
                SwipeItemType.BALANCE_OVERVIEW_ITEM
            is FileViewHolder ->
                SwipeItemType.PDF_ITEM
            else ->
                null
        }
        val icon: Bitmap
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView
            val height = itemView.bottom.toFloat() - itemView.top.toFloat()
            val width = height / 3
            if (dX > 10) {
                paint.color = ContextCompat.getColor(context, R.color.nativeBackgroundColor)
                val background =
                    RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX + (marginStart ?: 0.0f), itemView.bottom.toFloat())
                c.drawRect(background, paint)
                icon = swipeItemType?.getIcons(context = context)?.firstOrNull() ?: return
                val iconDest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width, itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
                c.drawBitmap(icon, null, iconDest, paint)
            } else if (dX < -10) {
                paint.color = ContextCompat.getColor(context, R.color.nativeBackgroundColor)
                val background = RectF(
                    itemView.right.toFloat() + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                c.drawRect(background, paint)
                icon = swipeItemType?.getIcons(context = context)?.secondOrNull() ?: return
                val iconDest = RectF(itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                c.drawBitmap(icon, null, iconDest, paint)
            }
        }
    }

    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return if (swipePredicateBlock(viewHolder))
            super.getSwipeDirs(recyclerView, viewHolder)
        else
            0

    }

}