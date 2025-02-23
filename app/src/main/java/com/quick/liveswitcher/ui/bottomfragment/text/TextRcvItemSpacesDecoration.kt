package com.quick.liveswitcher.ui.bottomfragment.text

import androidx.recyclerview.widget.RecyclerView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.utils.getDimenPixelSize

class TextRcvItemSpacesDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: android.graphics.Rect, view: android.view.View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom =  getDimenPixelSize(R.dimen.dp_32)
    }

}