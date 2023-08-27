package com.tubes.musicappproject

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_track.*
import java.util.Locale.filter

class TrackActivity : AppCompatActivity() {
    private lateinit var rv_track: RecyclerView
    companion object{
         lateinit var list: ArrayList<MusicTrack>
    }


    lateinit var gridTrackAdapter:GridTrackAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        back.setOnClickListener {
            finish()
        }

        rv_track = findViewById(R.id.rv_grid_track)
        rv_track.setHasFixedSize(true)

        //store shared preference setting path
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val cek = sharedPreference.getString("username","defaultName")
        sharedPreference.getLong("l",1L)
        Log.i("implemensetting", "Ini pathnya $cek")

        val sort = sharedPreference.getString("sort","defaultName")
        sharedPreference.getLong("l",1L)
        Log.i("implemensort", "Ini sortnya $sort")

        list = DataMusic.getAllAudioTrack(this@TrackActivity, cek.toString(), sort.toString())
        showRecyclerGrid()
        pencarian.clearFocus()
        pencarian.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false;
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true;
            }
        })

        back.setOnClickListener {
            finish()
        }

    }

    private fun filter(newText: String?) {
        var listM: ArrayList<MusicTrack> = ArrayList()
        for (item in list){
            if (item.title.lowercase().contains(newText!!.lowercase())){
                listM.add(item)
            }
        }
        if (listM.isEmpty()){
            Toast.makeText(this@TrackActivity, "Song not found", Toast.LENGTH_SHORT).show()
        }
        else{
            gridTrackAdapter.setfilter(listM)
        }
    }

    private fun showRecyclerGrid() {
        rv_track.layoutManager = GridLayoutManager(this@TrackActivity, 2)
         gridTrackAdapter = GridTrackAdapter(this,list)
        rv_track.adapter = gridTrackAdapter

//        gridTrackAdapter.setOnItemClickCallback(object : GridTrackAdapter.OnItemClickCallback {
//            override fun onItemClicked(data: MusicTrack) {
//                showSelectedList(data)
//            }
//        })
    }

//    fun showSelectedList(track: MusicTrack){
//        val intent = Intent(this@TrackActivity, PlayerActivity::class.java)
//        startActivity(intent)
//        Toast.makeText(this, "Kamu memilih " + track.title, Toast.LENGTH_LONG).show()
//    }
}