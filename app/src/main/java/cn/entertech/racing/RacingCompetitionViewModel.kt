package cn.entertech.racing

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import cn.entertech.ble.BaseBleConnectManager
import cn.entertech.racing.ble.BleManage
import cn.entertech.racing.connect.ConnectedActivity
import cn.entertech.racing.device.DeviceType
import cn.entertech.racing.setting.SettingActivity
import cn.entertech.racing.setting.SettingType

class RacingCompetitionViewModel : ViewModel() {

    companion object {
        const val BUNDLE_KEY_DEVICE_TYPE = "deviceType"
    }

    private val blueManagerMap by lazy {
        HashMap<String, BaseBleConnectManager>()
    }

    private var currentStatus: RacingStatus = RacingStatus.PRE_COMPETITION

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
                val intent = Intent(context, ConnectedActivity::class.java)
                intent.putExtra(BUNDLE_KEY_DEVICE_TYPE, DeviceType.DEVICE_TYPE_HEADBAND.name)
                intent
            } else {
                val intent = Intent(context, SettingActivity::class.java)
                intent.putExtra(
                    SettingType.BUNDLE_KEY_SETTING_TYPE,
                    SettingType.SETTINGS_HEADBAND_MAC.typeName
                )
                intent
            }
        context.startActivity(intent)
    }

    fun gotoTrack(context: Context) {
        val intent =
            if (hasTrackMac()) {
                val intent = Intent(context, ConnectedActivity::class.java)
                intent.putExtra(BUNDLE_KEY_DEVICE_TYPE, DeviceType.DEVICE_TYPE_TRACK.name)
                intent
            } else {
                val intent = Intent(context, SettingActivity::class.java)
                intent.putExtra(
                    SettingType.BUNDLE_KEY_SETTING_TYPE,
                    SettingType.SETTINGS_TRACK_MAC.typeName
                )
                intent
            }
        context.startActivity(intent)
    }

    fun gotoSetting(context: Context) {
        val intent = Intent(context, SettingActivity::class.java)
        intent.putExtra(
            SettingType.BUNDLE_KEY_SETTING_TYPE,
            SettingType.SETTINGS_SYSTEM.typeName
        )
        context.startActivity(intent)
    }

    /**
     * 有至少一个头环的mac地址
     * */
    fun hasHeadbandMac(): Boolean = true
    fun hasTrackMac(): Boolean = true

    fun blueIsConnected(): Boolean = BleManage.blueIsConnect()
    fun redIsConnected(): Boolean = BleManage.redIsConnect()
    fun trackIsConnected(): Boolean = BleManage.trackIsConnect()

    fun blueIsWear(): Boolean = false
    fun redIsWear(): Boolean = false
}