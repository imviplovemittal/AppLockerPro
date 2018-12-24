package viplove.applockerpro.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull

@Entity(tableName = "app_usage")
data class AppUsage(
    @PrimaryKey
    @NonNull
    var appPackage: String,
    var noUsed: Int? = 0,
    var date: String
)