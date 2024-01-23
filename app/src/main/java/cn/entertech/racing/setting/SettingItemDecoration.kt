package cn.entertech.racing.setting

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class SettingItemDecoration(private val transparentSpaceHeight: Int = 1) :
    ItemDecoration() {
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + transparentSpaceHeight
            val transparentDivider = ColorDrawable(Color.TRANSPARENT)
            transparentDivider.setBounds(left, top, right, bottom)
            transparentDivider.draw(canvas)
        }
    }
}
