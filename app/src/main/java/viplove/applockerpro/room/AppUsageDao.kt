package viplove.applockerpro.room

import android.arch.persistence.room.*

@Dao
interface AppUsageDao {
    @Insert
    fun insert(vararg appUsage: AppUsage)

    @Update
    fun update(vararg appUsage: AppUsage)

    @Delete
    fun delete(appUsage: AppUsage)

    @Query("SELECT * FROM app_usage")
    fun getAppUsages(): List<AppUsage>

    @Query("SELECT appPackage FROM app_usage WHERE date = :date")
    fun getPackages(date: String): List<String>

    @Query("UPDATE app_usage SET noUsed = noUsed + 1 WHERE date = :date AND appPackage = :appPackage")
    fun updateUsage(date: String, appPackage: String)

    @Query("SELECT DISTINCT date FROM app_usage")
    fun getDates(): List<String>

    @Query("SELECT * FROM app_usage WHERE date = :date")
    fun getAppUsagesByDate(date: String): List<AppUsage>
}