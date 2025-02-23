package com.quick.liveswitcher.ui.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecycleAdapter<T,VH: RecyclerView.ViewHolder>(
    var context: Context?, var listener:AdapterView.OnItemClickListener?):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var TAG = javaClass.simpleName

    var mDatas: ArrayList<T> = ArrayList()

    var dataPositionMap = LinkedHashMap<Int,Int>()//有序的map可以按照list选中顺序取出
    var dataMap = LinkedHashMap<Int,T?>()//有序的map可以按照list选中顺序取出

    abstract fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): VH
    abstract fun onBindContentVHolder(viewHolder: VH, data: T?, position: Int)

    fun inflate(layoutId: Int,parent: ViewGroup): View {
        var inflater = LayoutInflater.from(context)
        return inflater.inflate(layoutId,parent, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return onCreateContentVHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var item = getItem(position)
        onBindContentVHolder(holder as VH, item, position)
    }

    fun getContentItemCount(): Int {
        return if (mDatas == null) 0 else mDatas.size
    }

    override fun getItemCount(): Int {
        return getContentItemCount()
    }

    /**
     * 获取元素
     *
     * @param position
     * @return
     */
    fun getItem(position: Int): T? {
        //防止越界
        val index = if (position >= 0 && position < mDatas.size) position else 0
        return if (mDatas == null || mDatas.size == 0) {
            null
        } else mDatas.get(index)
    }

    /**
     * 添加元素
     *
     * @param item
     */
    fun add(item: T?) {
        if (item != null) {
            mDatas.add(item)
        }
    }

    /**
     * 添加元素
     *
     * @param item
     */
    fun add(index: Int, item: T?) {
        if (item != null) {
            mDatas.add(index, item)
        }
    }

    fun add(items: List<T>?) {
        if (items != null) {
            mDatas.addAll(items)
        }
    }

    /**
     * 重置元素
     *
     * @param items
     */
    fun setDatas(items: List<T>) {
        mDatas.clear()
        add(items)
    }

    /**
     * 移除
     *
     * @param index
     */

    fun removeItem(index: Int): T? {
        if (index >= 0 && index < mDatas.size) {
            return mDatas.removeAt(index)
        }
        return null
    }

    fun replaceItem(index: Int,data: T?) {
        if (index >= 0 && index < mDatas.size) {
            if (data != null) {
                mDatas[index] = data
            }
        }
    }

    fun getDatas(): List<T>? {
        return mDatas
    }

    fun clear() {
        mDatas.clear()
    }

    fun showData(list: List<T>?) {
        clear()
        add(list)
        notifyDataSetChanged()
    }

    fun showData(list: List<T>?,mEmptyView: View?, rv: RecyclerView?) {
        if (list == null || list?.size == 0) {
            mEmptyView?.visibility = View.VISIBLE
            clear()
            notifyDataSetChanged()
            return
        }
        mEmptyView?.visibility = View.GONE
        rv?.visibility = View.VISIBLE
        clear()
        add(list)
        notifyDataSetChanged()
    }

}