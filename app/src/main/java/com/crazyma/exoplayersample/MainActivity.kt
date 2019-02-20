package com.crazyma.exoplayersample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.view.Window

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
}
