package cn.entertech.racing.setting

enum class SettingType(val typeName: String) {
    SETTINGS_SYSTEM("settings"),
    SETTINGS_HEADBAND_MAC("headbandMac"),
    SETTINGS_UN_KNOW("un_know"),

    SETTINGS_TRACK_MAC("trackMac");

    companion object {
        private val map by lazy {
            val map = HashMap<String, SettingType>()
            SettingType.values().forEach {
                map[it.typeName] = it
            }
            map
        }

        fun getSettingTypeByName(typeName: String) = map[typeName] ?: SETTINGS_UN_KNOW

        const val BUNDLE_KEY_SETTING_TYPE="settingType"
    }
}