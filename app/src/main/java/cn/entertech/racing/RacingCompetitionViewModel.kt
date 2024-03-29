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
import cn.entertech.racing.setting.item.SetItemCelebrateThreshold
import cn.entertech.racing.setting.item.SetItemCelebrateTime
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

        /**
         * 发送注意力数据间隔时间 ms
         * */
        private const val SEND_ATTENTION_INTERVAL = 1200L
    }

    private var number = 0

    /**
     * 分数
     * */
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
    var competitionProgress = 0
        private set

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

    private val _blueCelebrate = MutableSharedFlow<Unit>()
    val blueCelebrate = _blueCelebrate.asSharedFlow()

    private val _redCelebrate = MutableSharedFlow<Unit>()
    val redCelebrate = _redCelebrate.asSharedFlow()

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

    private val _blueIsWear = MutableStateFlow(false)
    val blueIsWear = _blueIsWear.asStateFlow()


    private val _redIsWear = MutableStateFlow(false)
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

    private val deviceContractRecordMap by lazy {
        HashMap<String, Int>()
    }

    private var blueLastCelebrateAttentionTime = 0L
    private var redLastCelebrateAttentionTime = 0L

    private val redAffectiveDataListener: (RealtimeAffectiveData?) -> Unit by lazy {
        {
            viewModelScope.launch(Dispatchers.Main) {
                var redData = it?.realtimeAttentionData?.attention?.toInt() ?: 0
                EntertechRacingLog.d(TAG, "red Data: $redData")
                if (!_redIsWear.value) {
                    redData = 0
                }
                checkCelebrate(Device.Red, redData)
                redScoreList.add(redData)
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
                var blueData = it?.realtimeAttentionData?.attention?.toInt() ?: 0
                EntertechRacingLog.d(TAG, "blue Data: $blueData _blueIsWear ${_blueIsWear.value}")

                if (!_blueIsWear.value) {
                    blueData = 0
                }
                checkCelebrate(Device.Red, blueData)
                blueScoreList.add(blueData)
                blueArrayData.addLast(blueData)
                _blueAttention.emit(
                    blueData
                )
            }
        }
    }


    private suspend fun checkCelebrate(device: Device, attentionData: Int) {
        if (device == Device.Red) {
            if (attentionData >= SetItemCelebrateThreshold.getValue()) {
                if (redLastCelebrateAttentionTime == 0L) {
                    redLastCelebrateAttentionTime = System.currentTimeMillis()
                } else {
                    if (System.currentTimeMillis() - redLastCelebrateAttentionTime > SetItemCelebrateTime.getValue() * 1000) {
                        redLastCelebrateAttentionTime = 0
                        _redCelebrate.emit(Unit)
                    }
                }
            } else {
                redLastCelebrateAttentionTime = 0
            }
        }

        if (device == Device.Blue) {
            if (attentionData >= SetItemCelebrateThreshold.getValue()) {
                if (blueLastCelebrateAttentionTime == 0L) {
                    blueLastCelebrateAttentionTime = System.currentTimeMillis()
                } else {
                    if (System.currentTimeMillis() - blueLastCelebrateAttentionTime > SetItemCelebrateTime.getValue() * 1000) {
                        blueLastCelebrateAttentionTime = 0
                        _blueCelebrate.emit(Unit)
                    }
                }
            } else {
                blueLastCelebrateAttentionTime = 0
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
//            ergodicStartBrainCollection(getHeadbandDevice(), 0)
            //开始到计时
            // 设置定时器，参数依次为总时间（毫秒）、间隔时间（毫秒）
            val totalTime = SettingTimeEachRound.getValue().toLong() * 1000
            competitionCountDownTimer =
                object : CountDownTimer(SettingTimeEachRound.getValue().toLong() * 1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        viewModelScope.launch {
                            competitionProgress =
                                ((millisUntilFinished * 1.0 / totalTime) * 100).toInt()
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
                            0
                        }
                        sb.append(byteArray[0]).append(",")
                        byteArray[1] = TrackRedThreshold.getValue().toByte()
                        byteArray[2] = if (blueArrayData.isNotEmpty()) {
                            blueArrayData.removeFirst().toByte()
                        } else {
                            0
                        }
                        byteArray[3] = TrackBlueThreshold.getValue().toByte()
                        sb.append(byteArray[1]).append(",")
                        sb.append(byteArray[2]).append(",")
                        sb.append(byteArray[3])
                        EntertechRacingLog.d(TAG, "send data: $sb")
                        BleManage.sendData(Device.Track, byteArray)
                    }
                }
            }
            task?.apply {
                sendDataTime?.schedule(this, 0, SEND_ATTENTION_INTERVAL);
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
            competitionProgress = 0
            viewModelScope.launch {
                _showLoading.emit(true)
                _blueAttention.emit(0)
                _redAttention.emit(0)
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
//            ergodicStopBrainCollection(list, 0)
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

    fun gotoSettlement(context: Context) {
        val intent = Intent(
            context,
            SettlementActivity::class.java
        )
        val newBlueList = blueScoreList.filter { it != 0 }
        val newRedList = redScoreList.filter { it != 0 }
        intent.putExtra(
            BUNDLE_KEY_BLUE_SCORE, if (newBlueList.isEmpty()) {
                0
            } else {
                newBlueList.average().toInt()
            }
        )
        intent.putExtra(
            BUNDLE_KEY_RED_SCORE, if (newRedList.isEmpty()) {
                0
            } else {
                newRedList.average().toInt()
            }
        )
        context.startActivity(
            intent
        )
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
    fun trackIsConnected(): Boolean = BleManage.deviceIsConnect(Device.Track)


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
                viewModelScope.launch {
                    val canAppend = withContext(Dispatchers.Main) {
                        AffectiveManage.hasConnectAffectiveService(device)
                                && AffectiveManage.hasStartAffectiveService(device)
                    }
                    val isWear = if (device == Device.Blue) {
                        _blueIsWear.value
                    } else if (device == Device.Red) {
                        _redIsWear.value
                    } else {
                        false
                    }

                    if (canAppend && isWear) {
                        launch(Dispatchers.IO) {
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
                viewModelScope.launch(Dispatchers.Main) {
                    if (it == 0) {
                        //佩戴好了
                        var record =
                            deviceContractRecordMap[device.name]
                        if (record == null) {
                            record = 1
                        } else {
                            record++
                        }
                        if (record == 5) {
                            record = 0
                            if (device == Device.Blue) {
                                _blueIsWear.emit(true)
                            }
                            if (device == Device.Red) {
                                _redIsWear.emit((true))
                            }
                        }
                        deviceContractRecordMap[device.name] = record
                    } else {
                        deviceContractRecordMap[device.name] = 0
                        if (device == Device.Blue) {
                            _blueIsWear.emit(false)
                        }

                        if (device == Device.Red) {
                            _redIsWear.emit((false))
                        }
                    }
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