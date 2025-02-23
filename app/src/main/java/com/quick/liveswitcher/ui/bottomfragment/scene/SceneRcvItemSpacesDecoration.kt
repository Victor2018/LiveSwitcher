package com.quick.liveswitcher.ui.bottomfragment.scene

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.utils.getDimenPixelSize

class SceneRcvItemSpacesDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: android.graphics.Rect, view: android.view.View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildAdapterPosition(view)
        val colum = (pos % 5)
        if(colum == 0 || colum == 1 || colum == 2 || colum == 3 ){
            outRect.right =  getDimenPixelSize(R.dimen.dp_32)
        }
        outRect.bottom =  getDimenPixelSize(R.dimen.dp_32)
    }

}