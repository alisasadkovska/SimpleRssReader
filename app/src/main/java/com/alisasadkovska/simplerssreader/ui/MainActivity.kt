package com.alisasadkovska.simplerssreader.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.alisasadkovska.simplerssreader.R
import com.alisasadkovska.simplerssreader.adapter.NewspaperAdapter
import com.alisasadkovska.simplerssreader.common.Common.CACHE
import com.alisasadkovska.simplerssreader.common.Common.CONTENT_SIZE
import com.alisasadkovska.simplerssreader.common.Common.DATA_REFERENCE
import com.alisasadkovska.simplerssreader.common.Common.FEED_SIZE
import com.alisasadkovska.simplerssreader.common.Common.FIRST_RUN
import com.alisasadkovska.simplerssreader.common.Common.THEME_ID
import com.alisasadkovska.simplerssreader.common.Common.TITLE_SIZE
import com.alisasadkovska.simplerssreader.common.Common.database
import com.alisasadkovska.simplerssreader.common.Common.startContentSize
import com.alisasadkovska.simplerssreader.common.Common.startFeedSize
import com.alisasadkovska.simplerssreader.common.Common.startTitleSize
import com.alisasadkovska.simplerssreader.common.Utils
import com.alisasadkovska.simplerssreader.model.database.database.DataModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import es.dmoral.toasty.Toasty
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var themeId:Int = 0
    lateinit var adapter: NewspaperAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Paper.book().contains(THEME_ID))
        themeId = Paper.book().read(THEME_ID)
        Utils.onActivityCreateSetTheme(this, themeId)
        setContentView(R.layout.activity_main)

        if (!Paper.book().contains(FIRST_RUN))
            isFirstRun()

        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)

        getDataFromDatabase()
    }

    private fun isFirstRun() {
        Paper.book().write(FIRST_RUN, true)
        Paper.book().write(THEME_ID, themeId)
        Paper.book().write(FEED_SIZE, startFeedSize)
        Paper.book().write(TITLE_SIZE, startTitleSize)
        Paper.book().write(CONTENT_SIZE, startContentSize)
    }

    private fun getDataFromDatabase(){
        if (Paper.book().contains(CACHE))
            Paper.book().delete(CACHE)

        val reference = database.getReference(DATA_REFERENCE)
        val data: MutableList<DataModel> = ArrayList()
        reference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Toasty.error(this@MainActivity, p0.message, Toasty.LENGTH_SHORT).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnap in dataSnapshot.children){
                    val model: DataModel = postSnap.getValue(DataModel::class.java)!!
                    data.add(model)
                    Paper.book().write(CACHE, data)
                    fetchData()
                } } }) }

    private fun fetchData() {
        val listNews: MutableList<DataModel> = Paper.book().read(CACHE)
        adapter = NewspaperAdapter(this, listNews)

        val screenSize = resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK

        var gridLayoutManager = GridLayoutManager(this,3)

        when(screenSize){
            Configuration.SCREENLAYOUT_SIZE_NORMAL->gridLayoutManager= GridLayoutManager(this,3)
            Configuration.SCREENLAYOUT_SIZE_SMALL->gridLayoutManager= GridLayoutManager(this,3)
            Configuration.SCREENLAYOUT_SIZE_LARGE->gridLayoutManager= GridLayoutManager(this,4)
            Configuration.SCREENLAYOUT_SIZE_XLARGE->gridLayoutManager=GridLayoutManager(this,6)
        }



        recyclerNews.layoutManager = gridLayoutManager
        recyclerNews.itemAnimator = DefaultItemAnimator()
        recyclerNews.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (themeId==0)
        menuInflater.inflate(R.menu.main_menu_dark, menu)
        else
            menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        if (searchItem!=null){
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return false
                }
            })
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_settings->{
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_saved->{
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
