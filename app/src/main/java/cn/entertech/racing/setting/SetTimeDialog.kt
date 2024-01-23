package cn.entertech.racing.setting

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import cn.entertech.racing.R
import cn.entertech.racing.log.EntertechRacingLog
import cn.entertech.racing.wediget.PickerView


class SetTimeDialog : DialogFragment() {
    private var pvTime: PickerView? = null

    companion object {
        private const val TAG = "SetTimeDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val rootView = inflater.inflate(R.layout.set_time_dialog, container, false)
        pvTime = rootView.findViewById(R.id.pvTime)
        val data = ArrayList<String>()
        for (i in 0..10) {
            data.add(i.toString())
        }
        pvTime?.setData(data)
        pvTime?.setOnSelectListener {
            EntertechRacingLog.d(TAG, "select $it")
        }

        return rootView
    }

    override fun onResume() {
        val params: ViewGroup.LayoutParams =
            dialog?.window?.attributes ?: WindowManager.LayoutParams()
        params.width = resources.getDimensionPixelSize(R.dimen.dp343)
        params.height = resources.getDimensionPixelSize(R.dimen.dp346)
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        super.onResume()
    }
}