package com.musicplayer.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.musicplayer.app.R
import com.musicplayer.app.data.model.Track
import com.musicplayer.app.databinding.ItemTrackBinding

class TrackAdapter(
    private val onTrackClick: (Track) -> Unit
) : ListAdapter<Track, TrackAdapter.TrackViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TrackViewHolder(
        private val binding: ItemTrackBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            binding.apply {
                trackTitle.text = track.title
                trackArtist.text = track.artist.name
                trackDuration.text = track.getDurationFormatted()

                val imageUrl = track.album?.coverMedium
                    ?: track.album?.cover
                    ?: track.artist.pictureMedium
                    ?: track.artist.picture
                    ?: ""

                if (imageUrl.isNotEmpty()) {
                    albumArt.load(imageUrl) {
                        crossfade(true)
                        placeholder(R.drawable.ic_music_note)
                        error(R.drawable.ic_music_note)
                    }
                } else {
                    albumArt.setImageResource(R.drawable.ic_music_note)
                }

                root.setOnClickListener {
                    onTrackClick(track)
                }
            }
        }
    }

    private class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }
    }
}