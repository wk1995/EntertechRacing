package cn.entertech.racing.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        connectDevice(Device.Blue)
    }

    fun connectRedDevice() {
        connectDevice(Device.Red)
    }

    private fun connectDevice(device: Device) {
        EntertechRacingLog.d(TAG, "connectDevice $device")
        device.apply {
            BleManage.connectDevice(
                this
            ) {
                viewModelScope.launch {
                    when (it) {
                        Device.Red -> {
                            BleManage.startBrainCollection(it)
                            _connectRedResult.emit(Unit)
                        }

                        Device.Track -> {
                            _connectTrackResult.emit(Unit)
                        }

                        Device.Blue -> {
                            BleManage.startBrainCollection(it)
                            _connectBlueResult.emit(Unit)
                        }
                    }
                }
            }
        }
    }

    fun connectTrackDevice() {
        connectDevice(Device.Track)
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


    override fun onCleared() {
        BleManage.connectDeviceCallback = null
        super.onCleared()
    }
}