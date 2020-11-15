package com.navigatorsguide.app.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.navigatorsguide.app.R

class CustomProgressDialog : Dialog {
    private var mContext: Context? = null

    constructor(context: Context?) : super(context!!, R.style.CustomProgressDialog) {
        mContext = context
        init()
    }

    constructor(context: Context?, themeResId: Int) : super(context!!, R.style.CustomProgressDialog) {
        init()
    }

    private fun init() {
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.custom_progress_dialog)
        setCancelable(false)
    }

    override fun show() {
        super.show()
    }
}
