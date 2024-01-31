package cn.entertech.racing.base

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
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
import androidx.fragment.app.FragmentManager
import cn.entertech.racing.R
import cn.entertech.racing.log.EntertechRacingLog


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
        isCancelable = canCancel()
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
        tvDialogTitle?.visibility = if (showTitleAble()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        tvDialogCancel = rootView.findViewById(R.id.tvDialogCancel)
        tvDialogOk = rootView.findViewById(R.id.tvDialogOk)
        tvDialogOk?.setOnClickListener(this)
        tvDialogCancel?.setOnClickListener(this)
        tvDialogOk?.visibility = if (showOkButton()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        tvDialogCancel?.visibility = if (showCancelButton()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    open fun getContainResId() = -1


    override fun onResume() {
        val params: ViewGroup.LayoutParams =
            dialog?.window?.attributes ?: WindowManager.LayoutParams()
        params.width = resources.getDimensionPixelSize(R.dimen.d171dp)
        params.height = resources.getDimensionPixelSize(R.dimen.d173dp)
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

    open fun showTitleAble() = true

    open fun showOkButton() = true
    open fun showCancelButton() = true

    open fun canCancel() = true


    fun isShowing() = dialog?.isShowing == true


    override fun onDestroyView() {
        flDialogContain?.removeAllViews()
        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        EntertechRacingLog.d(TAG, "$this onDismiss")
    }

    override fun dismiss() {
        super.dismiss()
    }


    override fun show(manager: FragmentManager, tag: String?) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (manager.isDestroyed) return
        }
        try {
            //在每个add事务前增加一个remove事务，防止连续的add
            manager.beginTransaction().remove(this).commit()
            super.show(manager, tag)
        } catch (e: Exception) {
            //同一实例使用不同的tag会异常，这里捕获一下
            e.printStackTrace()
        }
    }
}