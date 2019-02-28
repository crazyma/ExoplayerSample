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
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import com.crazyma.exoplayersample.VideoCacheManager.Companion.STATE_DOWNLOADING
import com.crazyma.exoplayersample.VideoCacheManager.Companion.STATE_ERROR
import com.crazyma.exoplayersample.VideoCacheManager.Companion.STATE_EXIST
import com.crazyma.exoplayersample.VideoCacheManager.Companion.STATE_NON_EXIST
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.fragment_second.*

class SecondFragment : Fragment() {

    var bitmap: Bitmap? = null
    var position: Long = 0
    lateinit var future: ListenableFuture<List<WorkInfo>>

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

        button.setOnClickListener {
            future = WorkManager.getInstance().getWorkInfosByTag("XD")
            val list = future.get()
            list.forEach {
                when (it.state) {
                    WorkInfo.State.ENQUEUED -> {
                        Log.i("badu", "ENQUEUED")
                    }
                    WorkInfo.State.CANCELLED -> {
                        Log.i("badu", "CANCELLED")
                    }
                    WorkInfo.State.RUNNING -> {
                        Log.i("badu", "RUNNING")
                    }
                    WorkInfo.State.BLOCKED -> {
                        Log.i("badu", "BLOCKED")
                    }
                    WorkInfo.State.FAILED -> {
                        Log.i("badu", "FAILED")
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        Log.i("badu", "SUCCEEDED")
                    }
                }
            }
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
        loadPlayer2()
    }

    private fun loadPlayer2() {
        val urlString = "https://megapx-stage-assets.dcard.io/videos/b33a880e-131b-4597-afe8-7210348aa954/orig"
        val videoCacheManager = VideoCacheManager.getInstance()
        val payload = videoCacheManager.getPlayer(context!!, urlString)
        when (payload.state) {
            STATE_EXIST -> {
                Log.d("badu", "Second Fragment | EXIST")
                playerView.player = payload.exoplayer!!
            }
            STATE_DOWNLOADING -> {
                Log.d("badu", "Second Fragment | DOWNLOADING")
                videoCacheManager.getUUID(urlString)?.apply {
                    WorkManager.getInstance().getWorkInfoByIdLiveData(this).observe(viewLifecycleOwner, Observer {
                        if (it != null) {
                            when (it.state) {
                                WorkInfo.State.SUCCEEDED -> {
                                    Log.d("badu", "observe by download file finished in Second Page")
                                    videoCacheManager.removeUUID(it.outputData.getString("url")!!)
                                    loadPlayer2()
                                }
                                else -> {}
                            }
                        }
                    })
                }
            }
            STATE_NON_EXIST -> {
                Log.d("badu", "Second Fragment | NON EXIST")
                val data = Data.Builder().apply {
                    putString("url", urlString)
                    putString("filename", "megapx.mp4") //  TODO: need to be replaced
                }.build()
                val worker = OneTimeWorkRequest.Builder(VideoDownloadWorker::class.java)
                    .setInputData(data)
                    .build()

                WorkManager.getInstance().enqueue(worker)
                WorkManager.getInstance().getWorkInfoByIdLiveData(worker.id).observe(viewLifecycleOwner, Observer {
                    if (it != null) {
                        when (it.state) {
                            WorkInfo.State.SUCCEEDED -> {
                                Log.d("badu", "observe by download file finished in Second Page")
                                videoCacheManager.removeUUID(it.outputData.getString("url")!!)
                                loadPlayer2()
                            }
                            else -> {}
                        }
                    }
                })
            }
            STATE_ERROR -> {

            }
        }
    }

    private fun delayPlay() {
        playerView.player.apply {
            Log.d("badu", "duration : ${duration}")
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

    private fun delayHide() {
        Handler().postDelayed({
            imageView.visibility = View.INVISIBLE
        }, 500)
    }

}