package cn.entertech.racing

import android.app.Application

class RacingApplication : Application() {
    companion object {
        @JvmStatic
        var application: Application? = null


        @JvmStatic
        fun getInstance(): Application = application!!
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        SharedPreferencesUtil.init(this)
    }
}