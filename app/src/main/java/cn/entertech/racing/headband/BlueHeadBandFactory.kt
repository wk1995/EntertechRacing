package cn.entertech.racing.headband

import cn.entertech.racing.R

object BlueHeadBandFactory : BaseHeadBandFactory() {

    override fun getNameResId() = R.string.racing_setting_item_blue_headband
    private const val BLUE_HEAD_BAND_MAC = "BlueHeadBandFactory"

    override fun getKey() = BLUE_HEAD_BAND_MAC
}