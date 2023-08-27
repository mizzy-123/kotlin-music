package com.tubes.musicappproject

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import java.io.File

data class MusicTrack(
    val id: Long = 0,
    val title: String,
    val album: String,
    val artist: String,
    val path: String,
    val duration: Long = 0,
    val artUri: String="",
    val folderName: String
)

class Playlist {
    lateinit var name: String
    lateinit var playlist: ArrayList<MusicTrack>
    lateinit var createdBy: String
    lateinit var createdOn: String
}

class MusicPlaylist {
    var ref: ArrayList<Playlist> = ArrayList()
}

data class Folder(val id: String, val folderName: String)

@SuppressLint("InlinedApi", "Recycle", "Range")
fun getAllFolder(context: Context): ArrayList<MusicTrack> {


    //for avoiding duplicate folders
    MainActivity.folderList = ArrayList()

    val tempList = ArrayList<MusicTrack>()
    val tempFolderList = ArrayList<String>()
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
        MediaStore.Audio.Media.BUCKET_DISPLAY_NAME
    )
    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null,
        MediaStore.Audio.Media.DATE_ADDED + " DESC", null
    )
    if (cursor != null)
        if (cursor.moveToNext())
            do {
                //checking null safety with ?: operator


                val folderC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME))
                        ?: "Internal Storage"
                val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                        ?: "Unknown"
                val idC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                        ?: "Unknown"
                val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                        ?: "Unknown"
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

                    //for adding folders
                    if (!tempFolderList.contains(folderC) && !folderC.contains("Internal Storage")) {
                        tempFolderList.add(folderC)
                        MainActivity.folderList.add(Folder(id = folderidC, folderName = folderC))
                    }


                } catch (e: Exception) {
                }
            } while (cursor.moveToNext())
    cursor?.close()
    return tempList
}



