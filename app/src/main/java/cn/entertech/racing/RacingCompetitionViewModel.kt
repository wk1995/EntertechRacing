package cn.entertech.racing

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.Looper
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
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Timer
import java.util.TimerTask


class RacingCompetitionViewModel : ViewModel() {

    companion object {
        const val BUNDLE_KEY_DEVICE_TYPE = "deviceType"
        private const val TAG = "RacingCompetitionViewModel"
        const val BUNDLE_KEY_BLUE_SCORE = "bundle_key_blue_score"
        const val BUNDLE_KEY_RED_SCORE = "bundle_key_red_score"
    }

    private var number = 0

    private var blueScoreList = ArrayList<Int>()
    private var redScoreList = ArrayList<Int>()

    /**
     * 比赛倒计时
     * */
    private var competitionCountDownTimer: CountDownTimer? = null

    private var sendDataTime: Timer? = null
    private var task: TimerTask? = null

    private val blueArrayData: ArrayDeque<Int> = ArrayDeque()
    private val redArrayData: ArrayDeque<Int> = ArrayDeque()

    private val deviceAffectServiceSet by lazy {
        HashSet<Device>()
    }

    private val _showLoading = MutableStateFlow(false)
    val showLoading = _showLoading.asStateFlow()

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

    private val _blueIsWear = MutableStateFlow(true)
    val blueIsWear = _blueIsWear.asStateFlow()


    private val _redIsWear = MutableStateFlow(true)
    val redIsWear = _redIsWear.asStateFlow()


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
                EntertechRacingLog.d(TAG, "red Data: $redData")
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
                EntertechRacingLog.d(TAG, "blue Data: $blueData")
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
        ergodicStartAffectiveService(getHeadbandDevice(), 0)
    }

    private fun ergodicStartAffectiveService(list: List<Device>, index: Int) {
        if (index >= list.size) {
            return
        }
        val it = list[index]
        if (AffectiveManage.hasConnectAffectiveService(it)) {
            EntertechRacingLog.d(TAG, "$it hasConnectAffectiveService")
            if (!AffectiveManage.hasStartAffectiveService(it)) {
                startAffectiveService(it) {
                    ergodicStartAffectiveService(list, index + 1)
                }
            } else {
                EntertechRacingLog.d(TAG, "$it hasStartAffectiveService")
                AffectiveManage.subscribeData(
                    it, listener =
                    if (it == Device.Red) {
                        redAffectiveDataListener
                    } else if (it == Device.Blue) {
                        blueAffectiveDataListener
                    } else {
                        null
                    }
                )
                ergodicStartAffectiveService(list, index + 1)
            }
        } else {
            AffectiveManage.connectAffectiveServiceConnection(it, object :
                IConnectionServiceListener {
                override fun connectionError(error: Error?) {
                    //定时重连
                    EntertechRacingLog.e(
                        TAG,
                        "$it connectAffectiveServiceConnection error $error"
                    )
                }

                override fun connectionSuccess(sessionId: String?) {
                    EntertechRacingLog.i(
                        TAG,
                        "$it connectAffectiveServiceConnection  $sessionId"
                    )
                    viewModelScope.launch(Dispatchers.Main) {
                        deviceAffectServiceSet.add(it)
                    }
                    startAffectiveService(it) {
                        ergodicStartAffectiveService(list, index + 1)
                    }
                }
            })
        }

    }


    private fun startAffectiveService(device: Device, next: () -> Unit) {
        AffectiveManage.startAffectiveService(device,
            object : IStartAffectiveServiceLister {
                override fun startAffectionFail(error: Error?) {
                    EntertechRacingLog.e(
                        TAG,
                        "$device startAffectionFail error $error"
                    )
                }

                override fun startBioFail(error: Error?) {
                    EntertechRacingLog.e(
                        TAG,
                        "$device startBioFail error $error"
                    )
                }

                override fun startFail(error: Error?) {
                    EntertechRacingLog.e(
                        TAG,
                        "$device startFail error $error"
                    )
                    next()
                }

                override fun startSuccess() {
                    EntertechRacingLog.i(TAG, "$device startSuccess ")
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
                    next()
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
            blueScoreList.clear()
            redScoreList.clear()
            initAllAffectiveService()
            ergodicStartBrainCollection(getHeadbandDevice(), 0)
            //开始到计时
            // 设置定时器，参数依次为总时间（毫秒）、间隔时间（毫秒）
            competitionCountDownTimer =
                object : CountDownTimer(SettingTimeEachRound.getValue().toLong() * 1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
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
            sendDataTime = Timer("sendAttentionData" + number)
            number++
            task = object : TimerTask() {
                override fun run() {
                    // 在主线程中更新 UI
                    viewModelScope.launch(Dispatchers.Main) {
                        val sb = StringBuilder()
                        val byteArray = ByteArray(4)
                        byteArray[0] = if (redArrayData.isNotEmpty()) {
                            redArrayData.removeFirst().toByte()
                        } else {
                            TrackRedThreshold.getValue().toByte()
                        }
                        sb.append(byteArray[0]).append(",")
                        byteArray[1] = Integer.valueOf(50).toByte()
                        byteArray[2] = if (blueArrayData.isNotEmpty()) {
                            blueArrayData.removeFirst().toByte()
                        } else {
                            TrackBlueThreshold.getValue().toByte()
                        }
                        byteArray[3] = Integer.valueOf(50).toByte()
                        sb.append(byteArray[1]).append(",")
                        sb.append(byteArray[2]).append(",")
                        sb.append(byteArray[3])
                        EntertechRacingLog.d(TAG, "send data: $sb")
                        BleManage.sendData(Device.Track, byteArray)
                    }
                }
            }
            task?.apply {
                sendDataTime?.schedule(this, 0, 1000);
            }

        }

    }

    fun remainTimeToString(remainTime: Long): String {
        return sdf.format(Date(remainTime))
    }


    private fun getHeadbandDevice() = listOf(Device.Blue, Device.Red)

    /**
     * 结束竞速
     * 停止蓝牙脑波数据采集
     * 停止本地算法 脑波数据订阅
     * 释放本地算法sdk
     * 关闭本地算法连接
     * @param isAuto 是否是自动退出 true 自动退出
     * */
    fun finishCompetition(isAuto: Boolean) {
        EntertechRacingLog.d(TAG, "finishCompetition")
        if (_racingStatus.value == RacingStatus.COMPETITIONING) {
            viewModelScope.launch {
                _showLoading.emit(true)
            }
            task?.cancel()
            task = null
            //停止定时器
            sendDataTime?.cancel()
            sendDataTime?.purge()
            sendDataTime = null
            //关闭倒计时
            competitionCountDownTimer?.cancel()
            competitionCountDownTimer = null
            val list = getHeadbandDevice()
            ergodicStopBrainCollection(list, 0)
            list.forEach {
                EntertechRacingLog.d(TAG, "$it unSubscribeData")
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
                            EntertechRacingLog.e(TAG, "finishAffectiveFail $error")
                        }

                        override fun finishBioFail(error: Error?) {
                            EntertechRacingLog.e(TAG, "finishBioFail $error")
                        }

                        override fun finishError(error: Error?) {
                            EntertechRacingLog.e(TAG, "finishError $error")
                            closeAffectiveServiceConnection(it, isAuto)
                        }

                        override fun finishSuccess() {
                            EntertechRacingLog.i(TAG, "finishSuccess ")
                            closeAffectiveServiceConnection(it, isAuto)
                        }
                    })
            }
        }
    }

    private fun ergodicStartBrainCollection(list: List<Device>, index: Int) {
        if (index >= list.size) {
            return
        }
        val currentDevice = list[index]
        BleManage.startBrainCollection(currentDevice, success = { byteArray ->
            EntertechRacingLog.d(
                TAG,
                "stopBrainCollection success $currentDevice ${ThreadUtils.currentIsMain()}"
            )
            ergodicStartBrainCollection(list, index + 1)
        }, failure = {
            EntertechRacingLog.e(
                TAG,
                "stopBrainCollection failure $currentDevice ${ThreadUtils.currentIsMain()}"
            )
            ergodicStartBrainCollection(list, index + 1)
        })

    }

    private fun ergodicStopBrainCollection(list: List<Device>, index: Int) {
        if (index >= list.size) {
            return
        }
        val currentDevice = list[index]
        BleManage.stopBrainCollection(currentDevice, success = { byteArray ->
            EntertechRacingLog.d(
                TAG,
                "stopBrainCollection success $currentDevice ${ThreadUtils.currentIsMain()}"
            )
            ergodicStopBrainCollection(list, index + 1)
        }, failure = {
            EntertechRacingLog.e(
                TAG,
                "stopBrainCollection failure $currentDevice ${ThreadUtils.currentIsMain()}"
            )
            ergodicStopBrainCollection(list, index + 1)
        })

    }


    fun closeAffectiveServiceConnection(device: Device, isAuto: Boolean) {
        EntertechRacingLog.d(
            TAG,
            "closeAffectiveServiceConnection $device ${Thread.currentThread()}"
        )
        AffectiveManage.closeAffectiveServiceConnection(device)
        viewModelScope.launch(Dispatchers.Main) {
            EntertechRacingLog.d(
                TAG,
                "closeAffectiveServiceConnection ${deviceAffectServiceSet.size}"
            )
            deviceAffectServiceSet.remove(device)
            if (deviceAffectServiceSet.isEmpty()) {
                EntertechRacingLog.d(TAG, "emit $deviceAffectServiceSet")
                _showLoading.emit(false)
                if (isAuto) {
                    //求平均值
                    _racingStatus.emit(RacingStatus.COMPETITION_END)
                } else {
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
        intent.putExtra(BUNDLE_KEY_BLUE_SCORE, blueScoreList.average())
        intent.putExtra(BUNDLE_KEY_RED_SCORE, redScoreList.average())
        context.startActivity(intent)
    }

    /**
     * 有至少一个头环的mac地址
     * */
    private fun hasHeadbandMac(): Boolean =
        BlueHeadBandFactory.getValue().isNotEmpty() || RedHeadBandFactory.getValue().isNotEmpty()

    private fun hasTrackMac(): Boolean = SetItemTrackFactory.getValue().isNotEmpty()

    fun blueIsConnected(): Boolean = true
    fun redIsConnected(): Boolean = true
    fun trackIsConnected(): Boolean = true


    fun listenerAllDeviceDisconnect() {
        listOf(Device.Red, Device.Track, Device.Blue).forEach {
            BleManage.addDisconnectListener(it, getDisconnectListener(it))
        }
    }

    private fun getDisconnectListener(device: Device): (String) -> Unit {
        var listener = deviceDisconnectListenerMap[device]
        if (listener == null) {
            listener = {
                EntertechRacingLog.d(TAG, "$device Disconnect")
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
                EntertechRacingLog.d(
                    TAG,
                    "getRawData: $device isMainThread ${ThreadUtils.currentIsMain()}"
                )
                viewModelScope.launch {
                    val canAppend = withContext(Dispatchers.Main) {
                        EntertechRacingLog.d(
                            TAG,
                            "canAppend isMainThread ${ThreadUtils.currentIsMain()}"
                        )
                        AffectiveManage.hasConnectAffectiveService(device)
                                && AffectiveManage.hasStartAffectiveService(device)
                    }

                    if (canAppend) {
                        launch(Dispatchers.IO) {
                            EntertechRacingLog.d(
                                TAG,
                                "$device appendData isMainThread ${ThreadUtils.currentIsMain()}"
                            )
                            AffectiveManage.appendData(device, it)
                        }

                    } else {
                        /* EntertechRacingLog.e(
                        TAG,
                        "hasConnectAffectiveService ${AffectiveManage.hasConnectAffectiveService(device)}  hasStartAffectiveService ${
                            AffectiveManage.hasStartAffectiveService(
                                device
                            )
                        }"
                    )*/
                    }
                }
            }
            deviceRawDataListenerMap[device] = listener
        }
        return listener
    }

    private fun getContactListener(device: Device): (Int) -> Unit {
        var listener = deviceContactListenerMap[device]
        if (listener == null) {
            listener = {
                /* EntertechRacingLog.d(
                     TAG,
                     "$device ContactListener isMainThread ${Thread.currentThread() == Looper.getMainLooper().thread}"
                 )*/
                if (it == 0) {
                    //佩戴好了
                } else {

                }
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
        getHeadbandDevice().forEach {
            BleManage.addDeviceRawDataListener(it, getRawDataListener(it))
        }
    }

    fun removeHeadbandRawDataListener() {
        getHeadbandDevice().forEach {
            BleManage.addDeviceRawDataListener(it, getRawDataListener(it))
        }
    }

    fun listenerHeadbandContactStatus() {
        getHeadbandDevice().forEach {
            BleManage.addContactListener(it, getContactListener(it))
        }
    }

    fun removeHeadbandContactStatus() {
        getHeadbandDevice().forEach {
            BleManage.removeContactListener(it, getContactListener(it))
        }
    }
}