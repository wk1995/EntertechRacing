package cn.entertech.racing

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import cn.entertech.ble.BaseBleConnectManager

class RacingCompetitionViewModel : ViewModel() {

    private val blueManagerMap by lazy {
        HashMap<String, BaseBleConnectManager>()
    }

    fun connectBlueDevice() {

    }

    /**
     * 开始竞速
     * */
    fun startCompetition() {

    }

    /**
     * 结束竞速
     * */
    fun finishCompetition() {

    }

    fun gotoHandBand(context: Context) {
        //如果有至少一个头环的mac地址
        val intent =
            if (hasHeadbandMac()) {
                Intent(context, ConnectedActivity::class.java)

            } else {
                Intent(context, MacActivity::class.java)
            }
        context.startActivity(intent)
    }

    fun gotoTrack(context: Context) {
        val intent =
            if (hasTrackMac()) {
                Intent(context, ConnectedActivity::class.java)
            } else {
                Intent(context, MacActivity::class.java)
            }
        context.startActivity(intent)
    }

    fun gotoSetting(context: Context) {
        val intent = Intent(context, SettingActivity::class.java)
        context.startActivity(intent)
    }

    /**
     * 有至少一个头环的mac地址
     * */
    fun hasHeadbandMac(): Boolean = false
    fun hasTrackMac(): Boolean = false

    fun blueIsConnected(): Boolean = false
    fun redIsConnected(): Boolean = false
    fun trackIsConnected(): Boolean = false

    fun blueIsWear(): Boolean = false
    fun redIsWear(): Boolean = false
}