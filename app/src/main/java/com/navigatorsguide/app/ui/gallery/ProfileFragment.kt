package com.navigatorsguide.app.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.navigatorsguide.app.BaseFragment
import com.navigatorsguide.app.R
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.database.entities.Rank
import com.navigatorsguide.app.database.entities.ShipType
import com.navigatorsguide.app.managers.PreferenceManager
import com.navigatorsguide.app.model.User
import com.navigatorsguide.app.utils.AppConstants
import com.navigatorsguide.app.utils.AppUtils
import kotlinx.coroutines.launch

class ProfileFragment : BaseFragment(), View.OnClickListener {
    lateinit var nameTextView: TextView
    lateinit var emailTextView: TextView
    lateinit var passwordTextView: TextView
    lateinit var positionTextView: TextView
    lateinit var shiptypeTextView: TextView
    lateinit var nameThumbView: TextView
    lateinit var timeTextView: TextView
    lateinit var positionEditView: LinearLayout
    lateinit var shiptypeEditView: LinearLayout

    private var user: User? = null
    private lateinit var mRankList: List<Rank>
    private lateinit var mShipTypeList: List<ShipType>

    private var mDatabaseReference: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        mDatabaseReference =
            FirebaseDatabase.getInstance().getReference(AppConstants.USER_REFERENCE)
        nameTextView = root.findViewById<TextView>(R.id.name_text_view);
        emailTextView = root.findViewById<TextView>(R.id.email_text_view);
        passwordTextView = root.findViewById<TextView>(R.id.password_text_view);
        positionTextView = root.findViewById<TextView>(R.id.position_text_view);
        shiptypeTextView = root.findViewById<TextView>(R.id.ship_type_text_view);
        nameThumbView = root.findViewById<TextView>(R.id.name_thumb_text_view);
        timeTextView = root.findViewById<TextView>(R.id.time_text_view);
        positionEditView = root.findViewById<LinearLayout>(R.id.edit_position_view);
        shiptypeEditView = root.findViewById<LinearLayout>(R.id.edit_shiptype_view);

        positionEditView.setOnClickListener(this)
        shiptypeEditView.setOnClickListener(this)

        init()

        launch {
            context?.let {
                val db: AppDatabase = AppDatabase.invoke(requireActivity())
                mRankList = db.getRankDao().getAllRanks()
                mShipTypeList = db.getShipTypeDao().getAllShipType()
            }
        }
        return root
    }

    private fun init() {
        user = PreferenceManager.getRegistrationInfo(requireActivity())
        if (user != null) {
            nameTextView.text = user!!.getUserName()
            emailTextView.text = user!!.getEmail()
            passwordTextView.text = user!!.getPassword()
            positionTextView.text = user!!.getPosition()
            shiptypeTextView.text = user!!.getShipType()
            nameThumbView.text = AppUtils.getInitials(user!!.getUserName())
            timeTextView.text = "User since : ${AppUtils.getDate(user!!.createdAt)}"
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit_position_view -> {
                val rankMenu = PopupMenu(requireActivity(), positionTextView)
                for (i in 0 until mRankList.size) {
                    rankMenu.menu.add(i, Menu.FIRST, i, mRankList.get(i).rankName)
                }
                rankMenu.setOnMenuItemClickListener { item ->
                    positionTextView.setText(item.title)
                    PreferenceManager.savePositionInfo(requireActivity(), item.title.toString())
                    mDatabaseReference!!.child(user!!.userId).child(AppConstants.USER_POSITION).setValue(item.title)
                    false
                }
                rankMenu.show()
            }
            R.id.edit_shiptype_view -> {
                val shipTypeMenu = PopupMenu(requireActivity(), shiptypeTextView)
                for (i in 0 until mShipTypeList.size) {
                    shipTypeMenu.menu.add(i, Menu.FIRST, i, mShipTypeList.get(i).typeName)
                }
                shipTypeMenu.setOnMenuItemClickListener { item ->
                    shiptypeTextView.setText(item.title)
                    PreferenceManager.saveShipTypeInfo(requireActivity(), item.title.toString())
                    mDatabaseReference!!.child(user!!.userId).child(AppConstants.USER_SHIP_TYPE).setValue(item.title)
                    false
                }
                shipTypeMenu.show()
            }
        }
    }
}