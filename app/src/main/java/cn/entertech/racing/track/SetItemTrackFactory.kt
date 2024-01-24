package cn.entertech.racing.track

import cn.entertech.racing.R
import cn.entertech.racing.headband.BaseHeadBandFactory

object SetItemTrackFactory : BaseHeadBandFactory() {
    private const val SETTINGS_TRACK_MAC = "settings_track_mac"
    override fun getNameResId() = R.string.racing_track

    override fun getKey() = SETTINGS_TRACK_MAC
}