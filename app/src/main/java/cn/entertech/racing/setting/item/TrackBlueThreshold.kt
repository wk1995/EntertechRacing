package cn.entertech.racing.setting.item

import cn.entertech.racing.R
import cn.entertech.racing.SharedPreferencesUtil
import cn.entertech.racing.base.BaseActivity
import cn.entertech.racing.log.EntertechRacingLog
import cn.entertech.racing.setting.ISettingItemFactory

object TrackBlueThreshold : AbsSetItemIntValue() {
    private const val TRACK_BLUE_THRESHOLD = "TrackBlueThreshold"
    private const val TAG = "TrackBlueThreshold"

    private const val DEFAULT_TRACK_BLUE_THRESHOLD = 50

    override fun getNameResId() = R.string.racing_setting_item_track_blue_threshold

    override fun getKey() = TRACK_BLUE_THRESHOLD



    override fun getDefault() = DEFAULT_TRACK_BLUE_THRESHOLD


}