package cn.entertech.racing.ble

import cn.entertech.ble.BaseBleConnectManager
import cn.entertech.ble.ConnectionBleStrategy
import cn.entertech.ble.multiple.MultipleBiomoduleBleManager
import cn.entertech.racing.RacingApplication

object BleManage {

    private val blueBleConnectManager by lazy {
        MultipleBiomoduleBleManager(RacingApplication.getInstance())
    }

    private val redBleConnectManager by lazy {
        MultipleBiomoduleBleManager(RacingApplication.getInstance())
    }

    


    private val trackBleConnectManager by lazy {
        MultipleBiomoduleBleManager(RacingApplication.getInstance())
    }


    fun blueIsConnect() = blueBleConnectManager.isConnected()

    fun redIsConnect() = redBleConnectManager.isConnected()

    fun trackIsConnect() = trackBleConnectManager.isConnected()


    fun findBlueHeadband() {
        blueBleConnectManager.findConnectedDevice()
    }

    fun findRedHeadband() {
        redBleConnectManager.findConnectedDevice()
    }

    fun findTrack() {
        trackBleConnectManager.findConnectedDevice()
    }


    fun connectBleDevice(
        bleConnectManager: BaseBleConnectManager,
        successConnect: ((String) -> Unit)?,
        failure: ((String) -> Unit)?
    ) {
        bleConnectManager.connectDevice(
            successConnect,
            failure,
            ConnectionBleStrategy.SCAN_AND_CONNECT_HIGH_SIGNAL
        )
    }

    fun connectBlueDevice(
        successConnect: ((String) -> Unit)?,
        failure: ((String) -> Unit)?
    ) {
        connectBleDevice(blueBleConnectManager, successConnect, failure)
    }

    fun connectRedDevice(
        successConnect: ((String) -> Unit)?,
        failure: ((String) -> Unit)?
    ) {
        connectBleDevice(redBleConnectManager, successConnect, failure)
    }

    fun connectTrackDevice(
        successConnect: ((String) -> Unit)?,
        failure: ((String) -> Unit)?
    ) {
        connectBleDevice(trackBleConnectManager, successConnect, failure)
    }


}