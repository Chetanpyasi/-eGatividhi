package com.example.loginsignup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class ImageAdapter(private val context: Context, private val imageList: List<ProgressDashboard.ImageData>) : BaseAdapter() {

    override fun getCount(): Int = imageList.size

    override fun getItem(position: Int): Any = imageList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
            holder = ViewHolder(
                view.findViewById(R.id.imgUploaded),
                view.findViewById(R.id.tvDateTime)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val imageData = imageList[position]

        // Correct usage of Picasso to load the image URL into the ImageView
        Picasso.get()
            .load(imageData.imageUrl)  // imageUrl is a String containing the URL
            .placeholder(R.drawable.placeholder)  // Optional: Placeholder image while loading
            .error(R.drawable.error_image)  // Optional: Error image if loading fails
            .into(holder.imgUploaded)  // Load into the ImageView

        holder.tvDateTime.text = imageData.uploadTime  // Set the upload time in the TextView

        return view
    }

    // ViewHolder pattern for better performance
    private class ViewHolder(
        val imgUploaded: ImageView,
        val tvDateTime: TextView
    )
}
