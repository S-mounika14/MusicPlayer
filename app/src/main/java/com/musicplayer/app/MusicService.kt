package com.musicplayer.app.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class MusicService : Service() {

    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _currentPosition = MutableLiveData<Int>(0)
    val currentPosition: LiveData<Int> = _currentPosition

    private val _duration = MutableLiveData<Int>(0)
    val duration: LiveData<Int> = _duration

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var updateJob: Job? = null

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    fun playTrack(url: String) {
        try {
            releaseMediaPlayer()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepareAsync()

                setOnPreparedListener {
                    start()
                    _isPlaying.postValue(true)
                    _duration.postValue(duration)
                    startUpdatingProgress()
                }

                setOnCompletionListener {
                    _isPlaying.postValue(false)
                    stopUpdatingProgress()
                }

                setOnErrorListener { _, what, extra ->
                    _isPlaying.postValue(false)
                    stopUpdatingProgress()
                    true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.postValue(false)
                stopUpdatingProgress()
            } else {
                it.start()
                _isPlaying.postValue(true)
                startUpdatingProgress()
            }
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    private fun startUpdatingProgress() {
        updateJob = serviceScope.launch {
            while (isActive && mediaPlayer?.isPlaying == true) {
                _currentPosition.postValue(mediaPlayer?.currentPosition ?: 0)
                delay(100)
            }
        }
    }

    private fun stopUpdatingProgress() {
        updateJob?.cancel()
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        stopUpdatingProgress()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
        serviceScope.cancel()
    }
}