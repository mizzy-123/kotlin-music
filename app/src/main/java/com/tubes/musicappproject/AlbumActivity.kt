package com.tubes.musicappproject

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_album.*
import java.util.Locale.filter

class AlbumActivity : AppCompatActivity() {
    private lateinit var rv_album: RecyclerView
    private lateinit var list: ArrayList<MusicTrack>
    private lateinit var gridAlbumAdapter: GridAlbumAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        rv_album = findViewById(R.id.rv_grid_album)
        rv_album.setHasFixedSize(true)

        //store shared preference setting path
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val cek = sharedPreference.getString("username","defaultName")
        sharedPreference.getLong("l",1L)
        Log.i("implemensetting", "Ini pathnya $cek")

        list = DataMusic.getAlbumList(this@AlbumActivity, cek.toString())
        showRecyclerGrid()

        back_album.setOnClickListener {
            finish()
        }

        pencarian_album.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false;
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return false;
            }
        })
    }

    private fun filter(newText: String?) {
        var listM: ArrayList<MusicTrack> = ArrayList()
        for (item in list) {
            if (item.title.lowercase().contains(newText!!.lowercase())) {
                listM.add(item)
            }
        }
        if (listM.isEmpty()) {
            Toast.makeText(this@AlbumActivity, "Song not found", Toast.LENGTH_SHORT).show()
        } else {
            gridAlbumAdapter.setfilter(listM)
        }
    }
    private fun showRecyclerGrid() {
        rv_album.layoutManager = GridLayoutManager(this@AlbumActivity, 2)
        gridAlbumAdapter = GridAlbumAdapter(list)
        rv_album.adapter = gridAlbumAdapter
    }
}