package cn.entertech.racing.setting.item

import android.content.Context
import cn.entertech.racing.R
import cn.entertech.racing.SharedPreferencesUtil
import cn.entertech.racing.base.BaseActivity
import cn.entertech.racing.log.EntertechRacingLog
import cn.entertech.racing.setting.ISettingItemFactory

/**
 * 每局时间
 * */
object SettingTimeEachRound : ISettingItemFactory<Int>() {
    private const val SETTING_ITEM_EACH_ROUND_TIME = "SettingTimeEachRound"
    private const val TAG = "SettingTimeEachRound"
    override fun getNameResId() = R.string.racing_setting_item_time_each_round

    override fun getKey() = SETTING_ITEM_EACH_ROUND_TIME

    override fun getValue() = memoryValue ?: SharedPreferencesUtil.getInt(getKey(), getDefault())

    override fun saveValue(t: Int): Boolean {
        return try {
            SharedPreferencesUtil.putInt(getKey(), t)
            this.memoryValue = t
            true
        } catch (e: Exception) {
            EntertechRacingLog.e(TAG, "saveValue ${e.message}")
            false
        }
    }

    /**
     * 默认1分钟
     * */
    override fun getDefault() = 60

    override fun showDialog(context: BaseActivity, change: () -> Unit) {

    }

    override fun getShowText(context: Context): String {
        return "${
            context.getString(
                getNameResId()
            )
        }|${getTimeString()}"
    }

    private fun getTimeString(): String {
        val min = getValue() / 60
        val se = getValue() % 60
        return if (min == 0) {
            "${se}秒钟"
        } else {
            if (se != 0) {
                "${min}分钟${se}秒"
            } else {
                "${min}分钟"
            }

        }
    }
}

