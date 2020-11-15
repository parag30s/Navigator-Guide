package com.navigatorsguide.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.navigatorsguide.app.R
import com.navigatorsguide.app.database.entities.Questions
import com.navigatorsguide.app.utils.AppConstants
import java.util.*

class QuestionsListAdapter internal constructor(
    private val context: Context,
    private val listGroupData: List<Questions>,
    private val listChildData: HashMap<Questions, List<String>>
) : BaseExpandableListAdapter() {

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
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val expandedListText = getChild(listPosition, expandedListPosition) as String
        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.item_question_child, null)
        }
        var questionModel: Questions  = listGroupData.get(listPosition)
        val radioGroupButton = convertView!!.findViewById<RadioGroup>(R.id.radio_group_button)
        val radioYes = convertView!!.findViewById<RadioButton>(R.id.radio_yes_button)
        val radioNo = convertView!!.findViewById<RadioButton>(R.id.radio_no_button)
        val radioNa = convertView!!.findViewById<RadioButton>(R.id.radio_na_button)
        val descriptionTextView = convertView!!.findViewById<TextView>(R.id.description_textview)

        radioYes.isChecked = false
        radioNo.isChecked = false
        radioNa.isChecked = false

        if(questionModel.ansType.equals(AppConstants.SELECTABLE_CONTENT)){
            radioGroupButton.visibility = VISIBLE
        } else {
            radioGroupButton.visibility = GONE
        }

        if(!questionModel.description.isNullOrEmpty()){
            descriptionTextView.visibility = VISIBLE
            descriptionTextView.setText(questionModel.description)
        } else {
            descriptionTextView.visibility = GONE
        }

        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return this.listChildData.get(this.listGroupData.get(listPosition))!!.size
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
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val questionModel = getGroup(listPosition) as Questions
        if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.item_question_group, null)
        }
        val listTitleTextView = convertView!!.findViewById<TextView>(R.id.lblListHeader)
        listTitleTextView.text = questionModel.qtext
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}