package cn.entertech.racing.device

/**
 * 设备
 * @param deviceType 设备类型
 * */
enum class Device(val deviceType: DeviceType) {
    Blue(DeviceType.DEVICE_TYPE_HEADBAND),
    Red(DeviceType.DEVICE_TYPE_HEADBAND),
    Track(DeviceType.DEVICE_TYPE_TRACK)
}