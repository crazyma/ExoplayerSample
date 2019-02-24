package com.crazyma.exoplayersample

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.SimpleExoPlayer
import java.io.File
import java.io.IOException
import java.net.URL

class VideoCacheManager(
    val fileManager: FileManager = FileManager(),
    val exoplayerManager: ExoplayerManager = ExoplayerManager.getInstance(fileManager)
) {

    companion object {
        const val STATE_NON_EXIST = 1
        const val STATE_DOWNLOADING = 2
        const val STATE_EXIST = 3
        const val STATE_ERROR = 4
    }


    private val downloadingSet = HashSet<String>()



    fun getPlayer(context: Context, urlString: String): VideoPlayerPayload?{
        val state = checkState(context, urlString)
        val exoplayer: SimpleExoPlayer? = null

        when(state){
            STATE_EXIST -> {
                exoplayerManager.getPlayer(context, urlString)
            }
            STATE_DOWNLOADING -> {

            }
            STATE_NON_EXIST -> {
                startDownload(urlString)
            }
            else -> {

            }
        }

        return null
    }





//    @Throws(IOException::class)
//    fun getVideoFile(context: Context, urlString: String): File {
//        val directory = getDirectory(context)
//        val filename = parseFilename(urlString)
//
//        if (!directory.exists()) {
//            if (!directory.mkdirs()) {
//                throw IOException("Failed to create directory.")
//            }
//        }
//
//        return File(directory, filename)
//    }
//
//    fun getDirectory(context: Context) = File(context.cacheDir.toString() + "/DcardAdVideo")
//
//    fun parseFilename(urlString: String) =
//        Uri.parse(urlString).let { uri ->
//            String.format("%s%s",
//                uri.host,
//                uri.path.let { it?.substring(0, it.lastIndexOf(".")) ?: "filename" })
//        }

    fun startDownload(urlString: String) {
//        downloadingSet.add(urlString)
//        downloadingSet.remove(urlString)
    }

    fun checkState(context: Context, urlString: String): Int {

        val filename = fileManager.parseFilename(urlString)
        if (downloadingSet.contains(filename))
            return STATE_DOWNLOADING

        val file = fileManager.getVideoFile(context, urlString)
        if (file.exists())
            return STATE_EXIST

        return STATE_NON_EXIST

    }

    class FileManager(){
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

        fun parseFilename(urlString: String) =
            Uri.parse(urlString).let { uri ->
                String.format("%s%s",
                    uri.host,
                    uri.path.let { it?.substring(0, it.lastIndexOf(".")) ?: "filename" })
            }
    }

    class VideoPlayerPayload(){
        var state: Int = STATE_NON_EXIST
        var exoplayer: SimpleExoPlayer? = null
    }

}