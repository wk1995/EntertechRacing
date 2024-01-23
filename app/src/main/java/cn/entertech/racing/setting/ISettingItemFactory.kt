package cn.entertech.racing.setting

import android.content.Context

abstract class ISettingItemFactory<T> {

    protected abstract var value: T

    abstract fun getNameResId(): Int

    abstract fun getKey(): String

    abstract fun saveValue(value: T)

    abstract fun getDefault(): T

    fun getMemoryValue(): T = value

    abstract fun getDiskValue(): T

    fun getShowText(context: Context): String =
        "${
            context.getString(
                getNameResId()
            )
        }|${value}"
}