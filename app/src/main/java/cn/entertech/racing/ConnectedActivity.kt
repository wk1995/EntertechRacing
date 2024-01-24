package cn.entertech.racing

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.entertech.racing.base.BaseActivity
import cn.entertech.racing.device.DeviceType
import cn.entertech.racing.setting.SettingActivity
import cn.entertech.racing.setting.SettingType

/**
 * 连接页面
 * */
class ConnectedActivity : BaseActivity() {
    private var ivConnectBack: ImageView? = null
    private var ivConnectSettings: ImageView? = null

    private var ivBlueHeadbandConnect: View? = null
    private var ivRedHeadbandConnect: View? = null
    private var clTrackConnect: View? = null

    private var tvBlueHeadbandConnectStatus: TextView? = null
    private var tvFindBlueHeadband: TextView? = null
    private var tvFindBlueHeadbandHint: TextView? = null
    private var ivConnectBlueHeadbandLoading: ImageView? = null

    private var tvRedHeadbandConnectStatus: TextView? = null
    private var tvFindRedHeadband: TextView? = null
    private var tvFindRedHeadbandHint: TextView? = null
    private var ivConnectRedHeadbandLoading: ImageView? = null

    private var tvTrackConnectStatus: TextView? = null
    private var tvFindTrackHint: TextView? = null
    private var tvFindTrack: TextView? = null
    private var ivConnectTrackLoading: ImageView? = null

    private var tvConnectDeviceHintContent1: TextView? = null
    private var tvConnectDeviceHintContent2: TextView? = null
    private lateinit var deviceTypeName: String
    override fun getLayoutResID() = R.layout.racing_connected_activity

    override fun initView() {
        super.initView()
        ivConnectBack = findViewById(R.id.ivConnectBack)
        clTrackConnect = findViewById(R.id.clTrackConnect)
        ivConnectSettings = findViewById(R.id.ivConnectSettings)
        tvBlueHeadbandConnectStatus = findViewById(R.id.tvBlueHeadbandConnectStatus)
        tvFindBlueHeadband = findViewById(R.id.tvFindBlueHeadband)
        tvFindBlueHeadbandHint = findViewById(R.id.tvFindBlueHeadbandHint)
        ivConnectBlueHeadbandLoading = findViewById(R.id.ivConnectBlueHeadbandLoading)
        tvRedHeadbandConnectStatus = findViewById(R.id.tvRedHeadbandConnectStatus)
        tvFindRedHeadband = findViewById(R.id.tvFindRedHeadband)
        tvFindRedHeadbandHint = findViewById(R.id.tvFindRedHeadbandHint)
        ivConnectRedHeadbandLoading = findViewById(R.id.ivConnectRedHeadbandLoading)
        tvConnectDeviceHintContent1 = findViewById(R.id.tvConnectDeviceHintContent1)
        tvConnectDeviceHintContent2 = findViewById(R.id.tvConnectDeviceHintContent2)
        tvTrackConnectStatus = findViewById(R.id.tvTrackConnectStatus)
        tvFindTrackHint = findViewById(R.id.tvFindTrackHint)
        tvFindTrack = findViewById(R.id.tvFindTrack)
        ivConnectTrackLoading = findViewById(R.id.ivConnectTrackLoading)

        ivConnectBack?.setOnClickListener(this)
        ivConnectSettings?.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        deviceTypeName =
            intent.getStringExtra(RacingCompetitionViewModel.BUNDLE_KEY_DEVICE_TYPE) ?: ""
        if (deviceTypeName == DeviceType.DEVICE_TYPE_HEADBAND.name) {
            ivBlueHeadbandConnect?.visibility = View.VISIBLE
            ivRedHeadbandConnect?.visibility = View.VISIBLE
        } else {
            ivBlueHeadbandConnect?.visibility = View.GONE
            ivRedHeadbandConnect?.visibility = View.GONE
        }

        if (deviceTypeName == DeviceType.DEVICE_TYPE_TRACK.name) {
            clTrackConnect?.visibility = View.VISIBLE
        } else {
            clTrackConnect?.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.ivConnectBack -> {
                finish()
            }

            R.id.ivConnectSettings -> {
                if (deviceTypeName == DeviceType.DEVICE_TYPE_HEADBAND.name) {
                    val intent = Intent(this, SettingActivity::class.java)
                    intent.putExtra(
                        SettingType.BUNDLE_KEY_SETTING_TYPE,
                        SettingType.SETTINGS_HEADBAND_MAC.typeName
                    )
                    startActivity(intent)
                }

                if (deviceTypeName == DeviceType.DEVICE_TYPE_TRACK.name) {
                    val intent = Intent(this, SettingActivity::class.java)
                    intent.putExtra(
                        SettingType.BUNDLE_KEY_SETTING_TYPE,
                        SettingType.SETTINGS_TRACK_MAC.typeName
                    )
                    startActivity(intent)
                }
            }
        }
    }
}