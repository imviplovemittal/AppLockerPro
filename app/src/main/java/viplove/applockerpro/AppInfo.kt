package viplove.applockerpro

import android.graphics.drawable.Drawable
import java.io.Serializable

class AppInfo: Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }

    var appname = ""
    var pname = ""
    var versionName = ""
    var versionCode = 0
    var icon: Drawable? = null
}