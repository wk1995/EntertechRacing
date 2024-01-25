package cn.entertech.racing

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.entertech.affective.sdk.api.IConnectionServiceListener
import cn.entertech.affective.sdk.api.IFinishAffectiveServiceListener
import cn.entertech.affective.sdk.api.IStartAffectiveServiceLister
import cn.entertech.affective.sdk.bean.Error
import cn.entertech.affective.sdk.bean.RealtimeAffectiveData
import cn.entertech.racing.affective.AffectiveManage
import cn.entertech.racing.ble.BleManage
import cn.entertech.racing.connect.ConnectedActivity
import cn.entertech.racing.device.Device
import cn.entertech.racing.device.DeviceType
import cn.entertech.racing.headband.BlueHeadBandFactory
import cn.entertech.racing.headband.RedHeadBandFactory
import cn.entertech.racing.log.EntertechRacingLog
import cn.entertech.racing.setting.SettingActivity
import cn.entertech.racing.setting.SettingType
import cn.entertech.racing.setting.item.SettingTimeEachRound
import cn.entertech.racing.setting.item.TrackBlueThreshold
import cn.entertech.racing.setting.item.TrackRedThreshold
import cn.entertech.racing.track.SetItemTrackFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Timer
import java.util.TimerTask


class RacingCompetitionViewModel : ViewModel() {

    companion object {
        const val BUNDLE_KEY_DEVICE_TYPE = "deviceType"
        private const val TAG = "RacingCompetitionViewModel"
    }

    /**
     * 比赛倒计时
     * */
    private var competitionCountDownTimer: CountDownTimer? = null

    private var sendDataTime: Timer? = null

    private val blueArrayData: ArrayDeque<Int> = ArrayDeque()
    private val redArrayData: ArrayDeque<Int> = ArrayDeque()

    /**
     * 更新UI
     * */
    private val _update = MutableSharedFlow<Unit>()
    val updateUi = _update.asSharedFlow()

    /**
     * 蓝头环 实时 注意力
     * */
    private val _blueAttention = MutableStateFlow(0)
    val blueAttention = _blueAttention.asStateFlow()

    /**
     * 红头环 实时 注意力
     * */
    private val _redAttention = MutableStateFlow(0)
    val redAttention = _redAttention.asStateFlow()

    /**
     * 比赛剩余时间
     * */
    private val _remainingTime = MutableSharedFlow<String>()
    val remainingTime = _remainingTime.asSharedFlow()

    /**
     * 当前比赛状态
     * */
    private val _racingStatus = MutableStateFlow(RacingStatus.PRE_COMPETITION)
    val racingStatus = _racingStatus.asStateFlow()

    private val sdf by lazy {
        SimpleDateFormat("mm:ss")
    }

    private val deviceDisconnectListenerMap by lazy {
        HashMap<Device, (String) -> Unit>()
    }


    private val deviceRawDataListenerMap by lazy {
        HashMap<Device, (ByteArray) -> Unit>()
    }

    private val deviceContactListenerMap by lazy {
        HashMap<Device, (Int) -> Unit>()
    }

    private val redAffectiveDataListener: (RealtimeAffectiveData?) -> Unit by lazy {
        {
            viewModelScope.launch(Dispatchers.Main) {
                val redData = it?.realtimeAttentionData?.attention?.toInt() ?: 0
                redArrayData.addLast(redData)
                _redAttention.emit(
                    redData
                )
            }
        }
    }


    private val blueAffectiveDataListener: (RealtimeAffectiveData?) -> Unit by lazy {
        {
            viewModelScope.launch(Dispatchers.Main) {
                val blueData = it?.realtimeAttentionData?.attention?.toInt() ?: 0
                blueArrayData.addLast(blueData)
                _blueAttention.emit(
                    blueData
                )
            }
        }
    }


    fun resetCompetition() {
        viewModelScope.launch {
            _racingStatus.emit(RacingStatus.PRE_COMPETITION)
        }
    }

    private fun initAllAffectiveService() {
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
                    AffectiveManage.subscribeData(
                        device, listener =
                        if (device == Device.Red) {
                            redAffectiveDataListener
                        } else if (device == Device.Blue) {
                            blueAffectiveDataListener
                        } else {
                            null
                        }
                    )
                }
            })
    }


    /**
     * 开始竞速
     * */
    fun startCompetition() {
        if (_racingStatus.value == RacingStatus.PRE_COMPETITION) {
            viewModelScope.launch {
                _racingStatus.emit(RacingStatus.COMPETITIONING)
            }
            initAllAffectiveService()
            listOf(Device.Blue, Device.Red).forEach {
                BleManage.startBrainCollection(it)
            }
            //开始到计时
            // 设置定时器，参数依次为总时间（毫秒）、间隔时间（毫秒）
            competitionCountDownTimer =
                object : CountDownTimer(SettingTimeEachRound.getValue().toLong(), 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        EntertechRacingLog.d(TAG, "$millisUntilFinished: ${Thread.currentThread()}")
                        viewModelScope.launch {
                            _remainingTime.emit(remainTimeToString(millisUntilFinished))
                        }
                    }

                    override fun onFinish() {
                        EntertechRacingLog.d(TAG, "onFinish: ${Thread.currentThread()}")
                        // 定时器完成时调用
                        finishCompetition(true)
                    }
                }
            competitionCountDownTimer?.start()
            //定义定时器，每1s发一次数据
            sendDataTime = Timer()
            val task: TimerTask = object : TimerTask() {
                override fun run() {
                    // 在主线程中更新 UI
                    viewModelScope.launch(Dispatchers.Main) {
                        val byteArray = ByteArray(4)
                        byteArray[0] = if (redArrayData.isNotEmpty()) {
                            redArrayData.removeFirst().toByte()
                        } else {
                            TrackRedThreshold.getValue().toByte()
                        }
                        byteArray[1] = Integer.valueOf(50).toByte()
                        byteArray[2] = if (blueArrayData.isNotEmpty()) {
                            blueArrayData.removeFirst().toByte()
                        } else {
                            TrackBlueThreshold.getValue().toByte()
                        }
                        byteArray[3] = Integer.valueOf(50).toByte()
                        BleManage.sendData(Device.Track, byteArray)
                    }
                }
            }
            sendDataTime?.schedule(task, 0, 1000);
        }

    }

    fun remainTimeToString(remainTime: Long): String {
        return sdf.format(Date(remainTime))
    }


    /**
     * 结束竞速
     * @param isAuto 是否是自动退出 true 自动退出
     * */
    fun finishCompetition(isAuto: Boolean) {
        if (_racingStatus.value == RacingStatus.COMPETITIONING) {
            //停止定时器
            sendDataTime?.cancel()
            sendDataTime?.purge();
            sendDataTime = null
            //关闭倒计时
            competitionCountDownTimer?.cancel()
            competitionCountDownTimer = null
            listOf(Device.Blue, Device.Red).forEach {
                BleManage.stopBrainCollection(it)
                AffectiveManage.unSubscribeData(
                    it, listener = if (it == Device.Red) {
                        redAffectiveDataListener
                    } else if (it == Device.Blue) {
                        blueAffectiveDataListener
                    } else {
                        null
                    }
                )
                AffectiveManage.finishAffectiveService(it,
                    object : IFinishAffectiveServiceListener {
                        override fun finishAffectiveFail(error: Error?) {

                        }

                        override fun finishBioFail(error: Error?) {

                        }

                        override fun finishError(error: Error?) {
                            AffectiveManage.closeAffectiveServiceConnection(it)
                        }

                        override fun finishSuccess() {
                            AffectiveManage.closeAffectiveServiceConnection(it)
                        }
                    })
            }
            if (isAuto) {
                //求平均值
                viewModelScope.launch {
                    _racingStatus.emit(RacingStatus.COMPETITION_END)
                }
            } else {
                viewModelScope.launch {
                    _racingStatus.emit(RacingStatus.PRE_COMPETITION)
                }
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
    private fun hasHeadbandMac(): Boolean =
        BlueHeadBandFactory.getValue().isNotEmpty() || RedHeadBandFactory.getValue().isNotEmpty()

    private fun hasTrackMac(): Boolean = SetItemTrackFactory.getValue().isNotEmpty()

    fun blueIsConnected(): Boolean = BleManage.deviceIsConnect(Device.Blue)
    fun redIsConnected(): Boolean = BleManage.deviceIsConnect(Device.Red)
    fun trackIsConnected(): Boolean = true

    fun blueIsWear(): Boolean = true
    fun redIsWear(): Boolean = true

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