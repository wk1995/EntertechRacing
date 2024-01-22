package cn.entertech.racing

import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import cn.entertech.racing.base.BaseActivity
import com.caverock.androidsvg.SVG


/**
 * 比赛页面
 * */
class RacingCompetitionActivity : BaseActivity() {
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
    private var tvRacingErrorBlueConnected: TextView? = null
    private var tvRacingErrorTrackConnected: TextView? = null
    private var tvRacingErrorRedConnected: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.racing_competition_activity)


        initView()
    }

    private fun initView() {
        ivCompetitionStatus = findViewById(R.id.ivCompetitionStatus)
        ivCompetitionSetting = findViewById(R.id.ivCompetitionSetting)
        tvCompetitionTrack = findViewById(R.id.tvCompetitionTrack)
        tvCompetitionHandBand = findViewById(R.id.tvCompetitionHandBand)
        tvCompetitionFinish = findViewById(R.id.tvCompetitionFinish)
        tvBlueAttentionValue = findViewById(R.id.tvBlueAttentionValue)
        tvRedAttentionValue = findViewById(R.id.tvRedAttentionValue)
        tvStartCompetition = findViewById(R.id.tvStartCompetition)
        tvRacingErrorBlueConnected = findViewById(R.id.tvRacingErrorBlueConnected)
        tvRacingErrorTrackConnected = findViewById(R.id.tvRacingErrorTrackConnected)
        tvRacingErrorRedConnected = findViewById(R.id.tvRacingErrorRedConnected)

        tvStartCompetition?.setOnClickListener(this)
        tvCompetitionFinish?.setOnClickListener(this)
        tvCompetitionHandBand?.setOnClickListener(this)

        val svg = SVG.getFromResource(resources, R.raw.racing_rate_bg)
        val pictureDrawable = PictureDrawable(svg.renderToPicture())
        ivCompetitionStatus?.setImageDrawable(pictureDrawable)

    }

    /**
     * 更新UI状态
     * */
    private fun updateUI() {
        //判断蓝牙连接状态
        if (viewModel.blueIsConnected()) {
            if (viewModel.blueIsWear()) {
                tvRacingErrorBlueConnected?.visibility = View.GONE
            } else {
                tvRacingErrorBlueConnected?.visibility = View.VISIBLE
                tvRacingErrorBlueConnected?.setText(R.string.racing_error_headband_wear)
            }
        }

        if (viewModel.redIsConnected()) {
            if (viewModel.redIsWear()) {
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
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvStartCompetition -> {
                viewModel.startCompetition()
            }

            R.id.tvCompetitionFinish -> {
                viewModel.finishCompetition()
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