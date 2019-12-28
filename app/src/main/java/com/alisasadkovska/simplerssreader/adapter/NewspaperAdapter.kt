package com.alisasadkovska.simplerssreader.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.alisasadkovska.simplerssreader.R
import com.alisasadkovska.simplerssreader.common.Common
import com.alisasadkovska.simplerssreader.model.database.database.DataModel
import com.alisasadkovska.simplerssreader.ui.SourceActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.newspaper_grid_item.view.*


class NewspaperAdapter(private val context :Context, var covers: MutableList<DataModel>):RecyclerView.Adapter<NewspaperAdapter.NewspaperViewHolder>(), Filterable {

    private var coversFiltered:MutableList<DataModel> = ArrayList()

    init {
        coversFiltered = covers
    }


   inner class NewspaperViewHolder(val view:View):RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewspaperViewHolder {
        return NewspaperViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.newspaper_grid_item, parent, false))
    }

    override fun getItemCount(): Int {
       return coversFiltered.size
    }

    override fun onBindViewHolder(holder: NewspaperViewHolder, position: Int){
        val newspaper = coversFiltered[position]
        holder.view.txtNewspaper.text = newspaper.name
        Picasso.get().load(newspaper.smallIcon).into(holder.view.newspaperLogo, object : Callback{
            override fun onSuccess() {
            }

            override fun onError(e: Exception?) {
                Picasso.get().load(R.drawable.ic_terrain_red_24dp).into(holder.view.newspaperLogo)
            }

        })

        holder.view.setOnClickListener {
            val intent = Intent(context, SourceActivity::class.java)
            intent.putExtra("rss", coversFiltered[position].rss)
            intent.putExtra("name", coversFiltered[position].name)
            Common.coverPath = coversFiltered[position].image.toString()
            context.startActivity(intent)
        }

    }

    override fun getFilter(): Filter {
        return object : Filter(){
            @SuppressLint("DefaultLocale")
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint.toString()
                var filteredList:MutableList<DataModel> = ArrayList()

                if (query.isEmpty())
                    filteredList = covers
                else{
                    for (data in covers)
                        if (data.name!!.toLowerCase().contains(query.toLowerCase()))
                            filteredList.add(data)
                }

                val result = FilterResults()
                result.count = filteredList.size
                result.values = filteredList
                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                coversFiltered = results!!.values as MutableList<DataModel>
                notifyDataSetChanged()
            }

        }
    }
}