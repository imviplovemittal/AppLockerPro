package viplove.applockerpro.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase


@Database(entities = arrayOf(AppUsage::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAppUsageDao() : AppUsageDao
}