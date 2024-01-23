package cn.entertech.racing.setting.item

import cn.entertech.racing.R
import cn.entertech.racing.SharedPreferencesUtil
import cn.entertech.racing.base.BaseActivity
import cn.entertech.racing.log.EntertechRacingLog
import cn.entertech.racing.setting.ISettingItemFactory

object TrackRedThreshold : AbsSetItemIntValue() {
    private const val TRACK_BLUE_THRESHOLD = "TrackRedThreshold"
    private const val DEFAULT_TRACK_RED_THRESHOLD = 50
    private const val TAG = "TrackRedThreshold"

    override fun getNameResId() = R.string.racing_setting_item_track_red_threshold

    override fun getKey() = TRACK_BLUE_THRESHOLD

    override fun getDefault() = DEFAULT_TRACK_RED_THRESHOLD

}