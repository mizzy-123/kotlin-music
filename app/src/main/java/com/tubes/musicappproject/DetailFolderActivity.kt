package com.tubes.musicappproject

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class DetailFolderActivity : AppCompatActivity() {
    private lateinit var rv_folder_detail: RecyclerView
    private lateinit var gridFolderDetailAdapter: GridFolderDetailAdapter
    companion object{
        lateinit var currentFolderVideos: ArrayList<MusicTrack>
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_folder)

        rv_folder_detail = findViewById(R.id.rv_grid_folder_detail)
        rv_folder_detail.setHasFixedSize(true)
        try {
                val position=intent.getIntExtra("position",0)
                currentFolderVideos = getAllFolder(MainActivity.folderList[position].id)
                showRecyclerGrid()

        }catch (e: Exception){
            Toast.makeText(this, "Error" + e.message, Toast.LENGTH_SHORT).show()
        }
    }
    fun showRecyclerGrid() {
        rv_folder_detail.layoutManager = GridLayoutManager(this@DetailFolderActivity, 2)
        gridFolderDetailAdapter = GridFolderDetailAdapter(currentFolderVideos)
        rv_folder_detail.adapter = gridFolderDetailAdapter
    }
    @SuppressLint("InlinedApi", "Recycle", "Range")
    private fun getAllFolder(folderId: String): ArrayList<MusicTrack>{
        val tempList = ArrayList<MusicTrack>()
        val selection = MediaStore.Audio.Media.BUCKET_ID + " like? "
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.BUCKET_ID,
            MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, arrayOf(folderId),
            MediaStore.Audio.Media.DATE_ADDED + " DESC", null)
        if(cursor != null)
            if(cursor.moveToNext())
                do {
                    val folderC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME))
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)) ?: "Unknown"
                    val idC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)) ?: "Unknown"
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)) ?: "Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumidC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                        .toString()
                    val folderidC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.BUCKET_ID))
                        .toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumidC).toString()

                    try {
                        val trackMusic = MusicTrack(
                            id = idC,
                            title = titleC,
                            album = albumC,
                            artist = artistC,
                            path = pathC,
                            duration = durationC,
                            artUri = artUriC,
                            folderName = folderC
                        )
                        val file = File(trackMusic.path)
                        if (file.exists()) {
                            tempList.add(trackMusic)
                        }

                    }catch (e:Exception){}
                }while (cursor.moveToNext())
        cursor?.close()
        return tempList
    }


}