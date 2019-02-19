package com.crazyma.exoplayersample

import android.os.Bundle
import android.os.Parcelable
import android.transition.Fade
import android.view.Window
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransition()
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = SecondFragment().apply {
                Bundle().apply {
                    putParcelable(
                        "bitmap",
                        intent.getParcelableExtra<Parcelable>("bitmap")
                    )

                    putLong("position", intent.getLongExtra("position", 0))
                }.also {
                    arguments = it
                }

            }
            supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .commit()
        }
    }

    private fun setupTransition() {
        window.apply {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Fade()
            exitTransition = Fade()
        }
    }
}