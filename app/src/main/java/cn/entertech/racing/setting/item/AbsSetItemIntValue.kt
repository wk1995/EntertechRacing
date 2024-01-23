package cn.entertech.racing.setting.item

import cn.entertech.racing.SharedPreferencesUtil
import cn.entertech.racing.base.BaseActivity
import cn.entertech.racing.log.EntertechRacingLog
import cn.entertech.racing.setting.ISettingItemFactory

abstract class AbsSetItemIntValue : ISettingItemFactory<Int>() {

    companion object{
        private const val TAG="SetItemIntValue"
    }

    override fun getValue(): Int {
        return memoryValue ?: SharedPreferencesUtil.getInt(
            getKey(),
            getDefault()
        )
    }

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