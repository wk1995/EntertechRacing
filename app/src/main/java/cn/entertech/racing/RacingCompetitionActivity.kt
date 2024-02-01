package cn.entertech.racing

import android.animation.Animator
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
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * 比赛页面
 * */
class RacingCompetitionActivity : BaseActivity() {

    companion object {
        private const val TAG = "RacingCompetitionActivity"
    }

    private var mLoadingDialog: LoadingDialog? = null

    private val viewModel: RacingCompetitionViewModel by lazy {
        ViewModelProvider(this)[RacingCompetitionViewModel::class.java]
    }
    private var ivCompetitionStatus: ImageView? = null
    private var ivCompetitionSetting: ImageView? = null
    private var ivBlueCelebrateAnimation: LottieAnimationView? = null
    private var ivBlueCelebrateAnimation1: LottieAnimationView? = null
    private var ivRedCelebrateAnimation: LottieAnimationView? = null
    private var ivRedCelebrateAnimation1: LottieAnimationView? = null
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
        ivBlueCelebrateAnimation = findViewById(R.id.ivBlueCelebrateAnimation)
        ivRedCelebrateAnimation = findViewById(R.id.ivRedCelebrateAnimation)
        ivBlueCelebrateAnimation1 = findViewById(R.id.ivBlueCelebrateAnimation1)
        ivRedCelebrateAnimation1 = findViewById(R.id.ivRedCelebrateAnimation1)
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

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.blueCelebrate.collect {
                EntertechRacingLog.d(TAG, "blueCelebrate")
                listOf(ivBlueCelebrateAnimation1, ivBlueCelebrateAnimation).forEach {
                    it?.apply {
                        visibility = View.VISIBLE
                        cancelAnimation()
                        addAnimatorListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                            }

                            override fun onAnimationEnd(animation: Animator) {
                                visibility = View.GONE
                            }

                            override fun onAnimationCancel(animation: Animator) {
                            }

                            override fun onAnimationRepeat(animation: Animator) {
                            }
                        })
                        playAnimation()
                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.redCelebrate.collect {
                EntertechRacingLog.d(TAG, "redCelebrate")
                listOf(ivRedCelebrateAnimation1, ivRedCelebrateAnimation).forEach {
                    it?.apply {
                        visibility = View.VISIBLE
                        cancelAnimation()
                        addAnimatorListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                            }

                            override fun onAnimationEnd(animation: Animator) {
                                visibility = View.GONE
                            }

                            override fun onAnimationCancel(animation: Animator) {
                            }

                            override fun onAnimationRepeat(animation: Animator) {
                            }
                        })
                        playAnimation()
                    }
                }

            }
        }


        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.showLoading.collect {
                EntertechRacingLog.d(TAG, "showLoading $it mLoadingDialog $mLoadingDialog")
                if (it) {
                    if (mLoadingDialog?.isShowing() != true) {
                        mLoadingDialog?.show(supportFragmentManager, "LoadingDialog")
                    }
                } else {
                    if (mLoadingDialog?.isShowing() == true) {
                        mLoadingDialog?.dismiss()
                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.blueAttention.collect {
                EntertechRacingLog.d(TAG, "blueAttention: $it ")
                if (it == 0) {
                    tvBlueAttentionValue?.text = "--"
                } else {
                    tvBlueAttentionValue?.text = it.toString()
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.redAttention.collect {
                EntertechRacingLog.d(TAG, "redAttention: $it ")
                if (it == 0) {
                    tvRedAttentionValue?.text = "--"
                } else {
                    tvRedAttentionValue?.text = it.toString()
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.remainingTime.collect {
                EntertechRacingLog.d(TAG, "remainingTime: $it ")
                tvRemainingTime?.text = "剩余时间 $it"
                pbCompetitionProgress?.progress = viewModel.competitionProgress
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.blueIsWear.collect {
                if (viewModel.blueIsConnected()) {
                    if (it) {
                        tvRacingErrorBlueConnected?.visibility = View.GONE
                    } else {
                        tvRacingErrorBlueConnected?.visibility = View.VISIBLE
                        tvRacingErrorBlueConnected?.setText(R.string.racing_error_headband_wear)
                    }
                }
                updateUI()
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.redIsWear.collect {
                if (viewModel.redIsConnected()) {
                    if (it) {
                        tvRacingErrorRedConnected?.visibility = View.GONE
                    } else {
                        tvRacingErrorRedConnected?.visibility = View.VISIBLE
                        tvRacingErrorRedConnected?.setText(R.string.racing_error_headband_wear)
                    }
                }
                updateUI()
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.updateUi.collect {
                EntertechRacingLog.d(TAG, "updateUi: $it ")
                updateUI()
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
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
    }

    override fun onStart() {
        super.onStart()
        if (mLoadingDialog == null) {
            mLoadingDialog =
                supportFragmentManager.findFragmentByTag("LoadingDialog") as LoadingDialog?
            if (mLoadingDialog == null) {
                mLoadingDialog = LoadingDialog()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mLoadingDialog = null
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