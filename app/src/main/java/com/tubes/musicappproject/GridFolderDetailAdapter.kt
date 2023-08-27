package com.tubes.musicappproject

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.ArrayList

class GridFolderDetailAdapter internal constructor(private val gridFolderDetailList: ArrayList<MusicTrack>) : RecyclerView.Adapter<GridFolderDetailAdapter.GridViewHolder>() {
    private lateinit var onItemClickCallback: GridFolderDetailAdapter.OnItemClickCallback

    internal fun setOnItemClickCallback(onItemClickCallback: GridFolderDetailAdapter.OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_grid_folder_detail, parent, false)
        return GridViewHolder(view)
    }
    override fun onBindViewHolder(holder: GridFolderDetailAdapter.GridViewHolder, position: Int) {
        val grid = gridFolderDetailList[position]
        Glide.with(holder.itemView.context)
            .load(gridFolderDetailList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.cover_white).fitCenter())
            .into(holder.imgPhoto)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PlayerActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("music", "folder")
            intent.putExtra("item", grid.folderName)
            holder.itemView.context.startActivity(intent)
        }

        holder.title.text = grid.title
        holder.artist.text = grid.artist
    }
    override fun getItemCount(): Int {
        return gridFolderDetailList.size
    }
    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_grid_folder_detail)
        var title: TextView = itemView.findViewById(R.id.tv_folder_detail_lagu)
        var artist: TextView = itemView.findViewById(R.id.tv_folder_detail_artist)

    }
    interface OnItemClickCallback {
        fun onItemClicked(data: MusicTrack)
    }
}