package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    @Query("select * from databaseasteroid Where DATE(closeApproachDate) >= DATE('now') ORDER BY closeApproachDate DESC")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseasteroid Where DATE(closeApproachDate) = DATE('now') ORDER BY closeApproachDate DESC")
    fun getAsteroidsToday(): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseasteroid Where strftime('%W', closeApproachDate) == strftime('%W', 'now') ORDER BY closeApproachDate DESC")
    fun getAsteroidsWeek(): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseasteroid  ORDER BY closeApproachDate DESC")
    fun getAsteroidsSaved(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: DatabaseAsteroid)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AsteroidDatabase::class.java,
                "asteroid").build()
        }
    }
    return INSTANCE
}