package com.musicplayer.app.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.musicplayer.app.R
import com.musicplayer.app.databinding.ActivityMainBinding
import com.musicplayer.app.service.MusicService
import com.musicplayer.app.ui.adapter.TrackAdapter
import com.musicplayer.app.ui.viewmodel.MusicViewModel
import com.musicplayer.app.ui.viewmodel.MusicViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MusicViewModel
    private lateinit var adapter: TrackAdapter

    private var musicService: MusicService? = null
    private var isBound = false
    private var pendingTrackUrl: String? = null


    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
            setupServiceObservers()

            pendingTrackUrl?.let { url ->
                musicService?.playTrack(url)
                pendingTrackUrl = null
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()
        setupSortButtons()
        setupPlayerControls()
        observeViewModel()

        viewModel.loadTracks()
    }

    private fun setupViewModel() {
        val factory = MusicViewModelFactory()
        viewModel = ViewModelProvider(this, factory)[MusicViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter { track ->
            viewModel.selectTrack(track)
            playTrack(track.previewUrl)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            visibility = View.VISIBLE
        }
    }

    private fun setupSortButtons() {
        binding.sortByName.setOnClickListener {
            viewModel.sortByName()
        }

        binding.sortByDuration.setOnClickListener {
            viewModel.sortByDuration()
        }
    }

    private fun setupPlayerControls() {
        binding.playPauseButton.setOnClickListener {
            musicService?.togglePlayPause()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.tracks.observe(this) { tracks ->
            Log.d("MainActivity", "Tracks received: ${tracks.size}")
            adapter.submitList(tracks)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            Log.d("MainActivity", "Loading: $isLoading")
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Log.e("MainActivity", "Error: $it")
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.selectedTrack.observe(this) { track ->
            track?.let {
                binding.playerCard.visibility = View.VISIBLE
                binding.trackTitle.text = it.title
                binding.trackArtist.text = it.artist.name
                binding.trackDuration.text = it.getDurationFormatted()
            }
        }
    }

    private fun playTrack(url: String) {
        if (!isBound) {
            pendingTrackUrl = url

            val intent = Intent(this, MusicService::class.java)
            startService(intent)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        } else {
            musicService?.playTrack(url)
        }
    }


    private fun setupServiceObservers() {
        musicService?.let { service ->
            service.isPlaying.observe(this) { isPlaying ->
                binding.playPauseButton.setImageResource(
                    if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                )
            }

            service.currentPosition.observe(this) { position ->
                binding.seekBar.progress = position
                binding.currentTime.text = formatTime(position)
            }

            service.duration.observe(this) { duration ->
                binding.seekBar.max = duration
            }
        }
    }

    private fun formatTime(millis: Int): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", minutes, secs)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}
