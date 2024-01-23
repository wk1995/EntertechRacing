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


class SetTimeDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.set_time_dialog, container, false)
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