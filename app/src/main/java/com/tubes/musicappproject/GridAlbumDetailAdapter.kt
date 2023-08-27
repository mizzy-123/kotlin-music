package com.tubes.musicappproject

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*

class GridAlbumDetailAdapter internal constructor(private val gridAlbumDetailList: ArrayList<MusicTrack>) : RecyclerView.Adapter<GridAlbumDetailAdapter.GridViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    internal fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_grid_album_detail, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val grid = gridAlbumDetailList[position]
        Glide.with(holder.itemView.context)
            .load(gridAlbumDetailList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.cover_white).fitCenter())
            .into(holder.imgPhoto)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PlayerActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("music", "album")
            intent.putExtra("item", grid.album)
            holder.itemView.context.startActivity(intent)
        }

        holder.title.text = grid.title
        holder.artist.text = grid.artist
    }

    override fun getItemCount(): Int {
        return gridAlbumDetailList.size
    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_grid_album_detail)
        var title: TextView = itemView.findViewById(R.id.tv_album_detail_lagu)
        var artist: TextView = itemView.findViewById(R.id.tv_album_detail_artist)

    }

    interface OnItemClickCallback {
        fun onItemClicked(data: MusicTrack)
    }
}