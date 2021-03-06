package com.crazyma.exoplayersample

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context!!)
            adapter = MainAdapter().apply {
                callback = { view, bitmap, position ->

                    delayJump(view, bitmap, position)


//                    testImageView.setImageBitmap(bitmap)
                }

                needDownloadCallback = { urlString ->
                    val data = Data.Builder().apply {
                        putString("url", urlString)
                        putString("filename", "big_buck_bunny.mp4") //  TODO: need to be replaced
                    }.build()
                    val worker = OneTimeWorkRequest.Builder(VideoDownloadWorker::class.java)
                        .setInputData(data)
                        .build()

                    WorkManager.getInstance().enqueue(worker)
                    VideoCacheManager.getInstance().putUUID(urlString, worker.id)
                    WorkManager.getInstance().getWorkInfoByIdLiveData(worker.id).observe(viewLifecycleOwner, Observer {

                        if(it != null ){

                            when(it.state){
                                WorkInfo.State.SUCCEEDED -> {
                                    Log.d("badu", "observe by download file finished")
                                    val returnUrlString = it.outputData.getString("url")!!
                                    VideoCacheManager.getInstance().removeUUID(returnUrlString)
                                    recyclerView.adapter!!.notifyDataSetChanged()
                                }
                                WorkInfo.State.ENQUEUED,
                                WorkInfo.State.RUNNING,
                                WorkInfo.State.BLOCKED -> {
                                    //  running
                                    Log.i("badu","running")
                                }
                                else -> {

                                }
                            }
                        }
                    })
                }
            }
        }
    }

    private fun delayJump(view: View, bitmap: Bitmap, position: Long) {

        Handler().postDelayed({
            Log.d("badu", "get bitmap count : " + bitmap.byteCount + " | " + bitmap.toString())
            val compressedBitmap = compressBitmap2(bitmap)
            if (compressedBitmap != null) {

                Log.d(
                    "badu",
                    "get compressedBitmap count : " + compressedBitmap.byteCount + " | " + compressedBitmap.toString()
                )
                val options = ActivityOptions.makeSceneTransitionAnimation(activity!!, view, "robot")

                val intent = Intent(context!!, SecondActivity::class.java).apply {
                    putExtra("bitmap", compressedBitmap)
                    putExtra("position", position)
                }
                startActivity(intent, options.toBundle())
            }
        }, 100)


    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap? {
        val outputStream = ByteArrayOutputStream()
        var quality = 100
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        Log.i("badu", "size : ${outputStream.toByteArray().size}")
        while (outputStream.toByteArray().size / 1024 > 100) {
            outputStream.reset()
            quality -= 10
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            Log.i("badu", "size : ${outputStream.toByteArray().size}")
        }

        val inputStream = ByteArrayInputStream(outputStream.toByteArray())
        return BitmapFactory.decodeStream(inputStream, null, null)
    }

    private fun compressBitmap2(bitmap: Bitmap): Bitmap? {
        var newWidth = bitmap.width.toFloat()
        var newHeight = bitmap.height.toFloat()
        var newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth.toInt(), newHeight.toInt(), false)
        while (newBitmap.byteCount > 1024 * 500) {
            newWidth *= 0.8f
            newHeight *= 0.8f
            newBitmap.recycle()
            newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth.toInt(), newHeight.toInt(), false)
        }
        return newBitmap
    }

}