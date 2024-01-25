package cn.entertech.racing

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.entertech.affective.sdk.api.IConnectionServiceListener
import cn.entertech.affective.sdk.api.IStartAffectiveServiceLister
import cn.entertech.affective.sdk.bean.Error
import cn.entertech.racing.affective.AffectiveManage
import cn.entertech.racing.ble.BleManage
import cn.entertech.racing.connect.ConnectedActivity
import cn.entertech.racing.device.Device
import cn.entertech.racing.device.DeviceType
import cn.entertech.racing.setting.SettingActivity
import cn.entertech.racing.setting.SettingType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RacingCompetitionViewModel : ViewModel() {

    companion object {
        const val BUNDLE_KEY_DEVICE_TYPE = "deviceType"
    }

    private val _update = MutableSharedFlow<Unit>()
    val updateUi = _update.asSharedFlow()
    private val _blueAttention = MutableStateFlow(0)
    val blueAttention = _blueAttention.asStateFlow()
    private val _redAttention = MutableStateFlow(0)
    val redAttention = _redAttention.asStateFlow()
    private var currentStatus: RacingStatus = RacingStatus.PRE_COMPETITION


    private val deviceDisconnectListenerMap by lazy {
        HashMap<Device, (String) -> Unit>()
    }


    private val deviceRawDataListenerMap by lazy {
        HashMap<Device, (ByteArray) -> Unit>()
    }

    private val deviceContactListenerMap by lazy {
        HashMap<Device, (Int) -> Unit>()
    }

    fun initAllAffectiveService() {
        listOf(Device.Red, Device.Blue).forEach {
            if (AffectiveManage.hasConnectAffectiveService(it)) {
                if (!AffectiveManage.hasStartAffectiveService(it)) {
                    startAffectiveService(it)
                }
            } else {
                AffectiveManage.connectAffectiveServiceConnection(it, object :
                    IConnectionServiceListener {
                    override fun connectionError(error: Error?) {
                        //定时重连
                    }

                    override fun connectionSuccess(sessionId: String?) {
                        startAffectiveService(it)
                    }
                })
            }

        }
    }

    private fun startAffectiveService(device: Device) {
        AffectiveManage.startAffectiveService(device,
            object : IStartAffectiveServiceLister {
                override fun startAffectionFail(error: Error?) {
                }

                override fun startBioFail(error: Error?) {
                }

                override fun startFail(error: Error?) {
                }

                override fun startSuccess() {
                    AffectiveManage.subscribeData(device, listener = {
                        if (device == Device.Red) {
                            viewModelScope.launch {
                                _redAttention.emit(
                                    it?.realtimeAttentionData?.attention?.toInt() ?: 0
                                )
                            }
                        }

                        if (device == Device.Blue) {
                            viewModelScope.launch {
                                _blueAttention.emit(
                                    it?.realtimeAttentionData?.attention?.toInt() ?: 0
                                )
                            }
                        }
                    })

                }
            })
    }


    /**
     * 开始竞速
     * */
    fun startCompetition() {
        if (currentStatus == RacingStatus.PRE_COMPETITION) {
            currentStatus = RacingStatus.COMPETITIONING
            listOf(Device.Blue, Device.Red).forEach {
                BleManage.startBrainCollection(it)
            }
            //开始到计时
            //定义定时器，每0.6s发一次数据
        }

    }

    /**
     * 结束竞速
     * */
    fun finishCompetition() {
        if (currentStatus == RacingStatus.COMPETITIONING) {
            currentStatus = RacingStatus.PRE_COMPETITION
            //停止定时器
            //关闭倒计时
            //求平均值

            listOf(Device.Blue, Device.Red).forEach {
                BleManage.stopBrainCollection(it)
            }
        }

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

    fun blueIsConnected(): Boolean = BleManage.deviceIsConnect(Device.Blue)
    fun redIsConnected(): Boolean = BleManage.deviceIsConnect(Device.Red)
    fun trackIsConnected(): Boolean = BleManage.deviceIsConnect(Device.Track)

    fun blueIsWear(): Boolean = false
    fun redIsWear(): Boolean = false

    fun listenerAllDeviceDisconnect() {
        listOf(Device.Red, Device.Track, Device.Blue).forEach {
            BleManage.addDisconnectListener(it, getDisconnectListener(it))
        }
    }

    private fun getDisconnectListener(device: Device): (String) -> Unit {
        var listener = deviceDisconnectListenerMap[device]
        if (listener == null) {
            listener = {
                viewModelScope.launch {
                    _update.emit(Unit)
                }

            }
            deviceDisconnectListenerMap[device] = listener
        }
        return listener
    }

    private fun getRawDataListener(device: Device): (ByteArray) -> Unit {
        var listener = deviceRawDataListenerMap[device]
        if (listener == null) {
            listener = {
                AffectiveManage.appendData(device, it)
            }
            deviceRawDataListenerMap[device] = listener
        }
        return listener
    }

    private fun getContactListener(device: Device): (Int) -> Unit {
        var listener = deviceContactListenerMap[device]
        if (listener == null) {
            listener = {

            }
            deviceContactListenerMap[device] = listener
        }
        return listener
    }

    fun removeAllDeviceDisconnectListener() {
        listOf(Device.Red, Device.Track, Device.Blue).forEach {
            BleManage.removeDisconnectListener(it, getDisconnectListener(it))
        }
    }

    fun listenerHeadbandRawData() {
        listOf(Device.Red, Device.Blue).forEach {
            BleManage.addDeviceRawDataListener(it, getRawDataListener(it))
        }
    }

    fun removeHeadbandRawDataListener() {
        listOf(Device.Red, Device.Blue).forEach {
            BleManage.addDeviceRawDataListener(it, getRawDataListener(it))
        }
    }

    fun listenerHeadbandContactStatus() {
        listOf(Device.Red, Device.Blue).forEach {
            BleManage.addContactListener(it, getContactListener(it))
        }
    }

    fun removeHeadbandContactStatus() {
        listOf(Device.Red, Device.Blue).forEach {
            BleManage.removeContactListener(it, getContactListener(it))
        }
    }
}