package com.crazyma.exoplayersample

import android.content.Context
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class ExoplayerManager(val fileManager: VideoCacheManager.FileManager) {

    private val map = LinkedHashMap<String, SimpleExoPlayer>(8, .75f, true)

    /**
     * Get SimpleExoPlayer instance. If the local video file is not exist, return null
     */
    fun getPlayer(context: Context, urlString: String): SimpleExoPlayer? {
        return map[urlString] ?: createPlayer(context, urlString)
    }

    @Synchronized
    private fun createPlayer(context: Context, urlString: String): SimpleExoPlayer? {
        val applicationContext = context.applicationContext

        adjustSize()

        val file = fileManager.getVideoFile(applicationContext, urlString)
        if(!file.exists()){
            return null
        }

        return ExoPlayerFactory.newSimpleInstance(applicationContext).apply {
            playWhenReady = true
            val dataSourceFactory = DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, "hiking-playground") //   TODO: change the user agent
            )
            val videoSource =
                ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(
                        fileManager.parseLocalVideoUri(applicationContext, urlString)
                    )
            prepare(videoSource)

            map[urlString] = this
        }!!
    }

    @Synchronized
    private fun adjustSize() {
        while (map.size >= MAX_COUNT) {
            map.keys.iterator().next().let {
                map.remove(it)?.apply {
                    this.release()
                }
            }
        }
    }

    companion object {
        private val MAX_COUNT = 8

        private var INSTANCE: ExoplayerManager? = null

        @JvmStatic
        fun getInstance(
            fileManager: VideoCacheManager.FileManager
        ): ExoplayerManager = INSTANCE ?: ExoplayerManager(fileManager).apply { INSTANCE = this }
    }

}