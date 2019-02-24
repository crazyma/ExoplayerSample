package com.crazyma.exoplayersample

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.item_video.*

class SecondFragment : Fragment() {

    var bitmap: Bitmap? = null
    var position: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            bitmap = getParcelable<Bitmap>("bitmap")
            position = getLong("position", 0)
            Log.d("badu", "bitmap : $bitmap")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bitmap?.let {
            imageView.setImageBitmap(it)
        }

//        val simpleExoPlayer = ExoplayerManager.getPlayer(
//            context!!,
//            "https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
//
//        playerView.player = simpleExoPlayer
//        Log.d("badu","duration : ${simpleExoPlayer.duration}")
//
//        delayPlay()
//        delayHide()
    }

    private fun delayPlay(){
        playerView.player.apply {
            Log.d("badu","duration : ${duration}")
            seekTo(position)
            playWhenReady = true
        }

    }

    private fun loadPlayer(): SimpleExoPlayer {
        val player = ExoPlayerFactory.newSimpleInstance(context).apply {
            playWhenReady = false
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

    private fun delayHide(){
        Handler().postDelayed({
            imageView.visibility = View.INVISIBLE
        },500)
    }

}