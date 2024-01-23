package cn.entertech.racing.setting.item

import android.view.View
import cn.entertech.racing.R
import cn.entertech.racing.base.BaseDialogFragment
import cn.entertech.racing.log.EntertechRacingLog
import cn.entertech.racing.wediget.PickerView

class DoublePickDialog(val select: (Int) -> Unit) : BaseDialogFragment() {
    companion object {
        private const val TAG = "DoublePickDialog"
    }

    private var pvCelebrateTimeMin: PickerView<Int>? = null
    private var pvCelebrateTimeSecond: PickerView<Int>? = null
    private var min: Int? = null
    private var second: Int? = null
    override fun getContainResId(): Int {
        return R.layout.set_each_time_dialog
    }

    override fun initView(rootView: View) {
        super.initView(rootView)
        pvCelebrateTimeMin = rootView.findViewById(R.id.pvCelebrateTimeMin)
        pvCelebrateTimeSecond = rootView.findViewById(R.id.pvCelebrateTimeSecond)
        val minRang = ArrayList<Int>()
        for (i in 0..10) {
            minRang.add(i)
        }
        val secondRange = ArrayList<Int>()
        for (i in 0..59) {
            secondRange.add(i)
        }
        pvCelebrateTimeSecond?.setData(secondRange)
        pvCelebrateTimeMin?.setData(minRang)
        pvCelebrateTimeSecond?.setOnSelectListener {
            EntertechRacingLog.d(TAG, "select second $it")
            second = it
        }
        pvCelebrateTimeMin?.setOnSelectListener {
            EntertechRacingLog.d(TAG, "select min $it")
            min = it
        }
        second = pvCelebrateTimeSecond?.selectedData
        min = pvCelebrateTimeMin?.selectedData
    }

    override fun selectOk() {
        super.selectOk()
        var result: Int? = null
        result = min?.let {
            it * 60
        }
        if (result == null) {
            result = second
        } else {
            second?.apply {
                result += this
            }
        }
        result?.apply {
            select(this)
        }

    }
}