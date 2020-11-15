package com.navigatorsguide.app.ui.home

import android.os.Bundle
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.Toast
import com.navigatorsguide.app.BaseActivity
import com.navigatorsguide.app.R
import com.navigatorsguide.app.adapters.QuestionsListAdapter
import com.navigatorsguide.app.adapters.SubSectionAdapter
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.database.entities.Questions
import com.navigatorsguide.app.database.entities.SubSection
import com.navigatorsguide.app.utils.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class QuestionsActivity: BaseActivity(), SubSectionAdapter.OnItemClickListener {
    internal var expandableListView: ExpandableListView? = null
    internal var adapter: ExpandableListAdapter? = null

    lateinit var listOutPackHeader: List<Questions>
    var listOutPackChild: HashMap<Questions, List<String>> = HashMap()

    lateinit var contentList: List<Questions>
    var subsParent: Int? = null
    var sectionName: String? = null
    private var lastExpandedPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        subsParent = intent.getIntExtra(AppConstants.SUBS_PARENT, 0)
        sectionName = intent.getStringExtra(AppConstants.SECTION_NAME)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        getSupportActionBar()?.setTitle(sectionName);

        expandableListView = findViewById(R.id.questions_exp_list)

        launch {
            withContext(Dispatchers.Default) {
                listOutPackHeader = AppDatabase.invoke(context = this@QuestionsActivity)
                    .getQuestionsDao().getSelectedQuestions(subsParent!!)
            }
            setQuestionsListAdapter()
        }
    }

    fun setQuestionsListAdapter(){
        for (i in listOutPackHeader.indices) {
            val childData: MutableList<String> = ArrayList()
            val outcallPackageModel: Questions = listOutPackHeader[i]
            childData.add(outcallPackageModel.qtext)
            listOutPackChild[listOutPackHeader[i]] = childData
        }

        adapter = QuestionsListAdapter(this, listOutPackHeader, listOutPackChild)
        expandableListView!!.setAdapter(adapter)
        expandableListView!!.setOnGroupExpandListener { groupPosition ->
            if (lastExpandedPosition != -1
                && groupPosition != lastExpandedPosition) {
                expandableListView!!.collapseGroup(lastExpandedPosition);
            }
            lastExpandedPosition = groupPosition }
        expandableListView!!.setOnGroupCollapseListener { groupPosition ->  }
        expandableListView!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onItemClick(item: SubSection?) {

    }
}