package cn.entertech.racing

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface IRecyclerViewItemClickListener {
    fun onItemClick(rv: RecyclerView?, clickView: View, index: Int)
}