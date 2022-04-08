package com.agelousis.payments.custom.itemDecoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView

typealias PredicateBlock = (position: Int) -> Boolean
class DividerItemRecyclerViewDecorator(context: Context, private val margin: Int, private val predicateBlock: PredicateBlock = { true }): RecyclerView.ItemDecoration() {

    private var divider: Drawable? = null

    init {
        val styledAttributes = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        divider = styledAttributes.getDrawable(0)
        styledAttributes.recycle()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val right = parent.width - margin
        val itemCount = parent.adapter?.itemCount ?: return
        for (index in 0 until itemCount) {
            val view = parent.getChildAt(index)
            view?.let {
                if (parent.getChildAdapterPosition(it) == parent.adapter?.itemCount ?: 0 - 1)
                    return@let
                if (predicateBlock(parent.getChildAdapterPosition(it))) {
                    val params = it.layoutParams as? RecyclerView.LayoutParams
                    val top = it.bottom + (params?.bottomMargin ?: 0)
                    val bottom = top + (divider?.intrinsicHeight ?: 0)
                    divider?.setBounds(margin, top, right, bottom)
                    divider?.draw(c)
                }
            }
        }
    }

}