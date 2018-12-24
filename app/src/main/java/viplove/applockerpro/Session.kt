package viplove.applockerpro

import android.content.Context
import android.content.SharedPreferences

class Session(val context: Context) {
    private var editor: SharedPreferences.Editor? = null
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /*fun setFreshInstall() {
        editor = sharedPreferences.edit()
        editor?.putBoolean(IS_FRESH_INSTALL, false)
        editor?.apply()
    }

    fun getFreshInstall(): Boolean = sharedPreferences.getBoolean(IS_FRESH_INSTALL, true)

    fun setIsLogin() {
        editor = sharedPreferences.edit()
        editor?.putBoolean(IS_LOGIN, true)
        editor?.apply()
    }

    fun setIsLogout() {
        editor = sharedPreferences.edit()
        editor?.putBoolean(IS_LOGIN, true)
        editor?.apply()
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }

    fun setIsLoginLogout() {
        editor = sharedPreferences.edit()
        editor?.putBoolean(IS_LOGIN, false)
        editor?.apply()
    }

    fun getIsLogin(): Boolean = sharedPreferences.getBoolean(IS_LOGIN, false)

    private fun clearStorage() {
        val dir = File(Environment.getExternalStorageDirectory().toString() + "MMTP")
        if (dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                File(dir, children[i]).delete()
            }
        }
    }

    private fun clearApplicationData() {
        val cacheDirectory = context.cacheDir
        val applicationDirectory = File(cacheDirectory.parent)
        if (applicationDirectory.exists()) {
            val fileNames = applicationDirectory.list()
            for (fileName in fileNames) {
                if (fileName != "lib") {
                    deleteFile(File(applicationDirectory, fileName))
                }
            }
        }
    }

    private fun deleteFile(file: File?): Boolean {
        var deletedAll = true
        if (file != null) {
            if (file.isDirectory) {
                val children = file.list()
                for (i in children.indices) {
                    deletedAll = deleteFile(File(file, children[i])) && deletedAll
                }
            } else {
                deletedAll = file.delete()
            }
        }
        return deletedAll
    }

    fun registerUser(id: String, userName: String, userImage: String) {
        editor = sharedPreferences.edit()
        editor?.putString(ID, id)
        editor?.putString(USER_NAME, userName)
        editor?.putString(USER_IMAGE, userImage)
        editor?.apply()
    }*/

    fun getCurrent(current: String) {
        editor = sharedPreferences.edit()
        editor?.putString(CURRENT, current)
        editor?.apply()
    }

    fun saveCheckedAppList(appList: Set<String>) {
        editor = sharedPreferences.edit()
        editor?.putStringSet(APP_LIST, appList)
        editor?.apply()
    }

    fun getStringValue(key: String, default: String? = null) = sharedPreferences.getString(key, default)

    fun getStringSet(key: String, default: Set<String>? = HashSet()) =
        sharedPreferences.getStringSet(key, default)

    fun getBooleanValue(key: String, default: Boolean = false) = sharedPreferences.getBoolean(key, default)

    companion object {

        const val VERSION_CODE = "version_code"

        private const val IS_LOGIN = "IsLoggedIn"
        private const val IS_SAVE = "is_save"
        private const val PREF_NAME = "EngagePref"
        private const val IS_FRESH_INSTALL = "is_fresh_install"

        const val CURRENT = "current"
        const val APP_LIST = "app_list"

        const val ID = "id"
        const val USER_NAME = "user_name"
        const val USER_IMAGE = "user_image"

    }


}