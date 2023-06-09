package com.example.raul_lino_d.ui

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.example.raul_lino_d.R

class ViewPageAdapter(private val imageList: List<Bitmap>) : PagerAdapter() {

    override fun getCount(): Int {
        return imageList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            LayoutInflater.from(container.context).inflate(R.layout.img, container, false)
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        val imageBitmap = imageList[position]
        imageView.setImageBitmap(imageBitmap)
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
