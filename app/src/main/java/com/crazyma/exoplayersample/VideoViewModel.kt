package com.crazyma.exoplayersample

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.item_video.view.*

class VideoViewHolder(view: View) : MainAdapter.CustomViewHolder(view), Player.EventListener {
    companion object {
        fun create(parent: ViewGroup): VideoViewHolder {
            return VideoViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
            )
        }
    }

    fun bind(callback: (View, Bitmap, Long) -> Unit) {
        itemView.imageView.visibility = View.GONE
        itemView.playerView.apply {
            var simpleExoPlayer: SimpleExoPlayer
            if (tag == null) {
                simpleExoPlayer = ExoplayerManager.getPlayer(
                    context!!,
                    "https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
                player = simpleExoPlayer
                player.addListener(this@VideoViewHolder)
                tag = simpleExoPlayer
            } else {
                simpleExoPlayer = tag as SimpleExoPlayer
                simpleExoPlayer.seekTo(0)
            }

            setOnClickListener {
                val position = simpleExoPlayer.currentPosition
                val textureView = this.videoSurfaceView as TextureView

                Log.i("badu","position : ${position}")
                Log.i("badu","duration : ${simpleExoPlayer.duration}")

                itemView.imageView.apply {
                    visibility = View.VISIBLE
                    val bitmap = textureView.bitmap.run {
                        copy(config, isMutable)
                    }

                    setImageBitmap(bitmap)
                    callback.invoke(this, textureView.bitmap, position)

                }
            }
        }
    }

    private fun loadPlayer(): SimpleExoPlayer {
        val context = itemView.context!!
        val player = ExoPlayerFactory.newSimpleInstance(context).apply {
            playWhenReady = true
            val dataSourceFactory = DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, "hiking-playground")
            )
            val videoSource =
                ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse("https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"))
            prepare(videoSource)
        }!!
        return player
    }

}