package cn.entertech.racing.base

import android.widget.Toast
import cn.entertech.racing.RacingApplication

object ToastUtils {
    fun showShort(msg: String) {
        Toast.makeText(RacingApplication.getInstance(), msg, Toast.LENGTH_SHORT).show()
    }
}