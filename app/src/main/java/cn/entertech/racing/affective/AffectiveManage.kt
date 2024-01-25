package cn.entertech.racing.affective

import cn.entertech.affective.sdk.api.IAffectiveDataAnalysisService
import cn.entertech.affective.sdk.api.IConnectionServiceListener
import cn.entertech.affective.sdk.api.IFinishAffectiveServiceListener
import cn.entertech.affective.sdk.api.IGetReportListener
import cn.entertech.affective.sdk.api.IStartAffectiveServiceLister
import cn.entertech.affective.sdk.bean.AffectiveServiceWay
import cn.entertech.affective.sdk.bean.EnterAffectiveConfigProxy
import cn.entertech.affective.sdk.bean.RealtimeAffectiveData
import cn.entertech.affective.sdk.bean.RealtimeBioData
import cn.entertech.racing.device.Device

object AffectiveManage {

    private val analysisServiceMap by lazy {
        HashMap<Device, IAffectiveDataAnalysisService?>()
    }


    private fun getAnalysisService(device: Device): IAffectiveDataAnalysisService? {
        var service = analysisServiceMap[device]
        if (service == null) {
            service =
                IAffectiveDataAnalysisService.getService(AffectiveServiceWay.AffectiveLocalService)
            analysisServiceMap[device] = service
        }
        return service
    }

    fun connectAffectiveServiceConnection(device: Device, listener: IConnectionServiceListener) {
        getAnalysisService(device)?.connectAffectiveServiceConnection(
            listener,
            EnterAffectiveConfigProxy()
        )
    }

    fun addServiceConnectStatueListener(
        device: Device,
        connectionListener: () -> Unit,
        disconnectListener: (String) -> Unit
    ) {
        getAnalysisService(device)?.addServiceConnectStatueListener(
            connectionListener,
            disconnectListener
        )
    }

    fun removeServiceConnectStatueListener(
        device: Device,
        connectionListener: () -> Unit,
        disconnectListener: (String) -> Unit
    ) {
        getAnalysisService(device)?.removeServiceConnectStatueListener(
            connectionListener,
            disconnectListener
        )
    }


    fun startAffectiveService(device: Device, listener: IStartAffectiveServiceLister) {
        getAnalysisService(device)?.startAffectiveService(listener)
    }


    fun subscribeData(
        device: Device,
        bdListener: ((RealtimeBioData?) -> Unit)? = null,
        listener: ((RealtimeAffectiveData?) -> Unit)
    ) {
        getAnalysisService(device)?.subscribeData(bdListener = bdListener, listener)
    }

    fun appendData(device: Device, data: ByteArray) {
        getAnalysisService(device)?.appendEEGData(data)
    }

    fun restoreAffectiveService(device: Device, listener: IStartAffectiveServiceLister) {
        getAnalysisService(device)?.restoreAffectiveService(listener)
    }

    fun unSubscribeData(
        device: Device,
        bdListener: ((RealtimeBioData?) -> Unit)? = null,
        listener: ((RealtimeAffectiveData?) -> Unit)
    ) {
        getAnalysisService(device)?.unSubscribeData(bdListener = bdListener, listener)
    }


    fun closeAffectiveServiceConnection(device: Device) {
        getAnalysisService(device)?.closeAffectiveServiceConnection()
    }

    fun finishAffectiveService(device: Device, listener: IFinishAffectiveServiceListener) {
        getAnalysisService(device)?.finishAffectiveService(listener)
    }

    fun getReport(device: Device, listener: IGetReportListener, needReport: Boolean) {
        getAnalysisService(device)?.getReport(listener, needReport)
    }

    fun hasConnectAffectiveService(device: Device): Boolean {
        return getAnalysisService(device)?.hasConnectAffectiveService() ?: false

    }

    fun hasStartAffectiveService(device: Device): Boolean {
        return getAnalysisService(device)?.hasStartAffectiveService() ?: false
    }

}