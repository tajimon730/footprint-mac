package com.example.footprint

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults
import kotlinx.android.synthetic.main.gallery_photo.view.*

class MyRecyclerViewAdapter(val results: RealmResults<PhotoInfoModel>): RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {


    inner class  ViewHolder(val v: View): RecyclerView.ViewHolder(v){
        val imageSelectedLocationPhoto: ImageView = v.imageSelectedLocationPhoto

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
     val view = LayoutInflater.from(parent.context).inflate(R.layout.gallery_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selectedPhotoUri = results[position]?.stringContentUri

    }
    override fun getItemCount(): Int {
        return results
            .size
    }

}