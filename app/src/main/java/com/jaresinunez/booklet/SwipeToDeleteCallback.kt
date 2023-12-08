package com.jaresinunez.booklet

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeToDeleteCallback(
    private val adapter: BookAdapter,
    private val swipeActionListener: SwipeActionListener
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    interface SwipeActionListener {
        fun onLeftSwipe(position: Int)
        fun onRightSwipe(position: Int)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // No move action
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        when (direction) {
            ItemTouchHelper.LEFT -> {
                // Handle left swipe
                swipeActionListener.onLeftSwipe(position)
            }
            ItemTouchHelper.RIGHT -> {
                // Handle right swipe
                swipeActionListener.onRightSwipe(position)
            }
        }
    }
}
