package viplove.applockerpro

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_app_card.view.*

class AppAdapter(val context: Context, private val apps: List<AppInfo>) :
    RecyclerView.Adapter<AppAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_app_card, parent, false))

    override fun getItemCount(): Int = apps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(apps[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(app: AppInfo) {
            itemView.app_icon.setImageDrawable(app.icon)
            itemView.app_name.text = app.appname
            itemView.app_package.text = app.pname
            itemView.app_version.text = "Version: %s".format(app.versionName)
        }
    }
}