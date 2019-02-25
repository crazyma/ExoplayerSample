package com.crazyma.exoplayersample

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.exoplayer2.SimpleExoPlayer
import java.io.File
import java.io.IOException
import java.util.*

class VideoCacheManager(
    val fileManager: FileManager,
    val exoplayerManager: ExoplayerManager
) {

    companion object {
        const val STATE_NON_EXIST = 1
        const val STATE_DOWNLOADING = 2
        const val STATE_EXIST = 3
        const val STATE_ERROR = 4

        private var INSTANCE: VideoCacheManager? = null

        @JvmStatic
        fun getInstance(
            fileManager: FileManager = FileManager(),
            exoplayerManager: ExoplayerManager = ExoplayerManager.getInstance(fileManager)
        ): VideoCacheManager = INSTANCE ?: VideoCacheManager(fileManager, exoplayerManager).apply { INSTANCE = this }
    }

    private val downloadingMap = HashMap<String, UUID>()

    fun getPlayer(context: Context, urlString: String): VideoPlayerPayload? {
        val currentState = checkState(context, urlString)
        Log.d("badu", "currentState: $currentState")

        var payload: VideoPlayerPayload? = null

        when (currentState) {
            STATE_EXIST -> {
                payload = exoplayerManager.getPlayer(context, urlString)?.let {
                    VideoPlayerPayload(currentState, it)
                } ?: VideoPlayerPayload(STATE_ERROR)
            }
            STATE_DOWNLOADING -> {
                payload = VideoPlayerPayload(currentState)
            }
            STATE_NON_EXIST -> {
                startDownload(urlString)
                payload = VideoPlayerPayload(STATE_DOWNLOADING)
            }
            else -> {
                //  TODO: Error Handling?
            }
        }

        return payload
    }

    private fun startDownload(urlString: String) {
        val data = Data.Builder().apply {
            putString("url", urlString)
            putString("filename", fileManager.parseFilename(urlString))
        }.build()
        val worker = OneTimeWorkRequest.Builder(VideoDownloadWorker::class.java)
            .setInputData(data)
            .build()

        downloadingMap[urlString] = worker.id
        WorkManager.getInstance().enqueue(worker)
    }

    private fun checkState(context: Context, urlString: String): Int {
        val filename = fileManager.parseFilename(urlString)

        if (downloadingMap.keys.contains(filename)) {

            val uuid = downloadingMap[urlString]!!

            val workerState = WorkManager.getInstance().getWorkInfoById(uuid).get().state
            when (workerState) {
                WorkInfo.State.SUCCEEDED -> {
                    return STATE_EXIST
                }
                WorkInfo.State.FAILED -> {
                    return STATE_ERROR
                }
                WorkInfo.State.RUNNING,
                WorkInfo.State.ENQUEUED,
                WorkInfo.State.BLOCKED -> {
                    return STATE_DOWNLOADING
                }
                else -> {
                }
            }
        }

        val file = fileManager.getVideoFile(context, urlString)
        if (file.exists())
            return STATE_EXIST

        return STATE_NON_EXIST
    }

    class FileManager {
        @Throws(IOException::class)
        fun getVideoFile(context: Context, urlString: String): File {
            val directory = getDirectory(context)
            val filename = parseFilename(urlString)

            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw IOException("Failed to create directory.")
                }
            }

            return File(directory, filename)
        }

        fun parseLocalVideoUri(context: Context, urlString: String) =
            getVideoFile(context, urlString).let { Uri.fromFile(it) }

        fun getDirectory(context: Context) = File(context.cacheDir.toString() + "/DcardAdVideo")

        fun parseFilename(urlString: String) = Uri.parse(urlString).path    //  TODO: make sure the url is xxx.mp4
    }

    class VideoPlayerPayload(
        var state: Int = STATE_NON_EXIST,
        var exoplayer: SimpleExoPlayer? = null
    )

}