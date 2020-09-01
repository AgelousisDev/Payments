package com.agelousis.monthlyfees.custom.itemDecoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

class DividerItemRecyclerViewDecorator(context: Context, private val margin: Int): RecyclerView.ItemDecoration() {

    private var divider: Drawable? = null

    init {
        val styledAttributes = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        divider = styledAttributes.getDrawable(0)
        styledAttributes.recycle()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val right = parent.width - margin
        parent.children.forEachIndexed { index, view ->
            if (index < parent.childCount - 1) {
                val params = view.layoutParams as? RecyclerView.LayoutParams
                val top = view.bottom + (params?.bottomMargin ?: 0)
                val bottom = top + (divider?.intrinsicHeight ?: 0)
                divider?.setBounds(margin, top, right, bottom)
                divider?.draw(c)
            }
        }
    }

}