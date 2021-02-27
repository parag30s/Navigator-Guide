package com.navigatorsguide.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.Nullable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.navigatorsguide.app.R

class NoteSheetDialogFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?,
    ): View? {
        val v: View = inflater.inflate(R.layout.note_sheet_layout,
            container, false)
        val continueButton: Button = v.findViewById(R.id.continue_button)
        val noteTextView: TextView = v.findViewById(R.id.note_text_view)
        val note = requireArguments().getString("NOTE")
        noteTextView.text = note
        continueButton.setOnClickListener { dismiss() }
        return v
    }
}