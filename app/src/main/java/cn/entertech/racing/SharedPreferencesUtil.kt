package cn.entertech.racing

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesUtil {
    private var mSharedPreferences: SharedPreferences? = null
    private var edit: SharedPreferences.Editor? = null

    fun init(application: RacingApplication) {
        mSharedPreferences =
            application.getSharedPreferences(application.packageName, Context.MODE_PRIVATE)
        edit = mSharedPreferences?.edit()
    }

    fun putString(key: String, value: String) {
        edit?.putString(key, value)?.apply()
    }

    fun getString(key: String, default: String?): String? {
        return mSharedPreferences?.getString(key, default) ?: default
    }

    fun getInt(key: String, default: Int): Int {
        return mSharedPreferences?.getInt(key, default) ?: default
    }

    fun putInt(key: String, value: Int) {
        edit?.putInt(key, value)?.apply()
    }
}