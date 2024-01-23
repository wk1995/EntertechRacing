package cn.entertech.racing.setting

data class SettingItemBean(
    val name: String, val value: String
) {
    fun getItemText() = "$name|$value"
}
