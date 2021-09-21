package com.navigatorsguide.app

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.navigatorsguide.app.database.AppDatabase
import com.navigatorsguide.app.managers.PreferenceManager
import com.navigatorsguide.app.model.User
import com.navigatorsguide.app.rx.RxBus
import com.navigatorsguide.app.rx.RxEvent
import com.navigatorsguide.app.utils.AppConstants
import com.navigatorsguide.app.utils.OptionsBottomSheetFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity(), OptionsBottomSheetFragment.ItemClickListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var mDatabaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_profile,
                R.id.nav_inspection,
                R.id.nav_report,
                R.id.nav_contactus
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val userLayout = navView.getHeaderView(0)
        val userName = userLayout.findViewById(R.id.nav_user_name) as TextView
        val userEmail = userLayout.findViewById<TextView>(R.id.nav_user_email) as TextView
        val user: User? = PreferenceManager.getRegistrationInfo(this)
        if (user?.getUserName() != null
            && user.getEmail() != null
        ) {
            userName.text = user.getUserName()
            userEmail.text = user.getEmail()
        }

        mDatabaseReference =
            FirebaseDatabase.getInstance().getReference(AppConstants.USER_REFERENCE)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("MainActivity", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result?.token
                mDatabaseReference!!.child(user!!.userId).child(AppConstants.USER_TOKEN).setValue(
                    token)
            })

        val intent = intent
        if (intent != null && intent.extras != null) {
            val extras = intent.extras
            val id = extras!!.getString("id")
            val value = extras!!.getString("value")
            val action = extras!!.getString("action")
            Log.d("NotificationRepository", "id $id")
            Log.d("NotificationRepository", "value $value")
            Log.d("NotificationRepository", "action $action")
        }

        launch {
            withContext(Dispatchers.Default) {
                val rank = AppDatabase.invoke(this@MainActivity).getRankDao()
                    .getRank(user?.position!!)
                val ship = AppDatabase.invoke(this@MainActivity).getShipTypeDao()
                    .getShipType(user.shipType)

                PreferenceManager.savePositionName(this@MainActivity, rank.rankName)
                PreferenceManager.saveShipTypeName(this@MainActivity, ship.typeName)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onItemClick(item: String) {
        RxBus.publish(RxEvent.EventProfileImage(item))
    }
}