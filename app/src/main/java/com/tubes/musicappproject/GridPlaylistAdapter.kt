package com.tubes.musicappproject

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class GridPlaylistAdapter(
    private val context: Context,
    private var playlistList: ArrayList<Playlist>
) :
    RecyclerView.Adapter<GridPlaylistAdapter.GridViewHolder>() {
    companion object{
        var trackList: ArrayList<Playlist> = ArrayList()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GridPlaylistAdapter.GridViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_grid_playlist, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        trackList=playlistList
        holder.title.text = playlistList[position].name
        holder.title.isSelected=true
        holder.title.setOnLongClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle(playlistList[position].name)
            builder.setMessage("Anda Yakin Akan Menghapus Playlist?")
            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                PlaylistActivity.musicPlayList.ref.removeAt(position)
                refreshPlaylist()
                Toast.makeText(context, "Playlist Telah Terhapus", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
            false
        }

       holder.root.setOnClickListener{
           val intent=Intent(context,TrackPlaylistActivity::class.java)
           intent.putExtra("index", position)
           ContextCompat.startActivity(context,intent,null)
       }


    }

    override fun getItemCount(): Int {
        return playlistList.size
    }
    fun setfilter(datasearch: ArrayList<Playlist>){
        this.playlistList = datasearch
        notifyDataSetChanged()
    }

    fun refreshPlaylist() {
        playlistList = ArrayList()
        playlistList.addAll(PlaylistActivity.musicPlayList.ref)
        notifyDataSetChanged()
    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_grid_playlist)
        var title: TextView = itemView.findViewById(R.id.tv_nama_playlist)
        var root:LinearLayout=itemView.findViewById(R.id.ln_root)


    }
}