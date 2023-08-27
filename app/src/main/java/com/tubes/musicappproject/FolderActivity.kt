package com.tubes.musicappproject

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_album.*
import kotlinx.android.synthetic.main.activity_folder.*
import java.io.File

class FolderActivity : AppCompatActivity() {
    private lateinit var list: ArrayList<Folder>
    companion object {
        private lateinit var rv_folder: RecyclerView
        private lateinit var gridFolderAdapter: GridFolderAdapter
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)

        rv_folder = findViewById(R.id.rv_grid_folder)
        rv_folder.setHasFixedSize(true)
        list=MainActivity.folderList
        showRecyclerGrid()
        back_folder.setOnClickListener {
            finish()
        }
        pencarian_folder.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
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
        var listM: ArrayList<Folder> = ArrayList()
        for (item in list) {
            if (item.folderName.lowercase().contains(newText!!.lowercase())) {
                listM.add(item)
            }
        }
        if (listM.isEmpty()) {
            Toast.makeText(this@FolderActivity, "Song not found", Toast.LENGTH_SHORT).show()
        } else {
            gridFolderAdapter.setfilter(listM)
        }
    }
    private fun showRecyclerGrid() {
        rv_folder.layoutManager = GridLayoutManager(this@FolderActivity, 2)
        gridFolderAdapter = GridFolderAdapter(MainActivity.folderList)
        rv_folder.adapter = gridFolderAdapter
    }


}