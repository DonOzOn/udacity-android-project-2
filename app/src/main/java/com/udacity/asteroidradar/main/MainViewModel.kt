package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid

import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch


enum class AsteroidApiStatus { LOADING, ERROR, DONE }
enum class PicOfDayApiStatus { LOADING, ERROR, DONE }
class MainViewModel (application: Application) : AndroidViewModel(application)  {
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<AsteroidApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    // The internal MutableLiveData that stores the status of the most recent request
    private val _statusImgOfDay = MutableLiveData<PicOfDayApiStatus>()

    // The external immutable LiveData for the request status
    val statusImgOfDay: LiveData<PicOfDayApiStatus>
        get() = _statusImgOfDay

    private val _asteroidList = MutableLiveData<List<Asteroid>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList
    // Internally, we use a MutableLiveData to handle navigation to the selected property
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    private val _picOfDay = MutableLiveData<PictureOfDay>()
    // The external LiveData interface to the property is immutable, so only this class can modify
    val picOfDay: LiveData<PictureOfDay>
        get() = _picOfDay

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    val listAsteroid = asteroidRepository.asteroids

    val listAsteroidToday = asteroidRepository.asteroidsToday

    val listAsteroidWeek = asteroidRepository.asteroidsWeek

    val listAsteroidSave = asteroidRepository.asteroidsSaved

    init {
        getAsteroidList()
        getPicOfDay()


}

    fun saveDatatoList(list: List<Asteroid>){
        _asteroidList.value = list
    }

    private fun getAsteroidList(){

        viewModelScope.launch {
            _status.value = AsteroidApiStatus.LOADING
            try {
                asteroidRepository.refreshAsteroid()
                _status.value = AsteroidApiStatus.DONE
      } catch (e: Exception) {
                _status.value = AsteroidApiStatus.ERROR
            }
        }

    }


    private fun getPicOfDay(){
        viewModelScope.launch {
            _statusImgOfDay.value = PicOfDayApiStatus.LOADING
            try {
                val value = AsteroidApi.retrofitService.getImgOfTheDay().await()
                _statusImgOfDay.value = PicOfDayApiStatus.DONE
                _picOfDay.value = value;
            } catch (e: Exception) {
                _statusImgOfDay.value = PicOfDayApiStatus.ERROR
            }
        }
    }

    fun displayPropertyDetails(marsProperty: Asteroid) {
        _navigateToSelectedAsteroid.value = marsProperty
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }


}