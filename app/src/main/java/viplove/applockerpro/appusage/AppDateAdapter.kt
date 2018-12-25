package viplove.applockerpro.appusage

import android.arch.persistence.room.Room
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.layout_app_usage.view.*
import kotlinx.android.synthetic.main.layout_date.view.*
import viplove.applockerpro.R
import viplove.applockerpro.room.AppDatabase
import viplove.applockerpro.room.AppUsage
import java.text.SimpleDateFormat
import java.util.*

class AppDateAdapter(val context: Context, private val dates: List<String>) :
    RecyclerView.Adapter<AppDateAdapter.ViewHolder>() {

    val pm = context.packageManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_date, parent, false))

    override fun getItemCount(): Int = dates.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(dates[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(date: String) {

            var apps = Room.databaseBuilder(context, AppDatabase::class.java, "db-app-usage")
                .allowMainThreadQueries()
                .build()
                .getAppUsageDao().getAppUsagesByDate(date)

            apps = apps.sortedBy { app: AppUsage -> app.noUsed }

            if (date == SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().time)) {
                itemView.date_textview.text = "Today"
                itemView.date_wise_recycler.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
                itemView.date_wise_recycler.setHasFixedSize(false)
                itemView.date_wise_recycler.adapter = AppDateWiseAdapter(context, apps, pm)
                itemView.date_wise_recycler.visibility = View.VISIBLE
            } else
                itemView.date_textview.text = date
            itemView.date_textview.setOnClickListener {
                if (itemView.date_wise_recycler.visibility == View.GONE) {
                    itemView.date_wise_recycler.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
                    itemView.date_wise_recycler.setHasFixedSize(false)
                    itemView.date_wise_recycler.adapter = AppDateWiseAdapter(context, apps, pm)
                    itemView.date_wise_recycler.visibility = View.VISIBLE
                } else {
                    itemView.date_wise_recycler.visibility = View.GONE
                }
            }
        }
    }
}

class AppDateWiseAdapter(val context: Context, private val apps: List<AppUsage>, val pm: PackageManager) :
    RecyclerView.Adapter<AppDateWiseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_app_usage, parent, false))

    override fun getItemCount(): Int = apps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(apps[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(app: AppUsage) {
            try {
                val ai = pm.getApplicationInfo(app.appPackage, 0)
                val name = pm?.getApplicationLabel(ai ?: null).toString()
                val icon = pm.getApplicationIcon(app.appPackage)
                itemView.usage_app_icon.setImageDrawable(icon)
                itemView.usage_app_name.text = name
                itemView.usage_app_used.text = "Times Used: %d".format(app.noUsed)
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}