package com.navigatorsguide.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.navigatorsguide.app.BaseFragment
import com.navigatorsguide.app.R
import com.navigatorsguide.app.adapters.SectionAdapter
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.database.entities.Section
import com.navigatorsguide.app.utils.AppConstants
import com.navigatorsguide.app.utils.AppUtils
import com.navigatorsguide.app.utils.SpacesItemDecoration
import kotlinx.coroutines.launch


class HomeFragment : BaseFragment(), SectionAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sectionAdapter: SectionAdapter
    private lateinit var homeViewModel: HomeViewModel

    lateinit var sectionList: List<Section>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = root.findViewById(R.id.section_recyclerview)

        launch {
            context?.let {
                sectionList = AppDatabase.invoke(requireActivity()).getSectionDao().getAllSections()
                setSectionAdapter();
            }
        }
        return root
    }

    fun setSectionAdapter(){
        sectionAdapter = SectionAdapter(activity, sectionList, AppUtils.getScreenWidth(), this)
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(SpacesItemDecoration(0));
        recyclerView.adapter = sectionAdapter
    }

    override fun onItemClick(item: Section?) {
        activity?.let{
            val intent = Intent (it, SubSectionActivity::class.java)
            intent.putExtra(AppConstants.SECTION_ID, item?.sectionid)
            intent.putExtra(AppConstants.SECTION_NAME, item?.sectionName)
            it.startActivity(intent)
        }
    }

}