package com.example.uts_lec_map.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.uts_lec_map.R

class BannerPagerAdapter(private val context: Context, private val bannerImages: List<String>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.banner_item, container, false) // Menggunakan banner_item.xml
        val imageView = view.findViewById<ImageView>(R.id.banner_image) // Sesuaikan ID dengan layout baru

        // Menggunakan Glide untuk memuat gambar dari URL Firebase
        Glide.with(context)
            .load(bannerImages[position]) // Load URL dari Firebase
            .into(imageView)

        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return bannerImages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
