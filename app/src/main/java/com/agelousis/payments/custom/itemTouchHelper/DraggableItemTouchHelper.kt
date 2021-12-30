package com.agelousis.payments.custom.itemTouchHelper

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class DraggableItemTouchHelper(private val list: List<Any>): ItemTouchHelper.Callback() {

    private var dragFrom = -1
    private var dragTo = -1

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags( ItemTouchHelper.UP or ItemTouchHelper.DOWN,0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if(viewHolder.itemViewType != target.itemViewType)
            return false
        val fromPosition = viewHolder.bindingAdapterPosition
        val toPosition = target.bindingAdapterPosition
        if(dragFrom == -1)
            dragFrom =  fromPosition
        dragTo = toPosition

        if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
            reallyMoved(
                dragFrom = dragFrom,
                dragTo = dragTo
            )
            dragTo = -1
            dragFrom = dragTo
        }
        recyclerView.adapter?.notifyItemMoved(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    private fun reallyMoved(dragFrom: Int, dragTo: Int) {
        Collections.swap(list, dragFrom, dragTo)
    }

}