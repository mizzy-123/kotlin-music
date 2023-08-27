package com.tubes.musicappproject

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_setting_aplikasi.*
import java.io.File
import java.net.URI

class SettingAplikasi : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_aplikasi)

        //set editTextTextPersonName agar terisi path
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        val cek = sharedPreference.getString("username","Add Path")
        sharedPreference.getLong("l",1L)

        editTextTextPersonName.setText(cek)

        //handling checked switchSort
        val cek_sort = sharedPreference.getString("sort","ASC")
        sharedPreference.getLong("l",1L)

        if (cek_sort == "DESC"){
            switchSort.isChecked = true
        }else if (cek_sort == "DESC"){
            switchSort.isChecked = false
        }

        btnBackSetting.setOnClickListener {
            finish()
        }
    }

    fun openFolderDialog(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            i.addCategory(Intent.CATEGORY_DEFAULT)
            startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            9999 -> {
                val uri = data!!.data
                val file:File = File(uri!!.path)
                val split: List<String> = file.path.toString().split(":")
                val fillpath = split[1]

                Toast.makeText(this@SettingAplikasi, fillpath, Toast.LENGTH_LONG).show()
                editTextTextPersonName.setText(fillpath)

                //set shared preference setting path
                val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
                var editor = sharedPreference.edit()
                editor.putString("username",fillpath)
                editor.putLong("l",100L)
                editor.commit()

                val cek = sharedPreference.getString("username","defaultName")
                sharedPreference.getLong("l",1L)

                Log.i("test", "Ini fullnya " + file.path.toString())
            }
        }
    }

    fun ResetSettingPath(view: View) {
        //set shared preference setting path
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("username","")
        editor.putLong("l",100L)
        editor.commit()

        editTextTextPersonName.setText("Add Path")
    }



    fun SortSettingPath(view: View) {
        val sort_listen = switchSort.isChecked

        if ( sort_listen == true ){
            //set shared preference setting path
            val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putString("sort", "DESC")
            editor.putLong("l",100L)
            editor.commit()
            Log.i("test_sort", "Ini kondisinya " + sort_listen)

        }else if (sort_listen == false){
            //set shared preference setting path
            val sharedPreference =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putString("sort", "ASC")
            editor.putLong("l",100L)
            editor.commit()
            Log.i("test_sort", "Ini kondisinya " + sort_listen)
        }
    }


}