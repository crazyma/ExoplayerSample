package com.crazyma.exoplayersample

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context!!)
            adapter = MainAdapter().apply {
                callback = {
                    it?.apply {
                        imageView.setImageBitmap(this)
                    }

                    startActivity(Intent(context!!, SecondActivity::class.java))
                }
            }
        }
    }

}