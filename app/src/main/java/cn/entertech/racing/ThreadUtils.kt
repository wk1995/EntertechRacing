package cn.entertech.racing

import android.os.Looper

object ThreadUtils {
    fun currentIsMain(): Boolean {
        return Thread.currentThread() == Looper.getMainLooper().thread
    }
}