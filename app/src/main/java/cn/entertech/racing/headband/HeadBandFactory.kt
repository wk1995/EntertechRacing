package cn.entertech.racing.headband

import cn.entertech.racing.R
import cn.entertech.racing.SharedPreferencesUtil
import cn.entertech.racing.base.BaseActivity
import cn.entertech.racing.log.EntertechRacingLog
import cn.entertech.racing.setting.ISettingItemFactory

object HeadBandFactory : ISettingItemFactory<String>() {
    private const val TAG = "HeadBandFactory"
    private const val HEAD_BAND_MAC = "HeadBandFactory"
    override fun getNameResId() = R.string.racing_setting_item_red_headband

    override fun getKey() = HEAD_BAND_MAC

    override fun getValue(): String = memoryValue ?: SharedPreferencesUtil.getString(
        getKey(),
        getDefault()
    ) ?: getDefault()

    override fun saveValue(t: String): Boolean {
        return try {
            SharedPreferencesUtil.putString(getKey(), t)
            memoryValue = t
            true
        } catch (e: Exception) {
            EntertechRacingLog.e(TAG, "saveValue error : ${e.message}")
            false
        }

    }

    override fun getDefault(): String {
        return ""
    }

    override fun showDialog(context: BaseActivity, change: () -> Unit) {
        EditMacAddressDialog().show(context.supportFragmentManager, "")
    }
}