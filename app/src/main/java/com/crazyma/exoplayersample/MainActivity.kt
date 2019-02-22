package com.crazyma.exoplayersample

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.view.Window
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Future

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransition()
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = MainFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .commit()
        }

        saveVideoViaWorker()
//        saveVideoFile()
    }

    private fun setupTransition() {
        window.apply {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            exitTransition = Fade()
            reenterTransition = Fade()
        }
    }

    private fun test() {
        val map = LinkedHashMap<Int, String>(16, .75f, true)

        map.put(0, "aaa")
        map.put(1, "bbb")
        map.put(2, "ccc")
        map.put(3, "ddd")
        map.put(4, "eee")
        map.put(5, "fff")

        for ((key, value) in map) {
            Log.d("badu", "key: $key, value: $value")
        }
        Log.i("badu", "-----")

        map[1]
        map[5]
        map[3]

        for ((key, value) in map) {
            Log.d("badu", "key: $key, value: $value")
        }
        Log.i("badu", "-----")

        map[2]
        map[0]

        for ((key, value) in map) {
            Log.d("badu", "key: $key, value: $value")
        }
        Log.i("badu", "-----")

        map.keys.iterator().next().let {
            map.remove(it)
        }

        for ((key, value) in map) {
            Log.d("badu", "key: $key, value: $value")
        }
        Log.i("badu", "-----")

        map[1]
        map.keys.iterator().next().let {
            map.remove(it)
        }

        for ((key, value) in map) {
            Log.d("badu", "key: $key, value: $value")
        }
        Log.i("badu", "-----")
    }

    private fun saveVideoViaWorker() {
        Log.d("badu", "!!!!")
        val worker =
            OneTimeWorkRequest.Builder(VideoDownloadWorker::class.java)
                .addTag("XD")
                .build()

        WorkManager.getInstance().enqueue(worker)
//        WorkManager.getInstance().getWorkInfosByTag("XD")

        WorkManager.getInstance().getWorkInfoByIdLiveData(worker.id).observe(this, Observer {

            if (it != null) {
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
        })
    }

    private fun saveVideoFile() {
        val directory = File(cacheDir.toString() + "/DcardAdVideo")
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

        Thread(Runnable {
            try {
                val inputStream = URL("https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4").run {
                    openStream()
                }

                var read = -1

                val outputStream = FileOutputStream(file)
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
        }).start()


        val uri = Uri.parse("https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
        Log.i("badu", "host: ${uri.host}")
        Log.i("badu", "scheme: ${uri.scheme}")
        Log.i("badu", "path: ${uri.path}")
        Log.i("badu", "authority: ${uri.authority}")
        Log.i("badu", "lastPathSegment: ${uri.lastPathSegment}")
        Log.i("badu", "pathSegments: ${uri.pathSegments}")
        Log.i("badu", "schemeSpecificPart: ${uri.schemeSpecificPart}")


        String.format("%s%s",
            uri.host,
            uri.path.let { it?.substring(0, it.lastIndexOf(".")) ?: "" }).also {
            Log.d("badu", "test: $it")
        }
    }
}
