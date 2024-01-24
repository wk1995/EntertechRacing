package cn.entertech.racing.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import cn.entertech.racing.R


open class BaseDialogFragment() : DialogFragment(), OnClickListener {
    private var flDialogContain: FrameLayout? = null
    protected var tvDialogTitle: TextView? = null
    private var tvDialogCancel: TextView? = null
    private var tvDialogOk: TextView? = null

    companion object {
        private const val TAG = "BaseDialogFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val rootView = inflater.inflate(R.layout.set_time_dialog, container, false)
        initView(rootView)
        return rootView
    }

    open fun initView(rootView: View) {
        flDialogContain = rootView.findViewById(R.id.flDialogContain)
        flDialogContain?.apply {
            val layoutId = getContainResId()
            if (layoutId > 0) {
                val childView = LayoutInflater.from(context).inflate(layoutId, this, false)
                this.addView(childView)
            }
        }

        tvDialogTitle = rootView.findViewById(R.id.tvDialogTitle)
        tvDialogCancel = rootView.findViewById(R.id.tvDialogCancel)
        tvDialogOk = rootView.findViewById(R.id.tvDialogOk)
        tvDialogOk?.setOnClickListener(this)
        tvDialogCancel?.setOnClickListener(this)
    }

    open fun getContainResId() = -1


    override fun onResume() {
        val params: ViewGroup.LayoutParams =
            dialog?.window?.attributes ?: WindowManager.LayoutParams()
        params.width = resources.getDimensionPixelSize(R.dimen.d343dp)
        params.height = resources.getDimensionPixelSize(R.dimen.d346dp)
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        super.onResume()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvDialogOk -> {
                selectOk()
                dismiss()
            }

            R.id.tvDialogCancel -> {
                selectCancel()
                dismiss()
            }
        }
    }

    open fun selectOk() {

    }

    open fun selectCancel() {

    }
}