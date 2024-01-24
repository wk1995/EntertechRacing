package cn.entertech.racing.connect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.entertech.racing.R
import cn.entertech.racing.RacingCompetitionViewModel
import cn.entertech.racing.base.BaseActivity
import cn.entertech.racing.device.DeviceType
import cn.entertech.racing.log.EntertechRacingLog
import cn.entertech.racing.setting.SettingActivity
import cn.entertech.racing.setting.SettingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

/**
 * 连接页面
 * */
class ConnectedActivity : BaseActivity() {
    companion object {
        private const val TAG = "ConnectedActivity"
    }

    private val viewModel: ConnectDeviceViewModel by lazy {
        ViewModelProvider(this)[ConnectDeviceViewModel::class.java]
    }

    // 设置旋转动画
    private val rotateAnimation by lazy {
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        // 设置动画属性
        rotateAnimation.duration = 2000 // 动画持续时间，单位毫秒
        rotateAnimation.repeatCount = Animation.INFINITE // 无限循环
        rotateAnimation
    }


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
        ivBlueHeadbandConnect = findViewById(R.id.ivBlueHeadbandConnect)
        ivRedHeadbandConnect = findViewById(R.id.ivRedHeadbandConnect)
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
        tvFindRedHeadband?.setOnClickListener(this)
        tvFindBlueHeadband?.setOnClickListener(this)
        tvFindTrack?.setOnClickListener(this)
        initPermission()
    }

    private fun initPermission() {
        val needPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        val needRequestPermissions = ArrayList<String>()
        for (i in needPermission.indices) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    needPermission[i]
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                needRequestPermissions.add(needPermission[i])
            }
        }
        if (needRequestPermissions.size != 0) {
            val permissions = arrayOfNulls<String>(needRequestPermissions.size)
            for (i in needRequestPermissions.indices) {
                permissions[i] = needRequestPermissions[i]
            }
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }

    override fun initData() {
        super.initData()
        deviceTypeName =
            intent.getStringExtra(RacingCompetitionViewModel.BUNDLE_KEY_DEVICE_TYPE) ?: ""
        EntertechRacingLog.d(TAG, "deviceTypeName: $deviceTypeName")
        lifecycleScope.launch(Dispatchers.Main) {
            if (deviceTypeName == DeviceType.DEVICE_TYPE_HEADBAND.name) {
                launch {
                    viewModel.connectBlueResult.collect {
                        EntertechRacingLog.d(TAG, "collect BlueResult")
                        updateBlueUi()
                    }
                }

                launch {
                    viewModel.connectRedResult.collect {
                        EntertechRacingLog.d(TAG, "collect RedResult")
                        updateRedUi()
                    }

                }
            }
            if (deviceTypeName == DeviceType.DEVICE_TYPE_TRACK.name) {
                launch {
                    viewModel.connectTrackResult.collect {
                        EntertechRacingLog.d(TAG, "collect Track Result")
                        updateTrackUi()
                    }
                }
            }

        }
        updateUi()

    }

    private fun updateBlueUi() {
        if (deviceTypeName == DeviceType.DEVICE_TYPE_HEADBAND.name) {
            ivBlueHeadbandConnect?.visibility = View.VISIBLE
            if (viewModel.blueIsConnect()) {
                tvBlueHeadbandConnectStatus?.setText(R.string.racing_device_has_connected)
                tvFindBlueHeadband?.visibility = View.VISIBLE
                tvFindBlueHeadbandHint?.visibility = View.VISIBLE
                ivConnectBlueHeadbandLoading?.clearAnimation()
                ivConnectBlueHeadbandLoading?.visibility = View.GONE
            } else {
                tvBlueHeadbandConnectStatus?.setText(R.string.racing_device_connecting)
                tvFindBlueHeadband?.visibility = View.GONE
                tvFindBlueHeadbandHint?.visibility = View.GONE
                ivConnectBlueHeadbandLoading?.apply {
                    clearAnimation()
                    visibility = View.VISIBLE
                    // 开始动画
                    startAnimation(rotateAnimation)
                }
                viewModel.connectBlueDevice()
            }
        } else {
            ivBlueHeadbandConnect?.visibility = View.GONE
        }
    }

    private fun updateRedUi() {
        if (deviceTypeName == DeviceType.DEVICE_TYPE_HEADBAND.name) {
            ivRedHeadbandConnect?.visibility = View.VISIBLE
            if (viewModel.redIsConnect()) {
                tvRedHeadbandConnectStatus?.setText(R.string.racing_device_has_connected)
                tvFindRedHeadband?.visibility = View.VISIBLE
                tvFindRedHeadbandHint?.visibility = View.VISIBLE
                ivConnectRedHeadbandLoading?.clearAnimation()
                ivConnectRedHeadbandLoading?.visibility = View.GONE
            } else {
                tvRedHeadbandConnectStatus?.setText(R.string.racing_device_connecting)
                tvFindRedHeadband?.visibility = View.GONE
                tvFindRedHeadbandHint?.visibility = View.GONE
                ivConnectRedHeadbandLoading?.apply {
                    clearAnimation()
                    visibility = View.VISIBLE
                    // 开始动画
                    startAnimation(rotateAnimation)
                }
                viewModel.connectRedDevice()
            }
        } else {
            ivRedHeadbandConnect?.visibility = View.GONE
        }
    }

    private fun updateTrackUi() {
        if (deviceTypeName == DeviceType.DEVICE_TYPE_TRACK.name) {
            clTrackConnect?.visibility = View.VISIBLE
            if (viewModel.trackIsConnect()) {
                tvTrackConnectStatus?.setText(R.string.racing_device_has_connected)
                tvFindTrack?.visibility = View.VISIBLE
                tvFindTrackHint?.visibility = View.VISIBLE
                ivConnectTrackLoading?.clearAnimation()
                ivConnectTrackLoading?.visibility = View.GONE
            } else {
                tvTrackConnectStatus?.setText(R.string.racing_device_connecting)
                tvFindTrack?.visibility = View.GONE
                tvFindTrackHint?.visibility = View.GONE
                ivConnectTrackLoading?.apply {
                    clearAnimation()
                    visibility = View.VISIBLE
                    // 开始动画
                    startAnimation(rotateAnimation)
                }
                viewModel.connectTrackDevice()
            }
        } else {
            clTrackConnect?.visibility = View.GONE
        }
    }


    private fun updateUi() {
        updateBlueUi()
        updateRedUi()
        updateTrackUi()
    }


    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.ivConnectBack -> {
                finish()
            }

            R.id.tvFindTrack -> {
                viewModel.findTrack()
            }

            R.id.tvFindBlueHeadband -> {
                viewModel.findBlueHeadband()
            }

            R.id.tvFindRedHeadband -> {
                viewModel.findRedHeadband()
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

    override fun onDestroy() {
        ivConnectTrackLoading?.clearAnimation()
        ivConnectRedHeadbandLoading?.clearAnimation()
        ivConnectBlueHeadbandLoading?.clearAnimation()
        super.onDestroy()
    }
}