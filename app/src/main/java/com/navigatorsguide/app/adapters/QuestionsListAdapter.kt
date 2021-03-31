package com.navigatorsguide.app.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.navigatorsguide.app.R
import com.navigatorsguide.app.database.entities.Questions
import com.navigatorsguide.app.ui.home.QuestionsActivity
import com.navigatorsguide.app.utils.AppConstants
import com.navigatorsguide.app.utils.AppUtils
import com.navigatorsguide.app.utils.ImageBitmapUtils
import com.navigatorsguide.app.utils.ImagePreviewActivity
import java.util.*


class QuestionsListAdapter internal constructor(
    private val context: Context,
    private val listGroupData: List<Questions>,
    private val listChildData: HashMap<Questions, List<String>>,
    private var mListener: OnCommentPostClickListener,
) : BaseExpandableListAdapter(), RadioGroup.OnCheckedChangeListener {

    interface OnCommentPostClickListener {
        fun onCommentPostItemClick(qid: String, comment: String)
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return this.listChildData.get(this.listGroupData.get(listPosition))!!.get(
            expandedListPosition
        )
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        var convertView = convertView
        val expandedListText = getChild(listPosition, expandedListPosition) as String
        if (convertView == null) {
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.item_question_child, null)
        }
        var questionModel: Questions = listGroupData.get(listPosition)
        val radioGroupButton = convertView!!.findViewById<RadioGroup>(R.id.radio_group_button)
        val radioYa = convertView!!.findViewById<RadioButton>(R.id.radio_yes_button)
        val radioNo = convertView!!.findViewById<RadioButton>(R.id.radio_no_button)
        val radioNa = convertView!!.findViewById<RadioButton>(R.id.radio_na_button)
        val answerTextView = convertView!!.findViewById<TextView>(R.id.answer_text_view)
        val commentTextView = convertView!!.findViewById<TextView>(R.id.comment_text_view)
        val descriptionTextView = convertView!!.findViewById<TextView>(R.id.description_textview)
        val linkTextView = convertView!!.findViewById<TextView>(R.id.link_textview)
        val attachmentTextView = convertView!!.findViewById<TextView>(R.id.attachment_text_view)
        val attachmentImageView = convertView!!.findViewById<ImageView>(R.id.attachment_image_view)

        radioGroupButton.setOnCheckedChangeListener(this)
        radioGroupButton.contentDescription = questionModel.qid.toString()
        radioGroupButton.clearCheck()

        val mAnswer: String? =
            (context as QuestionsActivity).getUserQuestionResponse(questionModel.qid)[0].answer
        if (!mAnswer.isNullOrEmpty()) {
            if (questionModel.ansType?.contentEquals(AppConstants.TYPE_TEXT_FIELD)!!) {
                answerTextView.text = mAnswer
            } else {
                when (mAnswer) {
                    context.getString(R.string.txt_ya) -> radioYa.isChecked = true
                    context.getString(R.string.txt_no) -> radioNo.isChecked = true
                    context.getString(R.string.txt_na) -> radioNa.isChecked = true
                }
            }
        } else if (questionModel.ansType?.contentEquals(AppConstants.TYPE_TEXT_FIELD)!!) {
            answerTextView.text = ""
        }


        val mComment: String? =
            (context as QuestionsActivity).getUserQuestionResponse(questionModel.qid)[0].comment
        if (!mComment.isNullOrEmpty()) {
            commentTextView.text = String.format(context.getString(R.string.txt_comment), mComment)
        } else {
            commentTextView.text = null
        }

        if (questionModel.ansType.equals(AppConstants.TYPE_RADIO_FIELD)) {
            radioGroupButton.visibility = VISIBLE
            answerTextView.visibility = GONE
        } else if (questionModel.ansType.equals(AppConstants.TYPE_TEXT_FIELD)) {
            radioGroupButton.visibility = GONE
            answerTextView.visibility = VISIBLE
        }

        val attachment: ByteArray? =
            (context as QuestionsActivity).getUserQuestionResponse(questionModel.qid)[0].attachment
        if (attachment != null) {
            val bitmap: Bitmap = ImageBitmapUtils.convertStringToBitmap(attachment!!)
            attachmentImageView.setImageBitmap(bitmap)
            attachmentTextView.text = "View Attachment"
        } else {
            attachmentImageView.setImageResource(R.drawable.ic_baseline_attach_file)
            attachmentTextView.text = "Browse Attachment"
        }

        if (!questionModel.description.isNullOrEmpty()) {
            descriptionTextView.visibility = VISIBLE
            descriptionTextView.text = questionModel.description
        } else {
            descriptionTextView.visibility = GONE
        }

        if (!questionModel.link.isNullOrEmpty()) {
            linkTextView.visibility = VISIBLE
            val spannable = SpannableStringBuilder(context.getString(R.string.txt_link))
            spannable.setSpan(
                ForegroundColorSpan(Color.RED),
                12, 22,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            linkTextView.text = spannable
        } else {
            linkTextView.visibility = GONE
        }

        linkTextView.setOnClickListener {
            AppUtils.showWebViewDialog(context, questionModel.link!!)
        }

        answerTextView.setOnClickListener {
            openTextInputDialog(questionModel.qid.toString(), mAnswer, 0)
        }

        commentTextView.setOnClickListener {
            openTextInputDialog(questionModel.qid.toString(), mComment, 1)
        }

        attachmentTextView.setOnClickListener {
            if (questionModel.attachment != null) {
                val intent = Intent(context, ImagePreviewActivity::class.java)
                intent.putExtra(AppConstants.QID, questionModel.qid)
                context.startActivity(intent)
            } else {
                (context as QuestionsActivity).addAttachments(questionModel.qid)
            }
        }
        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return this.listChildData[this.listGroupData.get(listPosition)]!!.size
    }

    override fun getGroup(listPosition: Int): Any {
        return this.listGroupData.get(listPosition)
    }

    override fun getGroupCount(): Int {
        return this.listGroupData.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(
        listPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        var convertView = convertView
        val questionModel = getGroup(listPosition) as Questions
        if (convertView == null) {
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.item_question_group, null)
        }
        val listTitleTextView = convertView!!.findViewById<TextView>(R.id.lblListHeader)
        if (questionModel.viq.isNullOrEmpty()) {
            listTitleTextView.text = questionModel.qtext
        } else {
            listTitleTextView.text = questionModel.qtext + " (VIQ-${questionModel.viq})"
        }
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        val checkedRadioButton = group?.findViewById(group.checkedRadioButtonId) as? RadioButton
        checkedRadioButton?.let {

            if (checkedRadioButton.isChecked)
                (context as QuestionsActivity).updateQuestionResponse(
                    group?.contentDescription.toString(),
                    checkedRadioButton?.text.toString()
                )
        }
    }

    private fun openTextInputDialog(qid: String, content: String?, i: Int) {
        val dialog = BottomSheetDialog(context)
        val sheetView: View =
            AppUtils.getActivity(context)?.layoutInflater
                ?.inflate(R.layout.dialog_text_input_layout, null)!!
        dialog.setContentView(sheetView)

        var mContent: EditText? = dialog.findViewById(R.id.bottom_dialog_input)
        val mButtonYes: Button? = dialog.findViewById(R.id.bottom_yes_button)
        val mButtonNo: Button? = dialog.findViewById(R.id.bottom_no_button)

        mContent?.setText(content)

        mButtonYes?.setOnClickListener {
            when (i) {
                0 -> {
                    (context as QuestionsActivity).updateQuestionResponse(
                        qid,
                        mContent?.text.toString()
                    )
                }
                1 -> {
                    mListener.onCommentPostItemClick(
                        qid, mContent?.text.toString()
                    )
                }
            }
            dialog.dismiss()
        }

        mButtonNo?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}