package com.example.flickerbasic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickerbasic.R
import com.example.flickerbasic.model.Photo

class InterestingPhotoAdapter(var mContext:Context, var alPhoto:ArrayList<Photo>) : RecyclerView.Adapter<InterestingPhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_photo,parent,false)
        return PhotoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return alPhoto.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {

        var farm=alPhoto[position].farm
        var server=alPhoto[position].server
        var id=alPhoto[position].id
        var secret=alPhoto[position].secret
        var downloadUrl="https://farm$farm.static.flickr.com/$server/${id}_$secret.jpg"
        var thumnailUrl="https://farm$farm.static.flickr.com/$server/${id}_${secret}_t_d.jpg"
        Glide
            .with(mContext)
            .load(thumnailUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_place_holder)
            .into(holder.ivPhoto)

    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: AppCompatImageView =itemView.findViewById(R.id.ivPhoto)
        val llParent: LinearLayoutCompat =itemView.findViewById(R.id.llParent)
    }
}