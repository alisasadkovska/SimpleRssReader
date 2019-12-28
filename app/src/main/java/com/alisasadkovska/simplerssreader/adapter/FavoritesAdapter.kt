package com.alisasadkovska.simplerssreader.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alisasadkovska.simplerssreader.R
import com.alisasadkovska.simplerssreader.common.Common.FAVORITE
import com.alisasadkovska.simplerssreader.model.database.Item
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.paperdb.Paper
import kotlinx.android.synthetic.main.news_item.view.*

class FavoritesAdapter(private val favoritesList:MutableList<Item>):RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        return FavoritesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false))
    }

    fun removeItem(position: Int){
        favoritesList.removeAt(position)
        notifyItemRemoved(position)
        Paper.book().write(FAVORITE, favoritesList)
    }

    fun restoreItem(item:Item, position: Int){
        favoritesList.add(position, item)
        notifyItemInserted(position)
        Paper.book().write(FAVORITE, favoritesList)
    }

    override fun getItemCount(): Int {
       return favoritesList.size
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {

        holder.view.favoriteButton.visibility = View.GONE
        holder.view.textTitle.text = favoritesList[position].title
        holder.view.publishedAt.text = favoritesList[position].pubDate

        var categoryString=""
        if (favoritesList[position].categories.size!=0)
        categoryString = favoritesList[position].categories[0]

        holder.view.textCategory.text = categoryString

        val imageUrl = favoritesList[position].enclosure.link
        if (imageUrl!=null)
            Picasso.get().load(imageUrl).into(holder.view.newsCoverImage, object : Callback{
                override fun onSuccess() {
                    holder.view.progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    holder.view.progressBar.visibility = View.GONE
                    Picasso.get().load(R.drawable.ic_terrain_red_24dp).into(holder.view.newsCoverImage)
                }
            })
    }


    lateinit var mClickListener: ClickListener

    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }

    interface ClickListener {
        fun onClick(pos: Int, aView: View)
    }

    inner class FavoritesViewHolder(val view: View):RecyclerView.ViewHolder(view),View.OnClickListener{
        override fun onClick(v: View) {
            mClickListener.onClick(adapterPosition,v)
        }

        init {
            view.setOnClickListener(this)
        }
    }


}