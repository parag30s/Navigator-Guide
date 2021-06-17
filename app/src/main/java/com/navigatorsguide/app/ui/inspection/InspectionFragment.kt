package com.navigatorsguide.app.ui.inspection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.navigatorsguide.app.BaseFragment
import com.navigatorsguide.app.R
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.database.entities.ShipType
import com.navigatorsguide.app.managers.PreferenceManager
import com.navigatorsguide.app.model.User
import com.navigatorsguide.app.rx.RxBus
import com.navigatorsguide.app.rx.RxEvent
import com.navigatorsguide.app.utils.AppConstants
import kotlinx.coroutines.launch


class InspectionFragment() : BaseFragment(),
    View.OnClickListener {
    private lateinit var shipTypeEditText: EditText
    private lateinit var shipNameEditText: EditText
    private lateinit var submitButton: Button
    private var user: User? = null
    private lateinit var mShipTypeList: List<ShipType>
    private var mDatabaseReference: DatabaseReference? = null
    private var useAsDialogFragment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_inspection, container, false)
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        if (arguments != null) {
            val appbar: AppBarLayout = root.findViewById(R.id.fragment_appbar) as AppBarLayout
            appbar.visibility = View.VISIBLE
            if (requireActivity().actionBar != null) {
                requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
                requireActivity().actionBar?.setHomeButtonEnabled(true)
            }
            useAsDialogFragment = arguments?.getBoolean("fragmentDialog")!!
        }

        mDatabaseReference =
            FirebaseDatabase.getInstance().getReference(AppConstants.USER_REFERENCE)
        shipTypeEditText = root.findViewById(R.id.shiptype_edittext)
        shipNameEditText = root.findViewById(R.id.shipname_edittext)
        submitButton = root.findViewById(R.id.submit_button)

        shipTypeEditText.setOnClickListener(this)
        shipNameEditText.setOnClickListener {
            shipNameEditText.isCursorVisible = true
        }
        submitButton.setOnClickListener(this)

        init()
        launch {
            context?.let {
                val db: AppDatabase = AppDatabase.invoke(requireActivity())
                mShipTypeList = db.getShipTypeDao().getAllShipType()
            }
        }
        return root
    }

    fun onBackPressed(view: View) {
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun init() {
        user = PreferenceManager.getRegistrationInfo(requireActivity())
        if (user != null) {
            shipTypeEditText.setText(user!!.getShipType())
        }
        shipNameEditText.setText(PreferenceManager.getShipName(requireActivity()))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.shiptype_edittext -> {
                val shipTypeMenu = PopupMenu(requireActivity(), shipTypeEditText)
                for (i in 0 until mShipTypeList.size) {
                    shipTypeMenu.menu.add(i, Menu.FIRST, i, mShipTypeList.get(i).typeName)
                }
                shipTypeMenu.setOnMenuItemClickListener { item ->
                    shipTypeEditText.setText(item.title)
                    PreferenceManager.saveShipTypeInfo(requireActivity(), item.title.toString())
                    mDatabaseReference!!.child(user!!.userId).child(AppConstants.USER_SHIP_TYPE)
                        .setValue(item.title)
                    false
                }
                shipTypeMenu.show()
            }
            R.id.submit_button -> {
                val shipName: String = shipNameEditText.text.toString().trim { it <= ' ' }
                if (shipName.isEmpty()) {
                    shipNameEditText.error = getString(R.string.err_msg_ship_name)
                } else {
                    PreferenceManager.saveShipName(requireActivity(), shipName)
                    shipNameEditText.isCursorVisible = false

                    if (useAsDialogFragment) {
                        RxBus.publish(RxEvent.EventAddShipName(shipName))
                        dismiss()
                    }

                    Toast.makeText(requireActivity(),
                        "Inspection details updated.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}