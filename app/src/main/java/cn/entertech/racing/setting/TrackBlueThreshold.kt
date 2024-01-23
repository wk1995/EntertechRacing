package cn.entertech.racing.setting

import cn.entertech.racing.R
import cn.entertech.racing.SharedPreferencesUtil

object TrackBlueThreshold : ISettingItemFactory<Int>() {
    private const val TRACK_BLUE_THRESHOLD = "TrackBlueThreshold"
    private const val DEFAULT_TRACK_BLUE_THRESHOLD = 50
    override var value: Int = getDefault()

    override fun getNameResId() = R.string.racing_setting_item_track_blue_threshold

    override fun getKey() = TRACK_BLUE_THRESHOLD

    override fun saveValue(value: Int) {
        SharedPreferencesUtil.putInt(getKey(), value)
        this.value = value
    }

    override fun getDefault() = DEFAULT_TRACK_BLUE_THRESHOLD

    override fun getDiskValue() = SharedPreferencesUtil.getInt(getKey(), getDefault())

}