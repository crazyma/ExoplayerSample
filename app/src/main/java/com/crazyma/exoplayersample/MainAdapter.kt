package com.crazyma.exoplayersample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.RuntimeException

class MainAdapter : RecyclerView.Adapter<MainAdapter.CustomViewHolder>() {

    companion object {
        const val TYPE_NORMAL = 1
        const val TYPE_VIDEO = 2
    }

    override fun getItemViewType(position: Int) =
        when (position) {
            4 -> TYPE_NORMAL
            else -> TYPE_VIDEO
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

    }

    open class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class NormalViewHolder(view: View) : CustomViewHolder(view) {
        companion object {
            fun create(parent: ViewGroup) =
                NormalViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_normal, parent, false)
                )
        }
    }

    class VideoViewHolder(view: View) : CustomViewHolder(view) {
        companion object {
            fun create(parent: ViewGroup) =
                VideoViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_normal, parent, false)
                )
        }
    }
}