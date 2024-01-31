package cn.entertech.racing.ble

import cn.entertech.ble.BaseBleConnectManager
import cn.entertech.ble.ConnectionBleStrategy
import cn.entertech.ble.multiple.MultipleBiomoduleBleManager
import cn.entertech.racing.RacingApplication
import cn.entertech.racing.device.Device
import cn.entertech.racing.headband.BlueHeadBandFactory
import cn.entertech.racing.headband.RedHeadBandFactory
import cn.entertech.racing.log.EntertechRacingLog
import cn.entertech.racing.track.SetItemTrackFactory

object BleManage {

    private val bleConnectManagerMap by lazy {
        HashMap<String, BaseBleConnectManager>()
    }

    fun deviceIsConnect(device: Device) = getBleConnectManager(device).isConnected()


    fun findDevice(device: Device) {
        getBleConnectManager(device).findConnectedDevice()
    }

    var connectDeviceCallback: ((Device) -> Unit)? = null


    @Synchronized
    fun connectDevice(
        device: Device, callback: ((Device) -> Unit)? = null
    ) {
        val bleConnectManager = getBleConnectManager(device)
        /*bleConnectManager.connectDevice(
            successConnect,
            failure,
            ConnectionBleStrategy.SCAN_AND_CONNECT_HIGH_SIGNAL
        )*/
        connectDeviceCallback = callback
        val mac = when (device) {
            Device.Track -> {
                SetItemTrackFactory.getValue()
            }

            Device.Red -> {
                RedHeadBandFactory.getValue()
            }

            Device.Blue -> {
                BlueHeadBandFactory.getValue()
            }
        }
        bleConnectManager.scanMacAndConnect(
            mac = mac,
            successConnect = { connectDeviceCallback?.invoke(device) },
            failure = { connectDeviceCallback?.invoke(device) }
        )
    }

    fun disconnectDevice(
        device: Device,
        success: () -> Unit = {},
        failure: ((String) -> Unit)? = null
    ) {
        getBleConnectManager(device).disConnect(success, failure)
    }


    fun removeDisconnectListener(device: Device, listener: (String) -> Unit) {
        val bleConnectManager = getBleConnectManager(device)
        bleConnectManager.removeConnectListener(listener)
    }

    fun addDisconnectListener(device: Device, listener: (String) -> Unit) {
        val bleConnectManager = getBleConnectManager(device)
        bleConnectManager.addDisConnectListener(listener)
    }

    private fun getBleConnectManager(device: Device): BaseBleConnectManager {
        var bleConnectManager = bleConnectManagerMap[device.name]
        if (bleConnectManager == null) {
            bleConnectManager = MultipleBiomoduleBleManager(RacingApplication.getInstance())
            bleConnectManagerMap[device.name] = bleConnectManager
        }
        return bleConnectManager
    }

    fun addBlueDisconnectListener(listener: (String) -> Unit) {
        addDisconnectListener(Device.Blue, listener)
    }

    fun addRedDisconnectListener(listener: (String) -> Unit) {
        addDisconnectListener(Device.Red, listener)
    }

    fun addTrackDisconnectListener(listener: (String) -> Unit) {
        addDisconnectListener(Device.Track, listener)
    }


    fun addDeviceRawDataListener(device: Device, listener: (ByteArray) -> Unit) {
        getBleConnectManager(device).addRawDataListener(listener)
    }

    fun removeDeviceRawDataListener(device: Device, listener: (ByteArray) -> Unit) {
        getBleConnectManager(device).removeRawDataListener(listener)
    }

    fun addContactListener(device: Device, listener: (Int) -> Unit) {
        getBleConnectManager(device).addContactListener(listener)
    }

    fun removeContactListener(device: Device, listener: (Int) -> Unit) {
        getBleConnectManager(device).removeContactListener(listener)
    }

    fun startBrainCollection(
        device: Device, success: ((ByteArray) -> Unit)? = null,
        failure: ((String) -> Unit)? = null
    ) {
        getBleConnectManager(device).startHeartAndBrainCollection(success, failure)
    }

    fun stopBrainCollection(
        device: Device, success: ((ByteArray) -> Unit)? = null,
        failure: ((String) -> Unit)? = null
    ) {
        getBleConnectManager(device).stopHeartAndBrainCollection(success, failure)
    }

    fun sendData(device: Device, byteArray: ByteArray) {
        getBleConnectManager(device).command(byteArray)
        EntertechRacingLog.d("sendData", "byteArray")
    }

}