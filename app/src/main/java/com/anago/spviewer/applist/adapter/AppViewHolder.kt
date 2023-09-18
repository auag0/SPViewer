package com.anago.spviewer.applist.adapter

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.anago.spviewer.R
import com.google.android.material.textview.MaterialTextView

class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val icon: ImageView = itemView.findViewById(R.id.icon)
    val name: MaterialTextView = itemView.findViewById(R.id.name)
    val packageName: MaterialTextView = itemView.findViewById(R.id.packageName)
}