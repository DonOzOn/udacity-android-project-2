package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AsteroidDatabase) {

    /**
     * A playlist of videos that can be shown on the screen.
     */
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    val asteroidsToday = Transformations.map(database.asteroidDao.getAsteroidsToday()) {
            it.asDomainModel()
        }

    val asteroidsWeek = Transformations.map(database.asteroidDao.getAsteroidsWeek()) {
            it.asDomainModel()
        }

    val asteroidsSaved = Transformations.map(database.asteroidDao.getAsteroidsSaved()) {
            it.asDomainModel()
        }


    /**
     * Refresh the videos stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     */
    suspend fun refreshAsteroid() {
        withContext(Dispatchers.IO) {
            val value = AsteroidApi.retrofitService.getAsteroid("","")
            val jsonRes = JSONObject(value);
            parseAsteroidsJsonResult(jsonRes).toList().map {
                database.asteroidDao.insertAll( DatabaseAsteroid(
                    id = it.id,
                    codename = it.codename,
                    closeApproachDate = it.closeApproachDate,
                    absoluteMagnitude = it.absoluteMagnitude,
                    estimatedDiameter = it.estimatedDiameter,
                    relativeVelocity = it.relativeVelocity,
                    distanceFromEarth = it.distanceFromEarth,
                    isPotentiallyHazardous = it.isPotentiallyHazardous
                ))
            }
        }
    }
}
