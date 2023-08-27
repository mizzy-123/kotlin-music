package com.tubes.musicappproject

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.activity_playlist.rv_grid_track
import kotlinx.android.synthetic.main.activity_track.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlaylistActivity : AppCompatActivity() {
    lateinit var gridPlaylistAdapter: GridPlaylistAdapter

    companion object {
        var musicPlayList: MusicPlaylist = MusicPlaylist()
        lateinit var list: ArrayList<Playlist>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        showRecyclerGrid()

        fab_add.setOnClickListener {
            customDialog()
        }
        list = ArrayList()
        list.addAll(PlaylistActivity.musicPlayList.ref)
        sv_search.clearFocus()
        sv_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })
        iv_back.setOnClickListener {
            finish()
        }
    }

    private fun showRecyclerGrid() {
        rv_grid_track.layoutManager = GridLayoutManager(this@PlaylistActivity, 2)
        gridPlaylistAdapter = GridPlaylistAdapter(this, playlistList = musicPlayList.ref)
        rv_grid_track.adapter = gridPlaylistAdapter
    }

    private fun customDialog() {
        var dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_playlist_dialog)
        dialog.show()
        val batal = dialog.findViewById<TextView>(R.id.btn_batal)
        val ok = dialog.findViewById<TextView>(R.id.btn_ok)
        val pl_name = dialog.findViewById<EditText>(R.id.et_name_pl).text
        val uname = dialog.findViewById<EditText>(R.id.et_uname_pl).text
        ok.setOnClickListener {
            if (pl_name != null && uname != null) {
                addPlaylist(pl_name.toString(), uname.toString())
                dialog.dismiss()
            }

        }
        batal.setOnClickListener {
            dialog.dismiss()
        }
        //val customDialog: View =LayoutInflater.from(this@PlaylistActivity).inflate(R.layout.add_playlist_dialog,, false)
    }

    private fun addPlaylist(name: String, uname: String) {
        var playlistExists = false
        for (i in musicPlayList.ref) {
            if (name.equals(i.name)) {
                playlistExists = true
                break
            }
        }
        if (playlistExists) Toast.makeText(this, "Playlist Sudah Ada", Toast.LENGTH_SHORT).show()
        else {
            val tempPL = Playlist()
            tempPL.name = name
            tempPL.createdBy = uname
            tempPL.playlist = ArrayList()
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MM yyyy", Locale.ENGLISH)
            tempPL.createdOn = sdf.format(calendar)
            musicPlayList.ref.add(tempPL)

            gridPlaylistAdapter.refreshPlaylist()
        }
    }

    private fun filter(newText: String?) {
        var listM: ArrayList<Playlist> = ArrayList()
        for (item in list) {
            if (item.name.lowercase().contains(newText!!.lowercase())) {
                listM.add(item)
            }
        }
        if (listM.isEmpty()) {
            Toast.makeText(this, "Song not found", Toast.LENGTH_SHORT).show()
        } else {
            gridPlaylistAdapter.setfilter(listM)
        }
    }


}