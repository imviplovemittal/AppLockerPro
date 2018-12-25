package viplove.applockerpro.appusage

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_app_usage.*
import viplove.applockerpro.R
import viplove.applockerpro.room.AppDatabase
import viplove.applockerpro.room.AppUsageDao

class AppUsageActivity : AppCompatActivity() {

    private var appUsageDao: AppUsageDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_usage)


        appUsageDao = Room.databaseBuilder(this, AppDatabase::class.java, "db-app-usage")
            .allowMainThreadQueries()
            .build()
            .getAppUsageDao()

        val appDates = appUsageDao?.getDates()

        dates_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        dates_recycler.adapter = AppDateAdapter(this, appDates!!)
        dates_recycler.setHasFixedSize(true)
    }
}
