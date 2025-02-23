package com.quick.liveswitcher.ui.operatearea

import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.ui.base.BaseRecycleAdapter
import com.quick.liveswitcher.ui.previewarea.LayerController
import java.util.Collections

class ItemTouchHelperCallback(val adapter: BaseRecycleAdapter<*,RecyclerView.ViewHolder>): ItemTouchHelper.Callback() {
    private lateinit var onMoveCallback: (fromPosition: Int, toPosition: Int) -> Unit

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN // 允许上下拖动
        val swipeFlags = 0 // 不允许滑动删除
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(adapter.mDatas, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(adapter.mDatas, i, i - 1)
            }
        }
        Log.d("tag","fromPosition:$fromPosition" + "   toPosition:$toPosition")
        onMoveCallback(fromPosition, toPosition)
        adapter.notifyItemMoved(fromPosition, toPosition)
        return true
    }

    fun setOnMoveCallback(callback: (fromPosition: Int, toPosition: Int) -> Unit) {
        this.onMoveCallback = callback
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder?.itemView?.findViewById<LinearLayout>(R.id.root)?.setBackgroundResource(R.drawable.shape_color_gradient_left_radius_10)
            viewHolder?.itemView?.animate()?.scaleX(0.95f)?.scaleY(0.95f)?.alpha(0.95f)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder?.itemView?.findViewById<LinearLayout>(R.id.root)?.setBackgroundResource(R.drawable.shape_33ebebf5_left_radius_10)
        viewHolder?.itemView?.animate()?.scaleX(1f)?.scaleY(1f)?.alpha(1f)
    }
}