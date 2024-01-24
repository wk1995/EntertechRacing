package cn.entertech.racing.headband

import cn.entertech.racing.SharedPreferencesUtil
import cn.entertech.racing.base.BaseActivity
import cn.entertech.racing.log.EntertechRacingLog
import cn.entertech.racing.setting.ISettingItemFactory

abstract class BaseHeadBandFactory : ISettingItemFactory<String>() {

    companion object{
        private const val TAG = "HeadBandFactory"
    }


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
        EditMacAddressDialog {
            if (it != memoryValue) {
                if (saveValue(it)) {
                    change()
                }
            }
        }.show(context.supportFragmentManager, "")
    }
}