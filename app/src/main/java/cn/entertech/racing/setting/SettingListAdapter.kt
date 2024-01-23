package cn.entertech.racing.setting

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.entertech.racing.IRecyclerViewItemClickListener
import cn.entertech.racing.R

class SettingListAdapter(
    private var itemList: List<ISettingItemFactory<*>> = ArrayList(),
    private val listener: IRecyclerViewItemClickListener? = null
) :
    RecyclerView.Adapter<SettingListAdapter.SettingListVH>() {
    class SettingListVH(rootView: View, val textView: TextView) : RecyclerView.ViewHolder(rootView)

    private var target: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingListVH {
        val rootView = LayoutInflater.from(parent.context)
            .inflate(R.layout.racing_setting_list_item, parent, false)
        val text = rootView.findViewById<TextView>(R.id.tvSettingItemText)
        return SettingListVH(rootView, text)
    }

    override fun onBindViewHolder(holder: SettingListVH, position: Int) {
        val text = holder.textView
        text.text = itemList[position].getShowText(text.context)
        holder.itemView.setOnClickListener {
            listener?.onItemClick(target, holder.itemView, position)
        }
    }

    override fun getItemCount() = itemList.size


    @SuppressLint("NotifyDataSetChanged")
    fun replace(newData: List<ISettingItemFactory<*>>) {
        itemList = newData
        notifyDataSetChanged()
    }

    fun getData(): List<ISettingItemFactory<*>> {
        return itemList
    }

    fun getItemByPosition(position: Int) = itemList[position]
}