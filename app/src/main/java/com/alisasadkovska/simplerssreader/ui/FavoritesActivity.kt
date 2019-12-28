package com.alisasadkovska.simplerssreader.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alisasadkovska.simplerssreader.Interface.RecyclerItemTouchHelperListener
import com.alisasadkovska.simplerssreader.R
import com.alisasadkovska.simplerssreader.adapter.FavoritesAdapter
import com.alisasadkovska.simplerssreader.common.Common
import com.alisasadkovska.simplerssreader.common.Common.FAVORITE
import com.alisasadkovska.simplerssreader.common.Utils
import com.alisasadkovska.simplerssreader.common.helper.RecyclerItemTouchHelper
import com.alisasadkovska.simplerssreader.model.database.Item
import com.google.android.material.snackbar.Snackbar
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_favorites.*

class FavoritesActivity : AppCompatActivity(), RecyclerItemTouchHelperListener {

    private var themeId:Int = 0
    private var favoritesList:MutableList<Item> = ArrayList()
    lateinit var adapter: FavoritesAdapter

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        if (viewHolder is FavoritesAdapter.FavoritesViewHolder){
            val deletedItem = favoritesList[viewHolder.adapterPosition]
            val deleteIndex = viewHolder.adapterPosition
            adapter.removeItem(deleteIndex)
            if (favoritesList.size==0)
                noCacheLayout.visibility = View.VISIBLE

            Snackbar.make(root, getString(R.string.deleted), Snackbar.LENGTH_LONG)
                .setTextColor(Color.WHITE)
                .setAction(getString(R.string.restore))
                {
                    noCacheLayout.visibility = View.GONE
                    adapter.restoreItem(deletedItem, deleteIndex) }
                .setActionTextColor(Color.YELLOW)
                .show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Paper.book().contains(Common.THEME_ID))
            themeId = Paper.book().read(Common.THEME_ID)
        Utils.onActivityCreateSetTheme(this, themeId)
        setContentView(R.layout.activity_favorites)

        toolbar.title = getString(R.string.save)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        if (Paper.book().contains(FAVORITE)){
            favoritesList = Paper.book().read(FAVORITE)
            if (favoritesList.size==0)
                noCacheLayout.visibility = View.VISIBLE else noCacheLayout.visibility = View.GONE
            adapter = FavoritesAdapter(favoritesList)
            fetchData()
        }else
            noCacheLayout.visibility = View.VISIBLE

    }

    private fun fetchData() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd=true
        layoutManager.reverseLayout=true
        recycler.layoutManager = layoutManager
        recycler.itemAnimator = DefaultItemAnimator()
        recycler.adapter = adapter

        adapter.setOnItemClickListener(object : FavoritesAdapter.ClickListener{
            override fun onClick(pos: Int, aView: View) {
                val intent = Intent(this@FavoritesActivity, NewsDetailActivity::class.java)

                intent.putExtra("url", favoritesList[pos].link)
                var title: String = favoritesList[pos].title
                title = title.replace("(<.*?>)|(&.*?;)|([ ]{2,})".toRegex(), "")
                intent.putExtra("title", title)
                intent.putExtra("img", favoritesList[pos].enclosure.link)
                intent.putExtra("date", favoritesList[pos].pubDate)
                intent.putExtra("source", favoritesList[pos].guid)
                intent.putExtra("author", favoritesList[pos].author)
                var content: String = favoritesList[pos].content
                content = content.replace("<p>", "")
                content = content.replace("</p>", " \n")
                content = content.replace("(<.*?>)|(&.*?;)|([ ]{2,})".toRegex(), "")

                intent.putExtra("content", content)

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

        })
        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =  RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycler)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
