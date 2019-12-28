package com.alisasadkovska.simplerssreader.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.alisasadkovska.simplerssreader.R
import com.alisasadkovska.simplerssreader.adapter.FeedAdapter
import com.alisasadkovska.simplerssreader.common.Common.API_KEY
import com.alisasadkovska.simplerssreader.common.Common.COUNT
import com.alisasadkovska.simplerssreader.common.Common.FEED_SIZE
import com.alisasadkovska.simplerssreader.common.Common.RSS_to_json_API
import com.alisasadkovska.simplerssreader.common.Common.THEME_ID
import com.alisasadkovska.simplerssreader.common.Common.startFeedSize
import com.alisasadkovska.simplerssreader.common.HTTPDataHandler
import com.alisasadkovska.simplerssreader.common.Utils
import com.alisasadkovska.simplerssreader.model.database.RSSObject
import com.droidnet.DroidListener
import com.droidnet.DroidNet
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_source.*

open class SourceActivity : AppCompatActivity(), DroidListener {

    var rssObject: RSSObject? = null
    var rss: String? = ""
    var name: String? = ""
    private var feedSize = startFeedSize
    private var themeId = 0

    private lateinit var wifiManager: WifiManager
    private lateinit var mDroidNet: DroidNet


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Paper.book().contains(THEME_ID)) themeId =
            Paper.book().read(THEME_ID)
        Utils.onActivityCreateSetTheme(this, themeId)
        setContentView(R.layout.activity_source)
        if (Paper.book().contains(FEED_SIZE)) feedSize =
            Paper.book().read(FEED_SIZE)
        val bundle = intent.extras
        if (bundle != null) {
            rss = bundle.getString("rss")
            name = bundle.getString("name")
        }

        toolbar.title = name
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        mDroidNet = DroidNet.getInstance()
        mDroidNet.addInternetConnectivityListener(this)
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.itemAnimator = DefaultItemAnimator()
    }

    private fun loadRss(rss: String?) {
        if (rss=="")
            return

        @SuppressLint("StaticFieldLeak")
        val loadRssAsync: AsyncTask<String?, String?, String> = object : AsyncTask<String?, String?, String>() {

                override fun onPreExecute() {
                    swipeToRefresh.isRefreshing = true
                }

                override fun doInBackground(vararg params: String?): String? {
                    val result: String
                    val http = HTTPDataHandler()
                    result = http.GetHTTPData(params[0])
                    return result
                }

                override fun onPostExecute(s: String) {
                    rssObject = Gson().fromJson(s, RSSObject::class.java)
                    val adapter = FeedAdapter(rssObject, baseContext)
                    Paper.book().write(name, rssObject)
                    recycler.adapter = adapter
                    adapter.notifyDataSetChanged()
                    swipeToRefresh.isRefreshing = false
                }
            }
        loadRssAsync.execute(RSS_to_json_API + rss + API_KEY + COUNT + feedSize)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onInternetConnectivityChanged(isConnected: Boolean) {
        if (isConnected){
            noCacheLayout.visibility = View.GONE
            loadRss(rss)
            swipeToRefresh.setOnRefreshListener {
                loadRss(rss)
            }
        }else{
            showSnack()
            if (Paper.book().contains(name))
                loadCachedNews(name) else noCacheLayout.visibility = View.VISIBLE
        }
    }

    private fun showSnack() {
        val snack = Snackbar
            .make(root, getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.connect)) { if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) wifiManager.isWifiEnabled = true else startActivity(
                Intent(Settings.ACTION_WIFI_SETTINGS)
            ) }
        snack.setTextColor(Color.WHITE)
        snack.setActionTextColor(Color.RED)
        snack.show()
    }

    private fun loadCachedNews(name: String?) {
        val adapter = FeedAdapter(Paper.book().read(name), baseContext)
        recycler.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDroidNet.removeInternetConnectivityChangeListener(this)
    }
}