package com.crazyma.exoplayersample

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.RuntimeException

class MainAdapter : RecyclerView.Adapter<MainAdapter.CustomViewHolder>() {

    companion object {
        const val TYPE_NORMAL = 1
        const val TYPE_VIDEO = 2
    }

    lateinit var callback: (View, Bitmap, Long) -> Unit
    lateinit var needDownloadCallback: (String) -> Unit

    override fun getItemViewType(position: Int) =
        when (position) {
            4 -> TYPE_VIDEO
            else -> TYPE_NORMAL
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return when (viewType) {
            TYPE_NORMAL -> {
                NormalViewHolder.create(parent)
            }

            TYPE_VIDEO -> {
                VideoViewHolder.create(parent)
            }
            else -> throw RuntimeException("Wrong Item Type")
        }
    }

    override fun getItemCount() = 20

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_VIDEO -> {
                (holder as VideoViewHolder).bind(callback, needDownloadCallback)
            }
        }
    }

    open class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)


}