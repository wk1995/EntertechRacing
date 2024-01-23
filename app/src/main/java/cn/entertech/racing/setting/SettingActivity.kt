package cn.entertech.racing.setting

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.entertech.racing.IRecyclerViewItemClickListener
import cn.entertech.racing.R
import cn.entertech.racing.base.BaseActivity
import cn.entertech.racing.setting.SettingType.Companion.BUNDLE_KEY_SETTING_TYPE
import kotlinx.coroutines.launch


/**
 * 设置页面
 * */
class SettingActivity : BaseActivity(), IRecyclerViewItemClickListener {
    private val settingViewModel by lazy {
        ViewModelProvider(this)[SettingViewModel::class.java]
    }

    private val mSettingListAdapter by lazy {
        SettingListAdapter(listener = this)
    }


    private var ivSettingBack: ImageView? = null
    private var rvSettingList: RecyclerView? = null

    override fun getLayoutResID() = R.layout.racing_setting_activity

    override fun initView() {
        ivSettingBack = findViewById(R.id.ivSettingBack)
        rvSettingList = findViewById(R.id.rvSettingList)
        ivSettingBack?.setOnClickListener(this)
        rvSettingList?.adapter = mSettingListAdapter
        rvSettingList?.layoutManager = LinearLayoutManager(this)
        rvSettingList?.addItemDecoration(SettingItemDecoration())
    }

    override fun initData() {
        super.initData()
        lifecycleScope.launch {
            settingViewModel.settingItems.collect {
                mSettingListAdapter.replace(it)
            }
        }
        val settingType = intent.getStringExtra(BUNDLE_KEY_SETTING_TYPE) ?: ""
        when (SettingType.getSettingTypeByName(settingType)) {
            SettingType.SETTINGS_SYSTEM -> {
                settingViewModel.initSettingItems(this)
            }

            SettingType.SETTINGS_HEADBAND_MAC -> {
                settingViewModel.initHeadbandMacSettings()
            }

            SettingType.SETTINGS_TRACK_MAC -> {
                settingViewModel.initTrackMacSettings()
            }

            else -> {
                finish()
            }

        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivSettingBack -> {
                finish()
            }
        }
    }

    override fun onItemClick(rv: RecyclerView?, clickView: View, index: Int) {
        val item = mSettingListAdapter.getItemByPosition(index)
        item.showDialog(this) {
            mSettingListAdapter.notifyItemChanged(index)
        }

    }
}