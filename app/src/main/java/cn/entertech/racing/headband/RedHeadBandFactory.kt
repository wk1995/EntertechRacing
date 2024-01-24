package cn.entertech.racing.headband

import cn.entertech.racing.R

object RedHeadBandFactory : BaseHeadBandFactory() {

    override fun getNameResId() = R.string.racing_setting_item_red_headband
    private const val RED_HEAD_BAND_MAC = "RedHeadBandFactory"

    override fun getKey() = RED_HEAD_BAND_MAC
}