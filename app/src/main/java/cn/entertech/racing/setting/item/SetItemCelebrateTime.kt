package cn.entertech.racing.setting.item

import android.content.Context
import cn.entertech.racing.R

object SetItemCelebrateTime : AbsSetItemIntValue() {
    private const val SET_ITEM_CELEBRATE_TIME = "SetItemCelebrateTime"
    private const val TAG = "SetItemCelebrateTime"
    override fun getNameResId() = R.string.racing_setting_item_time_threshold

    override fun getKey() = SET_ITEM_CELEBRATE_TIME
    override fun getDefault() = 5
    override fun getShowText(context: Context): String =
        "${
            context.getString(
                getNameResId()
            )
        }|${getValue()}秒钟"
}