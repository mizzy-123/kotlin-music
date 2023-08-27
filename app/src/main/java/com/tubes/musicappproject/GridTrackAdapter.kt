package com.tubes.musicappproject

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*
import kotlin.collections.ArrayList

class GridTrackAdapter internal constructor(private val context:Context,private var gridTrackList: ArrayList<MusicTrack>,private var playlistDetails: Boolean=false,private val SelectionActivity:Boolean=false) : RecyclerView.Adapter<GridTrackAdapter.GridViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    companion object{
        var trackList: ArrayList<MusicTrack> = ArrayList()
    }

    internal fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_grid_track, parent, false)
        return GridViewHolder(view)
    }
    fun setfilter(datasearch: ArrayList<MusicTrack>){
        this.gridTrackList = datasearch
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val grid = gridTrackList[position]
        trackList = gridTrackList
        Glide.with(holder.itemView.context)
            .load(gridTrackList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.cover_white).fitCenter())
            .into(holder.imgPhoto)

        holder.itemView.setOnClickListener {
//            onItemClickCallback.onItemClicked(gridTrackList[holder.adapterPosition])
            val intent = Intent(holder.itemView.context, PlayerActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("music", "track")
            holder.itemView.context.startActivity(intent)
        }
        holder.title.text = grid.title
        holder.artist.text = grid.artist

        when{
            SelectionActivity ->{
                holder.root.setOnClickListener{
                    if(addSong(gridTrackList[position])) {
                        holder.root.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.teal_200
                            )
                        )

                    }
                    else
                        holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.black))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return gridTrackList.size
    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_grid_track)
        var title: TextView = itemView.findViewById(R.id.tv_track_lagu)
        var artist: TextView = itemView.findViewById(R.id.tv_track_artist)
        var root:LinearLayout=itemView.findViewById(R.id.ln_root_track)

    }

    interface OnItemClickCallback {
        fun onItemClicked(data: MusicTrack)
    }

    private fun addSong(song:MusicTrack):Boolean{
        PlaylistActivity.musicPlayList.ref[TrackPlaylistActivity.currentPLposition].playlist.forEachIndexed{index, musicTrack ->
            if(song.id==musicTrack.id){
                PlaylistActivity.musicPlayList.ref[TrackPlaylistActivity.currentPLposition].playlist.removeAt(index)
                return false
            }
        }
        PlaylistActivity.musicPlayList.ref[TrackPlaylistActivity.currentPLposition].playlist.add(song)
        return true
    }

    fun refreshPlaylist(){
        gridTrackList= ArrayList()
        gridTrackList=PlaylistActivity.musicPlayList.ref[TrackPlaylistActivity.currentPLposition].playlist
        notifyDataSetChanged()
    }
}