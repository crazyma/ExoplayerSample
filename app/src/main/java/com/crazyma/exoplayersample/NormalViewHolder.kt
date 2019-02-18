package com.crazyma.exoplayersample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class NormalViewHolder(view: View) : MainAdapter.CustomViewHolder(view) {
    companion object {
        fun create(parent: ViewGroup) =
            NormalViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_normal, parent, false)
            )
    }
}