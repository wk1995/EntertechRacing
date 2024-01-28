package cn.entertech.racing

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.entertech.racing.base.BaseActivity

/**
 * 结算页面
 * */
class SettlementActivity : BaseActivity() {
    private var tvBack: TextView? = null
    private var includeSettlementOne: View? = null
    private var includeSettlementTwo: View? = null
    private var ivOrderOne: ImageView? = null
    private var ivOrderTwo: ImageView? = null
    private var ivHeadbandOne: ImageView? = null
    private var ivHeadbandTwo: ImageView? = null
    private var tvAvgAttentionValueOne: TextView? = null
    private var tvAvgAttentionValueTwo: TextView? = null
    override fun getLayoutResID() = R.layout.racing_settlement_activity

    override fun initView() {
        super.initView()
        tvBack = findViewById(R.id.tvBack)
        includeSettlementOne = findViewById(R.id.includeSettlementOne)
        ivOrderOne = includeSettlementOne?.findViewById(R.id.ivOrder)
        tvAvgAttentionValueOne = includeSettlementOne?.findViewById(R.id.tvAvgAttentionValue)
        ivOrderOne?.setImageResource(R.drawable.racing_settlement_number_one)
        ivHeadbandOne = includeSettlementOne?.findViewById(R.id.ivHeadband)


        includeSettlementTwo = findViewById(R.id.includeSettlementTwo)
        tvAvgAttentionValueTwo = includeSettlementTwo?.findViewById(R.id.tvAvgAttentionValue)
        ivOrderTwo = includeSettlementTwo?.findViewById(R.id.ivOrder)
        ivOrderTwo?.setImageResource(R.drawable.racing_settlement_number_two)
        ivHeadbandTwo = includeSettlementTwo?.findViewById(R.id.ivHeadband)
        tvBack?.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
        val redScore = intent.getIntExtra(RacingCompetitionViewModel.BUNDLE_KEY_RED_SCORE, 0)
        val blueScore = intent.getIntExtra(RacingCompetitionViewModel.BUNDLE_KEY_BLUE_SCORE, 0)
        if (redScore == 0) {
            if (blueScore == 0) {
//出问题了
            } else {
//只有blue头环

            }
        } else {
            if (blueScore == 0) {
//只有red头环
            } else {
                //两个头环
                if (redScore > blueScore) {
                    ivHeadbandOne?.setImageResource(R.drawable.settlement_item_red_text)
                    ivHeadbandTwo?.setImageResource(R.drawable.settlement_item_blue_text)
                    tvAvgAttentionValueOne?.text = blueScore.toString()
                    tvAvgAttentionValueTwo?.text = redScore.toString()

                } else if (redScore < blueScore) {
                    ivHeadbandOne?.setImageResource(R.drawable.settlement_item_blue_text)
                    ivHeadbandTwo?.setImageResource(R.drawable.settlement_item_red_text)
                    tvAvgAttentionValueOne?.text = redScore.toString()
                    tvAvgAttentionValueTwo?.text = blueScore.toString()
                } else {

                }
            }
        }

    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.tvBack -> {
                finish()
            }
        }
    }
}