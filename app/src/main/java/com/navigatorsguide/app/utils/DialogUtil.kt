package com.navigatorsguide.app.utils

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.navigatorsguide.app.R

class DialogUtil(title: String?, message: String?, positiveText: String?, negativeText: String?) :
    DialogFragment() {
    private var mTitle: String? = null
    private var mMessage: String? = null
    private var mPositiveButtonText: String? = null
    private var mNegativeButtonText: String? = null
    private var mListener: OnDialogButtonClickListener? = null

    init {
        mTitle = title
        mMessage = message
        mPositiveButtonText = positiveText
        mNegativeButtonText = negativeText
    }

    fun setOnDialogClickListener(onDialogClickListener: OnDialogButtonClickListener?) {
        mListener = onDialogClickListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        val bottomSheet =
            layoutInflater.inflate(R.layout.dialog_bottom_sheet, null)
        val dialog = BottomSheetDialog(requireActivity())
        dialog.setContentView(bottomSheet)
        val titleTextView: TextView? =
            dialog.findViewById(R.id.bottom_title_text)
        val messageTextView: TextView? =
            dialog.findViewById(R.id.bottom_message_text)
        val mButtonYes: Button? =
            dialog.findViewById(R.id.bottom_yes_button)
        val mButtonNo: Button? = dialog.findViewById(R.id.bottom_no_button)

        titleTextView?.text = mTitle
        messageTextView?.text = mMessage
        mButtonYes?.text = mPositiveButtonText
        mButtonNo?.text = mNegativeButtonText


        mButtonYes?.setOnClickListener {
            dismiss()
            if (mListener != null) {
                mListener!!.onPositiveButtonClicked()
            }
        }

        mButtonNo?.setOnClickListener {
            dismiss()
            if (mListener != null) {
                mListener!!.onNegativeButtonClicked()
            }
        }

        dialog.show()
        return dialog
    }

    interface OnDialogButtonClickListener {
        fun onPositiveButtonClicked()
        fun onNegativeButtonClicked()
    }
}