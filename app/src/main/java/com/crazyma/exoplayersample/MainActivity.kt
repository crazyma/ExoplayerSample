package com.crazyma.exoplayersample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.view.Window
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransition()
        setContentView(R.layout.activity_main)

        if(savedInstanceState == null){
            val fragment = MainFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .commit()
        }

        test()
        saveVideoFile()
    }

    private fun setupTransition(){
        window.apply {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            exitTransition = Fade()
            reenterTransition = Fade()
        }
    }

    private fun test(){
        val map = LinkedHashMap<Int, String>(16, .75f, true)

        map.put(0,"aaa")
        map.put(1,"bbb")
        map.put(2,"ccc")
        map.put(3,"ddd")
        map.put(4,"eee")
        map.put(5,"fff")

        for((key, value) in map){
            Log.d("badu","key: $key, value: $value")
        }
        Log.i("badu","-----")

        map[1]
        map[5]
        map[3]

        for((key, value) in map){
            Log.d("badu","key: $key, value: $value")
        }
        Log.i("badu","-----")

        map[2]
        map[0]

        for((key, value) in map){
            Log.d("badu","key: $key, value: $value")
        }
        Log.i("badu","-----")

        map.keys.iterator().next().let {
            map.remove(it)
        }

        for((key, value) in map){
            Log.d("badu","key: $key, value: $value")
        }
        Log.i("badu","-----")

        map[1]
        map.keys.iterator().next().let {
            map.remove(it)
        }

        for((key, value) in map){
            Log.d("badu","key: $key, value: $value")
        }
        Log.i("badu","-----")
    }

    private fun saveVideoFile(){
        val directory = File(cacheDir.toString() + "/DcardAdVideo")
        if(directory.exists()){
            Log.d("badu","directory is already exist")
        }else{
            if(directory.mkdir()){
                Log.d("badu","directory is created")
            }else{
                Log.w("badu","directory failed to create")
                return
            }
        }

        val file = File(directory,"test.mp4")
        Log.d("badu","file: ${file.absolutePath}")

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
                Log.d("badu","save file done")
            } catch (t: Throwable){
                t.printStackTrace()
            }
        }).start()


    }
}
