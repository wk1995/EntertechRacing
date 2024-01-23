package cn.entertech.racing.setting.item

import cn.entertech.racing.R

object SetItemCelebrateThreshold : AbsSetItemIntValue() {
    private const val SET_ITEM_CELEBRATE_THRESHOLD = "SetItemCelebrateThreshold"
    private const val TAG = "SetItemCelebrateThreshold"
    override fun getNameResId() = R.string.racing_setting_item_celebrate_threshold

    override fun getKey() = SET_ITEM_CELEBRATE_THRESHOLD
    override fun getDefault() = 80

}