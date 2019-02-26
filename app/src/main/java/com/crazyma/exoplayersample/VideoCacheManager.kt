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

    fun putUUID(urlString: String, uuid: UUID){
        Log.d("badu","save uuid in Video Manager | $urlString")
        downloadingMap[urlString] = uuid
    }

    fun getUUID(urlString: String): UUID?{
        return downloadingMap[urlString]
    }

    fun removeUUID(urlString: String){
        Log.d("badu","remove uuid in Video Manager | $urlString")
        downloadingMap.remove(urlString)
    }

    fun getPlayer(context: Context, urlString: String): VideoPlayerPayload {
        var state = checkState2(context, urlString)

        var payload = VideoPlayerPayload(state)

        when (state) {
            STATE_EXIST -> {
                exoplayerManager.getPlayer(context, urlString)?.run {
                    payload.exoplayer = this
                } ?: run {
                    payload = VideoPlayerPayload(STATE_ERROR)
                }
            }
            STATE_DOWNLOADING -> {

            }
            STATE_NON_EXIST -> {

            }
            else -> {
                //  TODO: Error Handling?
            }
        }

        return payload
    }

//    private fun startDownload(urlString: String): UUID {
//        val data = Data.Builder().apply {
//            putString("url", urlString)
//            putString("filename", fileManager.parseFilename(urlString))
//        }.build()
//        val worker = OneTimeWorkRequest.Builder(VideoDownloadWorker::class.java)
//            .setInputData(data)
//            .build()
//
//        downloadingMap[urlString] = worker.id
//        WorkManager.getInstance().enqueue(worker)
//
//        return worker.id
//    }


    private fun checkState2(context: Context, urlString: String): Int{

        if(downloadingMap.containsKey(urlString)){
            return STATE_DOWNLOADING
        }

        val file = fileManager.getVideoFile(context, urlString)
        if (file.exists())
            return STATE_EXIST

        return STATE_NON_EXIST
    }

//    private fun checkState(context: Context, urlString: String): VideoPlayerPayload {
//        val filename = fileManager.parseFilename(urlString) ?: return VideoPlayerPayload(STATE_ERROR)
//
//        if (downloadingMap.keys.contains(filename)) {
//
//            val uuid = downloadingMap[urlString]!!
//
//            val workerState = WorkManager.getInstance().getWorkInfoById(uuid).get().state
//
//            when (workerState) {
//                WorkInfo.State.SUCCEEDED -> {
//                    return VideoPlayerPayload(STATE_EXIST)
//                }
//                WorkInfo.State.FAILED -> {
//                    return VideoPlayerPayload(STATE_ERROR)
//                }
//                WorkInfo.State.RUNNING,
//                WorkInfo.State.ENQUEUED,
//                WorkInfo.State.BLOCKED -> {
//                    return VideoPlayerPayload(STATE_DOWNLOADING, uuid)
//                }
//                else -> { }
//            }
//        }
//
//        val file = fileManager.getVideoFile(context, urlString)
//        if (file.exists())
//            return VideoPlayerPayload(STATE_EXIST)
//
//        return VideoPlayerPayload(STATE_NON_EXIST)
//    }

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

    class VideoPlayerPayload(var state: Int = STATE_NON_EXIST){
        var exoplayer: SimpleExoPlayer? = null
    }

}