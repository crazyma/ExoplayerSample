package com.crazyma.exoplayersample

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.IOException
import java.net.URL

object VedioCacheManager {


    const val STATE_NON_EXIST = 1
    const val STATE_DOWNLOADING = 2
    const val STATE_EXIST = 3

    private val downloadingSet = HashSet<String>()

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

    fun getDirectory(context: Context) = File(context.cacheDir.toString() + "/DcardAdVideo")

    fun parseFilename(urlString: String) =
        Uri.parse(urlString).let { uri ->
            String.format("%s%s",
                uri.host,
                uri.path.let { it?.substring(0, it.lastIndexOf(".")) ?: "filename" })
        }

    fun startDownload(urlString: String) {
//        downloadingSet.add(urlString)
//        downloadingSet.remove(urlString)
    }

    fun checkState(context: Context, urlString: String): Int {

        val file = getVideoFile(context, urlString)
        if (file.exists())
            return STATE_EXIST

        val filename = parseFilename(urlString)
        if (downloadingSet.contains(filename))
            return STATE_DOWNLOADING

        return STATE_NON_EXIST

    }

}