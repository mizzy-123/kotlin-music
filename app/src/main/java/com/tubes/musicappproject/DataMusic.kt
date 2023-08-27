package com.tubes.musicappproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File

object DataMusic {


    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllAudioTrack(context: Context, path: String, sort:String): ArrayList<MusicTrack>{
        val tempList = ArrayList<MusicTrack>()
        var selection = MediaStore.Audio.Media.IS_MUSIC +  " != 0"

        if (path !== ""){
            Log.i("bismillah", "Ini pathnya $path")
            selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " +
                    MediaStore.Audio.Media.DATA + " LIKE '/storage/emulated/0/$path%'"
        }

//      Set specific path for loading music
//      val selection = MediaStore.Audio.Media.IS_MUSIC +  " != 0"
//      /storage/emulated/0/$path
//      /storage/emulated/0/WhatsApp/Media/WhatsApp Audio
//      var DOWNLOAD_FILE_DIR = Environment.getExternalStorageDirectory().getPath() + "/MYDIR";
//        Log.i("mediadataw", "coba $DOWNLOAD_FILE_DIR")

        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID,MediaStore.Audio.Media.BUCKET_ID,
            MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)
        val cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            MediaStore.Audio.Media.DATE_ADDED + " $sort", null)
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))?:"Unknown"
                    val idC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))?:"Unknown"
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))?:"Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumidC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val folderidC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.BUCKET_ID)).toString()
                    val folderC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumidC).toString()
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
                    if (file.exists()){
                        tempList.add(trackMusic)
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
        }
        return tempList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAlbumList(context: Context, path: String): ArrayList<MusicTrack>{

        val arrayMusic = getAllAudioTrack(context, "", "")

        val temp = arrayListOf<String>()

        val arrayNew = ArrayList<MusicTrack>()

        for (x in arrayMusic.indices){
            var cek: Boolean = false
            if (arrayNew.isEmpty()){
                arrayNew.add(arrayMusic[x])
                temp.add(arrayMusic[x].album)
            } else {
                for (y in temp.indices){
                    if (arrayMusic[x].album == temp[y]){
                        cek = true
                        break
                    }
                }
                if (!cek){
                    arrayNew.add(arrayMusic[x])
                    temp.add(arrayMusic[x].album)
                }
            }
        }
        return arrayNew
    }
}