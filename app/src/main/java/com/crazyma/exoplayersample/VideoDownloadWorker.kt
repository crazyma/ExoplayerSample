package com.crazyma.exoplayersample

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import okhttp3.OkHttpClient
import okhttp3.Request


class VideoDownloadWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {

//        saveVideoFile()
        saveViaOKHttp()
        return Result.success()
    }

    private fun saveViaOKHttp(){
        val client = OkHttpClient()
        val request = Request.Builder().url("https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
            .build()
        val response = client.newCall(request).execute()

        response.body()?.run{

            val directory = File(applicationContext.cacheDir.toString() + "/DcardAdVideo")
            if (directory.exists()) {
                Log.d("badu", "directory is already exist")
            } else {
                if (directory.mkdir()) {
                    Log.d("badu", "directory is created")
                } else {
                    Log.w("badu", "directory failed to create")
                    return
                }
            }

            val file = File(directory, "test.mp4")

            var read : Int
            val inputStream = byteStream()
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(4)

            inputStream.use {input ->
                outputStream.use {output ->
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                }
            }
            Log.d("badu", "save file done")
        }
    }

    //  25 ~ 30 sec, 加上 buffer(ByteArray) 就可以加快
    private fun saveVideoFile() {

        val directory = File(applicationContext.cacheDir.toString() + "/DcardAdVideo")
        if (directory.exists()) {
            Log.d("badu", "directory is already exist")
        } else {
            if (directory.mkdir()) {
                Log.d("badu", "directory is created")
            } else {
                Log.w("badu", "directory failed to create")
                return
            }
        }

        val file = File(directory, "test.mp4")
        Log.d("badu", "file: ${file.absolutePath}")


        try {
            val inputStream = URL("https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4").run {
                openStream()
            }

            var read = -1

            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            inputStream.use { input ->
                outputStream.use {
                    while (input.read().also { read = it } != -1) {
                        it.write(read)
                    }
                }
            }
            Log.d("badu", "save file done")
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

}