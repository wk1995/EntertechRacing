package cn.entertech.racing.setting

import android.content.Context
import cn.entertech.racing.base.BaseActivity

abstract class ISettingItemFactory<T> {

    protected var memoryValue: T? = null

    abstract fun getNameResId(): Int

    abstract fun getKey(): String


    abstract fun getValue(): T

    /**
     * @return true 保存成功
     * */
    abstract fun saveValue(t: T): Boolean


    protected abstract fun getDefault(): T


    fun getShowText(context: Context): String =
        "${
            context.getString(
                getNameResId()
            )
        }|${getValue()}"


    abstract fun showDialog(context: BaseActivity,change:()->Unit)
}