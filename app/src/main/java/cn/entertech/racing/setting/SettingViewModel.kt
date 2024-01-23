package cn.entertech.racing.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.entertech.racing.setting.item.TrackBlueThreshold
import cn.entertech.racing.setting.item.TrackRedThreshold
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {
    private val _settingItems = MutableSharedFlow<List<ISettingItemFactory<*>>>()
    val settingItems = _settingItems.asSharedFlow()
    private val settingItemFactories by lazy {
        listOf(TrackRedThreshold, TrackBlueThreshold)
    }


    fun getHeadbandSettings() {

    }

    fun initSettingItems(context: Context) {
        viewModelScope.launch {
            settingItemFactories.forEach {

            }
            _settingItems.emit(settingItemFactories)
        }
    }
}