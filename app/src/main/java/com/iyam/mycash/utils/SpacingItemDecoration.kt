package com.iyam.mycash.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val isLeft = position % 2 == 0

        outRect.top = space / 2
        outRect.bottom = space / 2

        if (isLeft) {
            outRect.left = 0
            outRect.right = space
        } else {
            outRect.left = space
            outRect.right = 0
        }
    }
}
