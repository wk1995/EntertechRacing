package cn.entertech.racing

import android.os.Bundle
import android.view.View
import android.widget.TextView
import cn.entertech.racing.base.BaseActivity

/**
 * 结算页面
 * */
class SettlementActivity : BaseActivity() {
    private var tvBack: TextView? = null

    override fun getLayoutResID() = R.layout.racing_settlement_activity

    override fun initView() {
        super.initView()
        tvBack = findViewById(R.id.tvBack)
        tvBack?.setOnClickListener(this)
    }

    override fun initData() {
        super.initData()
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