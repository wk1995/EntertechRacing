package cn.entertech.racing.setting.item

import cn.entertech.racing.R
import cn.entertech.racing.SharedPreferencesUtil
import cn.entertech.racing.base.BaseActivity
import cn.entertech.racing.log.EntertechRacingLog
import cn.entertech.racing.setting.ISettingItemFactory

object TrackRedThreshold : ISettingItemFactory<Int>() {
    private const val TRACK_BLUE_THRESHOLD = "TrackRedThreshold"
    private const val DEFAULT_TRACK_RED_THRESHOLD = 50
    private const val TAG = "TrackRedThreshold"

    override fun getNameResId() = R.string.racing_setting_item_track_red_threshold

    override fun getKey() = TRACK_BLUE_THRESHOLD

    override fun saveValue(value: Int): Boolean {
        return try {
            SharedPreferencesUtil.putInt(getKey(), value)
            this.memoryValue = value
            true
        } catch (e: Exception) {
            EntertechRacingLog.e(TAG, "saveValue ${e.message}")
            false
        }
    }

    override fun getValue(): Int {
        return memoryValue ?: SharedPreferencesUtil.getInt(
            getKey(),
            getDefault()
        )
    }

    override fun getDefault() = DEFAULT_TRACK_RED_THRESHOLD


    override fun showDialog(context: BaseActivity, change: () -> Unit) {
        SetHeadbandThresholdDialog {
            if (it != memoryValue) {
                if (saveValue(it)) {
                    change()
                }
            }

        }.show(context.supportFragmentManager, "")
    }
}