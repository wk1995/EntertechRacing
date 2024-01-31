package cn.entertech.racing

import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import cn.entertech.racing.base.BaseDialogFragment

class LoadingDialog : BaseDialogFragment() {
    private var ivCompetitionFinishLoading: ImageView? = null

    // 设置旋转动画
    private val rotateAnimation by lazy {
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        // 设置动画属性
        rotateAnimation.duration = 2000 // 动画持续时间，单位毫秒
        rotateAnimation.repeatCount = Animation.INFINITE // 无限循环
        rotateAnimation
    }

    override fun canCancel(): Boolean {
        return false
    }

    override fun showTitleAble(): Boolean {
        return false
    }

    override fun showOkButton(): Boolean {
        return false
    }

    override fun showCancelButton(): Boolean {
        return false
    }

    override fun getContainResId(): Int {
        return R.layout.racing_competition_loading
    }

    override fun initView(rootView: View) {
        super.initView(rootView)
        ivCompetitionFinishLoading = rootView.findViewById(R.id.ivCompetitionFinishLoading)
    }

    override fun onStart() {
        super.onStart()
        ivCompetitionFinishLoading?.apply {
            clearAnimation()
            // 开始动画
            startAnimation(rotateAnimation)
        }
    }

    override fun onStop() {
        super.onStop()
        ivCompetitionFinishLoading?.clearAnimation()
    }
}