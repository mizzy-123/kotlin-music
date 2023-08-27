package com.tubes.musicappproject

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class GridFolderAdapter internal constructor(private var gridFolderList:ArrayList<Folder>):RecyclerView.Adapter<GridFolderAdapter.GridViewHolder>(){
    private lateinit var onItemClickCallback: GridAlbumAdapter.OnItemClickCallback

    internal fun setOnItemClickCallback(onItemClickCallback: GridAlbumAdapter.OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setfilter(datasearch: ArrayList<Folder>){
        this.gridFolderList = datasearch
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridFolderAdapter.GridViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_grid_folder, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridFolderAdapter.GridViewHolder, position: Int) {
        val grid = gridFolderList[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailFolderActivity::class.java)
            intent.putExtra("item", grid.folderName)
            intent.putExtra("position",position)
            Toast.makeText(holder.itemView.context, "ini folder "+grid.folderName, Toast.LENGTH_SHORT).show()
            holder.itemView.context.startActivity(intent)
        }

        holder.title.text = grid.folderName
    }
    override fun getItemCount(): Int {
        return gridFolderList.size
    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.tv_nama_folder)

    }

    interface OnItemClickCallback {
        fun onItemClicked(data: MusicTrack)
    }

}