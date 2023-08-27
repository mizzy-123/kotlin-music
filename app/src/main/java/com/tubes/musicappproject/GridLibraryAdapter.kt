package com.tubes.musicappproject
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*

class GridLibraryAdapter internal constructor(private val gridLibraryList: ArrayList<GridLibrary>) : RecyclerView.Adapter<GridLibraryAdapter.GridViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    internal fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_grid_library2, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val grid = gridLibraryList[position]
        Glide.with(holder.itemView.context)
            .load(gridLibraryList[position].imageLibrary)
            .apply(RequestOptions().override(400, 400))
            .into(holder.imgPhoto)

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(gridLibraryList[holder.adapterPosition]) }
        holder.textName.text = grid.nameLibrary
    }

    override fun getItemCount(): Int {
        return gridLibraryList.size
    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_grid_library)
        var textName: TextView = itemView.findViewById(R.id.tv_item_grid_library)

    }

    interface OnItemClickCallback {
        fun onItemClicked(data: GridLibrary)
    }
}