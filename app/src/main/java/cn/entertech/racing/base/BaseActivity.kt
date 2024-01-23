package cn.entertech.racing.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = getLayoutResID()
        if (id != -1) {
            setContentView(id)
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        initView()
        initData()
    }

    protected open fun getLayoutResID(): Int = -1

    protected open fun initView() {

    }

    protected open fun initData() {

    }

    override fun onClick(v: View?) {

    }
}