package com.tubes.musicappproject

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DetailAlbumActivity : AppCompatActivity() {

    private lateinit var rv_album_detail: RecyclerView
    private lateinit var list: ArrayList<MusicTrack>
    private lateinit var gridAlbumDetailAdapter: GridAlbumDetailAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_album)

        rv_album_detail = findViewById(R.id.rv_grid_album_detail)
        rv_album_detail.setHasFixedSize(true)
        try {
            val bundle: Bundle? = intent.extras
            if (bundle != null){
                val item = bundle.getString("item", "error")
                list = getDetailAlbum(this@DetailAlbumActivity, item)
                showRecyclerGrid()
            }


        }catch (e: Exception){
            Toast.makeText(this, "Error" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun showRecyclerGrid() {
        rv_album_detail.layoutManager = GridLayoutManager(this@DetailAlbumActivity, 2)
        gridAlbumDetailAdapter = GridAlbumDetailAdapter(list)
        rv_album_detail.adapter = gridAlbumDetailAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDetailAlbum(context: Context, item: String): ArrayList<MusicTrack>{

        //store shared preference setting path
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val cek = sharedPreference.getString("username","defaultName")
        sharedPreference.getLong("l",1L)
        Log.i("implemensetting", "Ini pathnya $cek")

        val sort = sharedPreference.getString("sort","ASC")
        sharedPreference.getLong("l",1L)
        Log.i("implemensort", "Ini sortnya $sort")

        val arrayAlbum = DataMusic.getAllAudioTrack(context, cek.toString(), sort.toString())
        val newArray: ArrayList<MusicTrack> = ArrayList()

        arrayAlbum.forEach {
            if (it.album.equals(item)){
                newArray.add(it)
            }
        }

        return newArray

    }

}