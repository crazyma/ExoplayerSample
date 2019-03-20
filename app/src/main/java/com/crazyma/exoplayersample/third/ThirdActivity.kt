package com.crazyma.exoplayersample.third

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.crazyma.exoplayersample.R
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_third.*

class ThirdActivity : AppCompatActivity() {


    var player: SimpleExoPlayer? = null
    var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        setupPlayerView()
    }

    fun buttonClicked(view: View) {
        flag = !flag

        if (flag) {   //  move to 1
            playerView.player = player
            Log.d("badu","move to 1")
        } else {  //  move to 2
            playerView2.player = player
            Log.d("badu","move to 2")
        }
    }

    private fun setupPlayerView() {

        player = ExoPlayerFactory.newSimpleInstance(applicationContext).apply {
            playWhenReady = true
            val dataSourceFactory = DefaultDataSourceFactory(
                this@ThirdActivity,
                Util.getUserAgent(this@ThirdActivity, "hiking-playground") //   TODO: change the user agent
            )
            val videoSource =
                ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(
                        Uri.parse("https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
                    )
            prepare(videoSource)


        }!!

        playerView2.player = player
    }

}