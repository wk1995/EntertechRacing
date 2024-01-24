package cn.entertech.racing.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.entertech.ble.ConnectionBleStrategy
import cn.entertech.ble.multiple.MultipleBiomoduleBleManager
import cn.entertech.racing.RacingApplication
import cn.entertech.racing.log.EntertechRacingLog
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ConnectDeviceViewModel : ViewModel() {

    companion object {
        private const val TAG = "ConnectDeviceViewModel"
    }

    private val blueBleConnectManager by lazy {
        MultipleBiomoduleBleManager(RacingApplication.getInstance())
    }

    private val redBleConnectManager by lazy {
        MultipleBiomoduleBleManager(RacingApplication.getInstance())
    }


    private val trackBleConnectManager by lazy {
        MultipleBiomoduleBleManager(RacingApplication.getInstance())
    }

    private val _connectBlueResult = MutableSharedFlow<Unit>()
    val connectBlueResult = _connectBlueResult.asSharedFlow()

    private val _connectRedResult = MutableSharedFlow<Unit>()
    val connectRedResult = _connectRedResult.asSharedFlow()

    private val _connectTrackResult = MutableSharedFlow<Unit>()
    val connectTrackResult = _connectTrackResult.asSharedFlow()


    fun blueIsConnect() = blueBleConnectManager.isConnected()

    fun redIsConnect() = redBleConnectManager.isConnected()

    fun trackIsConnect() = trackBleConnectManager.isConnected()

    fun connectBlueDevice() {
        EntertechRacingLog.d(TAG, "connectBlueDevice")
        blueBleConnectManager.connectDevice({
            EntertechRacingLog.d(TAG, "connect Blue success $it")
            viewModelScope.launch {
                _connectBlueResult.emit(Unit)
            }
        }, {
            EntertechRacingLog.e(TAG, "connect Blue fail $it")
            viewModelScope.launch {
                _connectBlueResult.emit(Unit)
            }
        }, ConnectionBleStrategy.SCAN_AND_CONNECT_HIGH_SIGNAL)
    }

    fun connectRedDevice() {
        EntertechRacingLog.d(TAG, "connectRedDevice")
        redBleConnectManager.connectDevice({
            EntertechRacingLog.d(TAG, "connect Red success $it")
            viewModelScope.launch {
                _connectRedResult.emit(Unit)
            }
        }, {
            EntertechRacingLog.e(TAG, "connect Red fail $it")
            viewModelScope.launch {
                _connectRedResult.emit(Unit)
            }
        }, ConnectionBleStrategy.SCAN_AND_CONNECT_HIGH_SIGNAL)
    }

    fun connectTrackDevice() {
        EntertechRacingLog.d(TAG, "connectTrackDevice")
        trackBleConnectManager.connectDevice({
            EntertechRacingLog.i(TAG, "connect Track success $it")
            viewModelScope.launch {
                _connectTrackResult.emit(Unit)
            }
        }, {
            EntertechRacingLog.e(TAG, "connect Track fail $it")
            viewModelScope.launch {
                _connectTrackResult.emit(Unit)
            }
        }, ConnectionBleStrategy.SCAN_AND_CONNECT_HIGH_SIGNAL)
    }

    fun findBlueHeadband(){
        blueBleConnectManager.findConnectedDevice()
    }

    fun findRedHeadband(){
        redBleConnectManager.findConnectedDevice()
    }

    fun findTrack(){
        trackBleConnectManager.findConnectedDevice()
    }

}