package viplove.applockerpro

import android.app.Activity
import android.app.ActivityManager
import android.app.ProgressDialog
import android.app.admin.DevicePolicyManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.rvalerio.fgchecker.Utils.hasUsageStatsPermission
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    var progressDialog: ProgressDialog? = null
    var res: ArrayList<AppInfo>? = null
    private var devicePolicyManager: DevicePolicyManager? = null
    private var activityManager: ActivityManager? = null
    private var compName: ComponentName? = null
    private var forgroundToastService: ForegroundToastService? = null
    private var selectedAppList: HashSet<String>? = null
    private var session: Session? = null
    private var systemEnabled: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        initialise()
    }

    private fun initialise() {

        supportActionBar?.title = Html.fromHtml("<font color=\"#4275E3\">" + getString(R.string.app_name) + "</font>")
        session = Session(this)
        selectedAppList = session?.getStringSet(Session.APP_LIST) as HashSet<String>
        forgroundToastService = ForegroundToastService()

        if (!hasUsageStatsPermission(this)) {
            usageAccessSettingsPage()
            Toast.makeText(this, "Enable App Locker Pro", Toast.LENGTH_LONG).show()
        }
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        compName = ComponentName(this, MyAdmin::class.java)
        val active = devicePolicyManager?.isAdminActive(compName)
        if (!active!!) {
            val i = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
            i.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable Device Admin to stop uninstall")
            startActivityForResult(i, 499)
        }


        res = ArrayList<AppInfo>()
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Fetching Data...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()

        doit().execute()
        systemEnabled = false

        start_button.setOnClickListener {
            session?.saveCheckedAppList(AppAdapter.statified.newAppList)
            forgroundToastService?.start(baseContext)
            Toast.makeText(baseContext, "Service Started", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    inner class doitSystem : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {

            try {
                val packageManager = packageManager
                val mainIntent = Intent(Intent.ACTION_MAIN, null)
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                res?.clear()
                val appList = packageManager.queryIntentActivities(mainIntent, 0)
                Collections.sort(appList, ResolveInfo.DisplayNameComparator(packageManager))
                val apps = packageManager.getInstalledPackages(0)
                for (i in apps.indices) {
                    val p = apps[i]

                    val newInfo = AppInfo()
                    newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString()
                    newInfo.pname = p.packageName
                    newInfo.versionName = p.versionName
                    newInfo.versionCode = p.versionCode
                    newInfo.icon = p.applicationInfo.loadIcon(getPackageManager())
                    res?.add(newInfo)
                }
                res?.sortBy { appInfo: AppInfo -> appInfo.appname.toUpperCase() }
                res?.sortBy { appInfo: AppInfo -> !(session?.getStringSet(Session.APP_LIST)?.contains(appInfo.pname))!! }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            try {
                app_recycler.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                app_recycler.setHasFixedSize(true)
                app_recycler.adapter = AppAdapter(this@MainActivity, res!!, selectedAppList!!)
                progressDialog?.dismiss()
            } catch (e: java.lang.Exception) {
                progressDialog?.dismiss()
            }
        }

    }

    inner class doit : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {

            try {
                val packageManager = packageManager
                val mainIntent = Intent(Intent.ACTION_MAIN, null)
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                res?.clear()
                val appList = packageManager.queryIntentActivities(mainIntent, 0)
                Collections.sort(appList, ResolveInfo.DisplayNameComparator(packageManager))
                val apps = packageManager.getInstalledPackages(0)
                for (i in apps.indices) {
                    val p = apps[i]
                    if (p.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) {
                        continue
                    }

                    val newInfo = AppInfo()
                    newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString()
                    newInfo.pname = p.packageName
                    newInfo.versionName = p.versionName
                    newInfo.versionCode = p.versionCode
                    newInfo.icon = p.applicationInfo.loadIcon(getPackageManager())
                    res?.add(newInfo)
                }
                res?.sortBy { appInfo: AppInfo -> appInfo.appname.toUpperCase() }
                res?.sortBy { appInfo: AppInfo -> !(session?.getStringSet(Session.APP_LIST)?.contains(appInfo.pname))!! }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            try {
                app_recycler.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                app_recycler.setHasFixedSize(true)
                app_recycler.adapter = AppAdapter(this@MainActivity, res!!, selectedAppList!!)
                progressDialog?.dismiss()
            } catch (e: java.lang.Exception) {
                progressDialog?.dismiss()
            }
        }

    }

    fun getForegroundApp(): String {
        var currentApp = "NULL"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            val usm = this.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
            if (appList != null && appList.size > 0) {
                val mySortedMap = TreeMap<Long, UsageStats>()
                for (usageStats in appList) {
                    mySortedMap[usageStats.lastTimeUsed] = usageStats
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap[mySortedMap.lastKey()]?.getPackageName()!!
                }
            }
        } else {
            val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasks = am.runningAppProcesses
            currentApp = tasks[0].processName
        }

        return currentApp
    }

    fun showHomeScreen(): Boolean {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(startMain)
        return true
    }

    private fun usageAccessSettingsPage() {
        startActivityForResult(Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS), 500)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_system_apps -> {
                if(!systemEnabled!!) {
                    progressDialog = ProgressDialog(this)
                    progressDialog?.setMessage("Fetching Data...")
                    progressDialog?.setCancelable(false)
                    progressDialog?.show()

                    doitSystem().execute()
                    systemEnabled = true
                }
                else{
                    progressDialog = ProgressDialog(this)
                    progressDialog?.setMessage("Fetching Data...")
                    progressDialog?.setCancelable(false)
                    progressDialog?.show()

                    doit().execute()
                    systemEnabled = false
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        var isActive = devicePolicyManager?.isAdminActive(compName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            499 -> {
                if (requestCode == Activity.RESULT_OK)
                    Toast.makeText(this, "Device Admin Enabled", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this, "Some Error Occured to enable device admin", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
