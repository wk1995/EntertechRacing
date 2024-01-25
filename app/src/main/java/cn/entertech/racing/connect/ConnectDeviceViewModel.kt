package cn.entertech.racing.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.entertech.ble.ConnectionBleStrategy
import cn.entertech.ble.multiple.MultipleBiomoduleBleManager
import cn.entertech.racing.RacingApplication
import cn.entertech.racing.ble.BleManage
import cn.entertech.racing.device.Device
import cn.entertech.racing.log.EntertechRacingLog
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ConnectDeviceViewModel : ViewModel() {

    companion object {
        private const val TAG = "ConnectDeviceViewModel"
    }

    private val _connectBlueResult = MutableSharedFlow<Unit>()
    val connectBlueResult = _connectBlueResult.asSharedFlow()

    private val _connectRedResult = MutableSharedFlow<Unit>()
    val connectRedResult = _connectRedResult.asSharedFlow()

    private val _connectTrackResult = MutableSharedFlow<Unit>()
    val connectTrackResult = _connectTrackResult.asSharedFlow()


    fun blueIsConnect() = BleManage.deviceIsConnect(Device.Blue)

    fun redIsConnect() = BleManage.deviceIsConnect(Device.Red)

    fun trackIsConnect() = BleManage.deviceIsConnect(Device.Track)

    fun connectBlueDevice() {
        EntertechRacingLog.d(TAG, "connectBlueDevice")
        BleManage.connectDevice(Device.Blue, {
            EntertechRacingLog.d(TAG, "connect Blue success $it")
            viewModelScope.launch {
                _connectBlueResult.emit(Unit)
            }
        }, {
            EntertechRacingLog.e(TAG, "connect Blue fail $it")
            viewModelScope.launch {
                _connectBlueResult.emit(Unit)
            }
        })
    }

    fun connectRedDevice() {
        EntertechRacingLog.d(TAG, "connectRedDevice")
        BleManage.connectDevice(Device.Red, {
            EntertechRacingLog.d(TAG, "connect Red success $it")
            viewModelScope.launch {
                _connectRedResult.emit(Unit)
            }
        }, {
            EntertechRacingLog.e(TAG, "connect Red fail $it")
            viewModelScope.launch {
                _connectRedResult.emit(Unit)
            }
        })
    }

    fun connectTrackDevice() {
        EntertechRacingLog.d(TAG, "connectTrackDevice")
        BleManage.connectDevice(Device.Track, {
            EntertechRacingLog.i(TAG, "connect Track success $it")
            viewModelScope.launch {
                _connectTrackResult.emit(Unit)
            }
        }, {
            EntertechRacingLog.e(TAG, "connect Track fail $it")
            viewModelScope.launch {
                _connectTrackResult.emit(Unit)
            }
        })
    }

    fun findBlueHeadband() {
        BleManage.findDevice(Device.Blue)
    }

    fun findRedHeadband() {
        BleManage.findDevice(Device.Red)
    }

    fun findTrack() {
        BleManage.findDevice(Device.Track)
    }

}