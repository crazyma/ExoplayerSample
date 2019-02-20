package com.crazyma.exoplayersample

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

object ExoplayerManager {

    private val MAX_COUNT = 4
    private val map = LinkedHashMap<String, SimpleExoPlayer>(8, .75f, true)

    fun getPlayer(context: Context, urlString: String): SimpleExoPlayer {
        return map[urlString] ?: createPlayer(context, urlString)
    }

    @Synchronized
    private fun createPlayer(context: Context, urlString: String): SimpleExoPlayer {
        val applicationContext = context.applicationContext

        adjustSize()

        return ExoPlayerFactory.newSimpleInstance(applicationContext).apply {
            playWhenReady = true
            val dataSourceFactory = DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, "hiking-playground")
            )
            val videoSource =
                ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(urlString))
            prepare(videoSource)

            map[urlString] = this
        }!!
    }

    @Synchronized
    private fun adjustSize() {
        while (map.size >= 4) {
            map.keys.iterator().next().let {
                map.remove(it)?.apply {
                    this.release()
                }
            }
        }
    }

}