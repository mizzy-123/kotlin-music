package com.tubes.musicappproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.activity_track_playlist.*

class TrackPlaylistActivity : AppCompatActivity() {
    lateinit var gridTrackAdapter: GridTrackAdapter
    companion object{
        var currentPLposition:Int=-1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_playlist)
        currentPLposition=intent.extras?.get("index") as Int
        rv_grid_track_playlist.setItemViewCacheSize(10)
        rv_grid_track_playlist.setHasFixedSize(true)
        tv_nama_playlist.text=PlaylistActivity.musicPlayList.ref[currentPLposition].name
//        PlaylistActivity.musicPlayList.ref[currentPLposition].playlist.addAll(TrackActivity.list)
        showRecyclerGrid()
        fab_add_song.setOnClickListener{
            startActivity(Intent(this,SelectionActivity::class.java))
        }

        iv_back_track_playlist.setOnClickListener {
            finish()
        }

        sv_search_track_playlist.clearFocus()

        sv_search_track_playlist.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })
    }
    private fun showRecyclerGrid() {

        rv_grid_track_playlist.layoutManager = GridLayoutManager(this@TrackPlaylistActivity, 2)

        gridTrackAdapter = GridTrackAdapter(this,PlaylistActivity.musicPlayList.ref[currentPLposition].playlist, playlistDetails=true)
        rv_grid_track_playlist.adapter = gridTrackAdapter
    }

    override fun onResume() {
        super.onResume()
        //for storing favourites data using shared preferences
        val editor = getSharedPreferences("PLAYLIST", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlayList)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
    }

    private fun filter(newText: String?) {
        var listM: ArrayList<MusicTrack> = ArrayList()
        for (item in PlaylistActivity.musicPlayList.ref[currentPLposition].playlist) {
            if (item.title.lowercase().contains(newText!!.lowercase())) {
                listM.add(item)
            }
        }
        if (listM.isEmpty()) {
            Toast.makeText(this, "Song not found", Toast.LENGTH_SHORT).show()
        } else {
            gridTrackAdapter.setfilter(listM)
        }
    }
}