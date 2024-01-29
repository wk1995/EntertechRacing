package cn.entertech.racing

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.entertech.racing.base.BaseActivity
import cn.entertech.racing.log.EntertechRacingLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * 比赛页面
 * */
class RacingCompetitionActivity : BaseActivity() {

    companion object {
        private const val TAG = "RacingCompetitionActivity"
    }

    private val viewModel: RacingCompetitionViewModel by lazy {
        ViewModelProvider(this)[RacingCompetitionViewModel::class.java]
    }
    private var ivCompetitionStatus: ImageView? = null
    private var ivCompetitionSetting: ImageView? = null
    private var tvCompetitionTrack: TextView? = null
    private var tvCompetitionHandBand: TextView? = null
    private var tvCompetitionFinish: TextView? = null
    private var tvBlueAttentionValue: TextView? = null
    private var tvRedAttentionValue: TextView? = null
    private var tvStartCompetition: TextView? = null
    private var tvRemainingTime: TextView? = null
    private var pbCompetitionProgress: ProgressBar? = null
    private var tvRacingErrorBlueConnected: TextView? = null
    private var tvRacingErrorTrackConnected: TextView? = null
    private var tvRacingErrorRedConnected: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.racing_competition_activity)
        initView()
    }

    override fun initView() {
        ivCompetitionStatus = findViewById(R.id.ivCompetitionStatus)
        ivCompetitionSetting = findViewById(R.id.ivCompetitionSetting)
        tvCompetitionTrack = findViewById(R.id.tvCompetitionTrack)
        tvCompetitionHandBand = findViewById(R.id.tvCompetitionHandBand)
        tvCompetitionFinish = findViewById(R.id.tvCompetitionFinish)
        tvBlueAttentionValue = findViewById(R.id.tvBlueAttentionValue)
        tvRedAttentionValue = findViewById(R.id.tvRedAttentionValue)
        tvStartCompetition = findViewById(R.id.tvStartCompetition)
        tvRemainingTime = findViewById(R.id.tvRemainingTime)
        pbCompetitionProgress = findViewById(R.id.pbCompetitionProgress)
        tvRacingErrorBlueConnected = findViewById(R.id.tvRacingErrorBlueConnected)
        tvRacingErrorTrackConnected = findViewById(R.id.tvRacingErrorTrackConnected)
        tvRacingErrorRedConnected = findViewById(R.id.tvRacingErrorRedConnected)

        tvStartCompetition?.setOnClickListener(this)
        tvCompetitionTrack?.setOnClickListener(this)
        tvCompetitionFinish?.setOnClickListener(this)
        tvCompetitionHandBand?.setOnClickListener(this)
        ivCompetitionSetting?.setOnClickListener(this)
        /* ivCompetitionStatus?.apply {
             Glide.with(this).load(R.drawable.racing_rate_bg).into(this)
         }*/

        /*try {
            val svgInputStream = resources.openRawResource(R.raw.racing_rate_bg)
            val svg = SVG.getFromInputStream(svgInputStream)
            ivCompetitionStatus?.setLayerType(
                ImageView.LAYER_TYPE_SOFTWARE,
                null
            ) // Required for animated SVGs

            // 使用 Glide 显示 SVG
            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE) // Disable disk caching for SVGs
            ivCompetitionStatus?.apply {
                Glide.with(this@RacingCompetitionActivity)
                    .load(svg.renderToPicture())
                    .apply(requestOptions)
                    .into(this)
            }

        } catch (e: SVGParseException) {
            e.printStackTrace()
        }*/

        /*try{
            val  svg = SVG.getFromResource(this, R.drawable.racing_rate_bg)
            ivCompetitionStatus?.setSVG(svg)

            // 将 SVG 对象设置到 ImageView 中
            ivCompetitionStatus?.setImageDrawable(PictureDrawable(svg.renderToPicture()))

            // 创建一个简单的旋转动画
            ivCompetitionStatus?.apply {
                val animator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f)
                animator.duration = 5000 // 设置动画持续时间为5秒

                animator.repeatCount = ObjectAnimator.INFINITE // 设置动画无限循环

                animator.start() // 启动动画
            }

        } catch (e : SVGParseException) {
        }*/
        /*  val svg = SVG.getFromResource(resources, R.raw.racing_rate_bg)
          val pictureDrawable = PictureDrawable(svg.renderToPicture())
          ivCompetitionStatus?.setImageDrawable(pictureDrawable)*/

        /* ivCompetitionStatus?.drawable?.apply {
             if(this is Animatable ){
                 start()
             }
         }*/

        /*lifecycleScope.launch(Dispatchers.Main) {
            viewModel.showLoading.collect {
                var mLoadingDialog: LoadingDialog? =
                    supportFragmentManager.findFragmentByTag("LoadingDialog") as LoadingDialog?
                EntertechRacingLog.d(TAG, "showLoading $it mLoadingDialog $mLoadingDialog")
                if (it) {
                    if (mLoadingDialog == null) {
                        mLoadingDialog = LoadingDialog()
                    }
                    if (mLoadingDialog?.isShowing() != true) {
                        mLoadingDialog?.show(supportFragmentManager, "LoadingDialog")
                    }
                } else {
                    mLoadingDialog?.dismiss()
                }
            }
        }*/



        lifecycleScope.launch(Dispatchers.Main) {
            launch {
                viewModel.racingStatus.collect {
                    EntertechRacingLog.d(TAG, "racingStatus: $it ")
                    when (it) {
                        RacingStatus.COMPETITIONING -> {
                            pbCompetitionProgress?.visibility = View.VISIBLE
                            tvCompetitionFinish?.visibility = View.VISIBLE
                            tvRemainingTime?.visibility = View.VISIBLE
                            tvStartCompetition?.visibility = View.GONE
                            tvCompetitionTrack?.visibility = View.GONE
                            tvCompetitionHandBand?.visibility = View.GONE
                            ivCompetitionSetting?.visibility = View.GONE

                        }

                        RacingStatus.PRE_COMPETITION -> {
                            pbCompetitionProgress?.visibility = View.GONE
                            tvRemainingTime?.visibility = View.GONE
                            tvStartCompetition?.visibility =
                                if (
                                    ((viewModel.blueIsConnected() && viewModel.blueIsWear.value)
                                            || (viewModel.redIsConnected() && viewModel.redIsWear.value)) && viewModel.trackIsConnected()
                                ) {
                                    View.VISIBLE
                                } else {
                                    View.GONE
                                }

                            tvCompetitionFinish?.visibility = View.GONE
                            tvCompetitionTrack?.visibility = View.VISIBLE
                            tvCompetitionHandBand?.visibility = View.VISIBLE
                            ivCompetitionSetting?.visibility = View.VISIBLE
                        }

                        RacingStatus.COMPETITION_END -> {
                            viewModel.resetCompetition()
                            viewModel.gotoSettlement(this@RacingCompetitionActivity)
                        }
                    }
                }
            }
            launch {
                viewModel.updateUi.collect {
                    EntertechRacingLog.d(TAG, "updateUi: $it ")
                    updateUI()
                }
            }
            launch {
                viewModel.blueAttention.collect {
                    EntertechRacingLog.d(TAG, "blueAttention: $it ")
                    if (it == 0) {
                        tvBlueAttentionValue?.text = "--"
                    } else {
                        tvBlueAttentionValue?.text = it.toString()
                    }
                }
            }
            launch {
                viewModel.redAttention.collect {
                    EntertechRacingLog.d(TAG, "redAttention: $it ")
                    if (it == 0) {
                        tvRedAttentionValue?.text = "--"
                    } else {
                        tvRedAttentionValue?.text = it.toString()
                    }
                }
            }

            launch {
                viewModel.remainingTime.collect {
                    EntertechRacingLog.d(TAG, "remainingTime: $it ")
                    tvRemainingTime?.text = "剩余时间 $it"
                }
            }

            launch {
                viewModel.blueIsWear.collect {
                    if (viewModel.blueIsConnected()) {
                        if (it) {
                            tvRacingErrorBlueConnected?.visibility = View.GONE
                        } else {
                            tvRacingErrorBlueConnected?.visibility = View.VISIBLE
                            tvRacingErrorBlueConnected?.setText(R.string.racing_error_headband_wear)
                        }
                    }
                }
            }

            launch {
                viewModel.redIsWear.collect {
                    if (viewModel.redIsConnected()) {
                        if (it) {
                            tvRacingErrorBlueConnected?.visibility = View.GONE
                        } else {
                            tvRacingErrorBlueConnected?.visibility = View.VISIBLE
                            tvRacingErrorBlueConnected?.setText(R.string.racing_error_headband_wear)
                        }
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        EntertechRacingLog.d(TAG, "onResume $this")
        updateUI()
        viewModel.listenerAllDeviceDisconnect()
        viewModel.listenerHeadbandRawData()
        viewModel.listenerHeadbandContactStatus()
    }

    override fun onPause() {
        super.onPause()
        viewModel.removeAllDeviceDisconnectListener()
        viewModel.removeHeadbandRawDataListener()
        viewModel.removeHeadbandContactStatus()
    }


    /**
     * 更新UI状态
     * */
    private fun updateUI() {
        //判断蓝牙连接状态
        if (viewModel.blueIsConnected()) {
            if (viewModel.blueIsWear.value) {
                tvRacingErrorBlueConnected?.visibility = View.GONE
            } else {
                tvRacingErrorBlueConnected?.visibility = View.VISIBLE
                tvRacingErrorBlueConnected?.setText(R.string.racing_error_headband_wear)
            }
        }

        if (viewModel.redIsConnected()) {
            if (viewModel.redIsWear.value) {
                tvRacingErrorRedConnected?.visibility = View.GONE
            } else {
                tvRacingErrorRedConnected?.visibility = View.VISIBLE
                tvRacingErrorRedConnected?.setText(R.string.racing_error_headband_wear)
            }
        }
        tvRacingErrorTrackConnected?.visibility =
            if (viewModel.trackIsConnected()) {
                View.GONE
            } else {
                View.VISIBLE
            }

        tvStartCompetition?.visibility =
            if (
                ((viewModel.blueIsConnected() && viewModel.blueIsWear.value)
                        || (viewModel.redIsConnected() && viewModel.redIsWear.value)) && viewModel.trackIsConnected()
            ) {
                View.VISIBLE
            } else {
                View.GONE
            }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvStartCompetition -> {
                viewModel.startCompetition()
            }

            R.id.tvCompetitionFinish -> {
                viewModel.finishCompetition(false)
            }

            R.id.tvCompetitionHandBand -> {
                viewModel.gotoHandBand(this)
            }

            R.id.tvCompetitionTrack -> {
                viewModel.gotoTrack(this)
            }

            R.id.ivCompetitionSetting -> {
                viewModel.gotoSetting(this)
            }
        }
    }
}