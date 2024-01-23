package cn.entertech.racing.setting.item

import android.view.View
import cn.entertech.racing.R
import cn.entertech.racing.base.BaseDialogFragment
import cn.entertech.racing.log.EntertechRacingLog
import cn.entertech.racing.wediget.PickerView

class SetHeadbandThresholdDialog(val select: (Int) -> Unit) : BaseDialogFragment() {
    companion object {
        private const val TAG = "SetHeadbandThresholdDialog"
    }

    private var currentValue: Int? = null

    private var pvSelectInitValue: PickerView<Int>? = null
    override fun getContainResId() = R.layout.set_headband_threshold

    override fun initView(rootView: View) {
        super.initView(rootView)
        pvSelectInitValue = rootView.findViewById(R.id.pvSelectInitValue)
        val data = ArrayList<Int>()
        for (i in 0..100) {
            data.add(i)
        }
        pvSelectInitValue?.setData(data)
        pvSelectInitValue?.setOnSelectListener {
            EntertechRacingLog.d(TAG, "select $it")
            currentValue = it
        }
    }

    override fun selectOk() {
        super.selectOk()
        currentValue?.apply {
            select(this)
        }
    }
}