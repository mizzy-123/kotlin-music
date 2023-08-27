package com.tubes.musicappproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.activity_selection.*
import kotlinx.android.synthetic.main.activity_track_playlist.*
import kotlinx.android.synthetic.main.activity_track_playlist.iv_back_track_playlist

class SelectionActivity : AppCompatActivity() {
    lateinit var gridTrackAdapter: GridTrackAdapter
    companion object{
        var currentPLposition:Int=-1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)
        rv_grid_track_selection.setItemViewCacheSize(10)
        rv_grid_track_selection.setHasFixedSize(true)
        showRecyclerGrid()
    }
    private fun showRecyclerGrid() {

        rv_grid_track_selection.layoutManager = GridLayoutManager(this@SelectionActivity, 2)

        gridTrackAdapter = GridTrackAdapter(this,TrackActivity.list,SelectionActivity=true)
        rv_grid_track_selection.adapter = gridTrackAdapter
    }
}