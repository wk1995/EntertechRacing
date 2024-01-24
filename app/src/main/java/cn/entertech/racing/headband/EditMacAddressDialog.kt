package cn.entertech.racing.headband

import android.view.View
import android.widget.EditText
import cn.entertech.racing.R
import cn.entertech.racing.base.BaseDialogFragment

class EditMacAddressDialog(val select: (String) -> Unit) : BaseDialogFragment() {
    private var etMacAddress: EditText? = null
    override fun getContainResId() = R.layout.edit_mac_address
    override fun initView(rootView: View) {
        super.initView(rootView)
        tvDialogTitle?.setText(R.string.racing_setting_set_mac)
        etMacAddress = rootView.findViewById(R.id.etMacAddress)
    }

    override fun selectOk() {
        super.selectOk()
        etMacAddress?.text?.apply {
            if (this.isNotBlank()) {
                select(this.toString())
            }
        }
    }
}