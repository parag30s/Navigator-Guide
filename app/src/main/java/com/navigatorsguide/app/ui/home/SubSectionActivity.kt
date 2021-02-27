package com.navigatorsguide.app.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.navigatorsguide.app.BaseActivity
import com.navigatorsguide.app.R
import com.navigatorsguide.app.adapters.SubSectionAdapter
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.database.entities.Section
import com.navigatorsguide.app.database.entities.SubSection
import com.navigatorsguide.app.utils.AppConstants
import com.navigatorsguide.app.utils.SpacesItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubSectionActivity: BaseActivity(), SubSectionAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var subSectionAdapter: SubSectionAdapter
    lateinit var subSectionList: List<SubSection>
    var sectionId: Int? = null
    var sectionName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_section)
        sectionId = intent.getIntExtra(AppConstants.SECTION_ID, 0)
        sectionName = intent.getStringExtra(AppConstants.SECTION_NAME)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        getSupportActionBar()?.setTitle(sectionName);

        recyclerView = findViewById(R.id.sub_section_recyclerview)

        launch {
            withContext(Dispatchers.Default) {
                subSectionList = AppDatabase.invoke(context = this@SubSectionActivity)
                    .getSubSectionDao().getSelectedSubSections(sectionId!!)
            }
            setSubSectionAdapter()
        }
    }

    fun setSubSectionAdapter(){
        subSectionAdapter = SubSectionAdapter(this, subSectionList,this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(SpacesItemDecoration(0));
        recyclerView.adapter = subSectionAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onItemClick(item: SubSection?) {
        val intent = Intent(this, QuestionsActivity::class.java)
        intent.putExtra(AppConstants.SUBS_ID, item?.subsid)
        intent.putExtra(AppConstants.SECTION_NAME, item?.subsname)
        intent.putExtra(AppConstants.SECTION_LINK, item?.subLink)
        intent.putExtra(AppConstants.SECTION_NOTE, item?.note)
        startActivity(intent)
    }
}