package com.navigatorsguide.app.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.navigatorsguide.app.BaseFragment
import com.navigatorsguide.app.R
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.database.entities.Rank
import com.navigatorsguide.app.database.entities.ShipType
import com.navigatorsguide.app.managers.PreferenceManager
import com.navigatorsguide.app.model.User
import com.navigatorsguide.app.utils.AppConstants
import com.navigatorsguide.app.utils.AppUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class ProfileFragment : BaseFragment(), View.OnClickListener {
    private val TAG: String = "ProfileFragment"
    lateinit var nameTextView: TextView
    lateinit var emailTextView: TextView
    lateinit var passwordTextView: TextView
    lateinit var positionTextView: TextView
    lateinit var shiptypeTextView: TextView
    lateinit var nameThumbView: TextView
    lateinit var timeTextView: TextView
    lateinit var imageThumbView: CircleImageView
    lateinit var positionEditView: LinearLayout
    lateinit var shiptypeEditView: LinearLayout

    private var user: User? = null
    private lateinit var mRankList: List<Rank>
    private lateinit var mShipTypeList: List<ShipType>
    private var imageUri: Uri? = null
    private var CAMERA_REQUEST = 10001

    private var mDatabaseReference: DatabaseReference? = null
    private var mStorageReference: StorageReference? = null
    private var mProfilePictureReference: StorageReference? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        mDatabaseReference =
            FirebaseDatabase.getInstance().getReference(AppConstants.USER_REFERENCE)
        nameTextView = root.findViewById(R.id.name_text_view);
        emailTextView = root.findViewById(R.id.email_text_view);
        passwordTextView = root.findViewById(R.id.password_text_view);
        positionTextView = root.findViewById(R.id.position_text_view);
        shiptypeTextView = root.findViewById(R.id.ship_type_text_view);
        nameThumbView = root.findViewById(R.id.name_thumb_text_view);
        imageThumbView = root.findViewById(R.id.image_thumb_view);
        timeTextView = root.findViewById(R.id.time_text_view);
        positionEditView = root.findViewById(R.id.edit_position_view);
        shiptypeEditView = root.findViewById(R.id.edit_shiptype_view);

        positionEditView.setOnClickListener(this)
        shiptypeEditView.setOnClickListener(this)
        imageThumbView.setOnClickListener(this)

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
        mStorageReference = FirebaseStorage.getInstance().reference

        user = PreferenceManager.getRegistrationInfo(requireActivity())
        if (user != null) {
            nameTextView.text = user!!.getUserName()
            emailTextView.text = user!!.getEmail()
            passwordTextView.text = user!!.getPassword()
            positionTextView.text = user!!.getPosition()
            shiptypeTextView.text = user!!.getShipType()
            nameThumbView.text = AppUtils.getInitials(user!!.getUserName())
            timeTextView.text = "User since : ${AppUtils.getDate(user!!.createdAt)}"

            Glide.with(requireActivity())
                .load(PreferenceManager.getProfileImage(requireActivity()))
                .into(imageThumbView)

            mProfilePictureReference =
                mStorageReference!!.child("profile_picture/${user!!.createdAt}.jpg")
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
                    mDatabaseReference!!.child(user!!.userId).child(AppConstants.USER_POSITION)
                        .setValue(
                            item.title)
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
                    mDatabaseReference!!.child(user!!.userId).child(AppConstants.USER_SHIP_TYPE)
                        .setValue(
                            item.title)
                    false
                }
                shipTypeMenu.show()
            }
            R.id.image_thumb_view -> {
                val cameraIntent = Intent()
                cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            val bitmap: Bitmap = data?.extras?.get("data") as Bitmap
            imageThumbView.setImageBitmap(bitmap)
            handleUpload(bitmap)
            showFullScreenProgress()
        }
    }

    private fun handleUpload(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val uploadTask: UploadTask? = mProfilePictureReference?.putBytes(byteArray)

        uploadTask?.addOnFailureListener { Log.d(TAG, "handleUpload onFailure") }
            ?.addOnSuccessListener {
                Log.d(TAG, "handleUpload onSuccess")
                mProfilePictureReference?.downloadUrl?.addOnSuccessListener { uri ->
                    Log.d(TAG, "Download Photo successful URL: $uri")
                    if (isAdded) {
                        PreferenceManager.saveProfileImage(requireActivity(), uri.toString())
                        hideFullScreenProgress()
                    }
                }?.addOnFailureListener { e ->
                    Log.e(TAG, "downloadUrl: something went wrong: " + e.message)
                }
            }
    }
}