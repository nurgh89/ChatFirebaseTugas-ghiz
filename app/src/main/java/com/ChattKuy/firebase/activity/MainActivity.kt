package com.ChattKuy.firebase.activity

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ChattKuy.firebase.R
import com.ChattKuy.firebase.data.Tools
import com.ChattKuy.firebase.fragment.ChatsFragment
import com.ChattKuy.firebase.services.NotificationService
import com.ChattKuy.firebase.utilities.CustomToast

class MainActivity : AppCompatActivity() {

    private var toolbar: Toolbar? = null
    lateinit var fab: FloatingActionButton
    internal lateinit var mJobScheduler: JobScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        fab = findViewById(R.id.add) as FloatingActionButton

        prepareActionBar(toolbar)
        initComponent()

        fab.setOnClickListener {
            val i = Intent(this@MainActivity, SelectFriendActivity::class.java)
            startActivity(i)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // for system bar in marshmellow
            Tools.systemBarMarshmellow(this)
            //Create the scheduler
            mJobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val builder = JobInfo.Builder(1, ComponentName(packageName, NotificationService::class.java.getName()))
            builder.setPeriodic(900000)
            //If it needs to continue even after boot, persisted needs to be true
            //builder.setPersisted(true);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            mJobScheduler.schedule(builder.build())
        }
    }

    private fun initComponent() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val ctf = ChatsFragment()
        //icf.setRetainInstance(true);
        fragmentTransaction.add(R.id.main_container, ctf, "Chat History")
        fragmentTransaction.commit()

    }

    private fun prepareActionBar(toolbar: Toolbar?) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(false)
        actionBar.setHomeButtonEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_logout -> {
                val logoutIntent = Intent(this, SplashActivity::class.java)
                logoutIntent.putExtra("mode", "logout")
                startActivity(logoutIntent)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private var exitTime: Long = 0

    fun doExitApp() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            CustomToast(this).showInfo(getString(R.string.press_again_exit_app))
            exitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        doExitApp()
    }
}
