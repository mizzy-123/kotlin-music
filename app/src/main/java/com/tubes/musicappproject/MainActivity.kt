package com.tubes.musicappproject

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_player.*

class MainActivity : AppCompatActivity() {
    private lateinit var rv_library: RecyclerView
    private val list = ArrayList<GridLibrary>()
    private var backPressedTime = 0L
    companion object{
        lateinit var musiclist: ArrayList<MusicTrack>
        lateinit var folderList: ArrayList<Folder>
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_library = findViewById(R.id.rv_grid_library)
        rv_library.setHasFixedSize(true)
        folderList= ArrayList()
        musiclist= getAllFolder(context = applicationContext)
        btnSetting.setOnClickListener {
            val intent:Intent = Intent(this, SettingAplikasi::class.java)
            startActivity(intent)
        }

        list.addAll(dataGridLibrary.dataGrid)

        //store shared preference setting path
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val cek = sharedPreference.getString("username","defaultName")
        sharedPreference.getLong("l",1L)
        Log.i("implemensetting", "Ini pathnya $cek")

        val sort = sharedPreference.getString("sort","ASC")
        sharedPreference.getLong("l",1L)
        Log.i("implemensort", "Ini sortnya $sort")

        TrackActivity.list = DataMusic.getAllAudioTrack(this@MainActivity, cek.toString(), sort.toString())
        showRecyclerGrid()
        requestRuntimePermission()
        if(requestRuntimePermission()){
            //for retrieving favourites data using shared preferences

            val editor = getSharedPreferences("PLAYLIST", MODE_PRIVATE)
            PlaylistActivity.musicPlayList = MusicPlaylist()
            val jsonStringPlaylist = editor.getString("MusicPlaylist", null)
            if(jsonStringPlaylist != null){
                val dataPlaylist: MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist, MusicPlaylist::class.java)
                PlaylistActivity.musicPlayList = dataPlaylist
            }
        }
    }

    private fun requestRuntimePermission():Boolean {
        if (Build.VERSION.SDK_INT >=23){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 121)
                return false
            }

        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 121){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRecyclerGrid() {
        rv_library.layoutManager = GridLayoutManager(this, 2)
        val gridLibraryAdapter = GridLibraryAdapter(list)
        rv_library.adapter = gridLibraryAdapter

        gridLibraryAdapter.setOnItemClickCallback(object : GridLibraryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: GridLibrary) {
                showSelectedList(data)
            }
        })
    }

    private fun showSelectedList(library: GridLibrary) {
        when {
            library.id.equals("1") -> {
                val intent = Intent(this, AlbumActivity::class.java)
                startActivity(intent)
            }
            library.id.equals("2") -> {
                val intent = Intent(this, TrackActivity::class.java)
                startActivity(intent)
            }
            library.id.equals("3") -> {
                val intent = Intent(this, FolderActivity::class.java)
                startActivity(intent)

            }
            library.id.equals("4") -> {
                val intent = Intent(this, PlaylistActivity::class.java)
                startActivity(intent)
            }
        }
        Toast.makeText(this, "Kamu memilih " + library.nameLibrary, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(this,"Tekan kembali untuk keluar", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()

        
    }
}