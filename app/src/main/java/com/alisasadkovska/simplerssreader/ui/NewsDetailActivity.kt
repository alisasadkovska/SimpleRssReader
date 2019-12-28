package com.alisasadkovska.simplerssreader.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.alisasadkovska.simplerssreader.R
import com.alisasadkovska.simplerssreader.common.Common
import com.alisasadkovska.simplerssreader.common.Common.CONTENT_SIZE
import com.alisasadkovska.simplerssreader.common.Common.TITLE_SIZE
import com.alisasadkovska.simplerssreader.common.Common.coverPath
import com.alisasadkovska.simplerssreader.common.CustomWebChromeClient
import com.alisasadkovska.simplerssreader.common.Utils
import com.google.android.material.appbar.AppBarLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_news_detail.*

class NewsDetailActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener, CustomWebChromeClient.ProgressListener{

    private var isHideToolbarView = false
    private var mUrl:String?=null
    private var mImg:String?=null
    private var mTitle:String?=null
    private var mDate:String?=null
    private var mSource:String?=null
    private var mAuthor:String?=null
    private var mContent:String?=null
    private var themeId:Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Paper.book().contains(Common.THEME_ID))
            themeId = Paper.book().read(Common.THEME_ID)

        Utils.onActivityCreateSetTheme(this, themeId)
        setContentView(R.layout.activity_news_detail)

        toolbar.title = ""
        collapsing_toolbar.title = ""
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        appbar.addOnOffsetChangedListener(this)

        val bundle :Bundle ?=intent.extras

        mUrl = bundle!!.getString("url")
        mImg = bundle.getString("img")
        mTitle = bundle.getString("title")
        mDate = bundle.getString("date")
        mSource = bundle.getString("source")
        mAuthor = bundle.getString("author")
        mContent = bundle.getString("content")

        if (mImg!=null){
            Picasso.get().load(mImg).into(coverImage, object : Callback {
                override fun onSuccess() {
                    progressBarCover.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    progressBarCover.visibility = View.GONE
                    Toasty.error(this@NewsDetailActivity, e!!.message.toString(), Toasty.LENGTH_SHORT).show()
                }
            })
        }else{
            Picasso.get().load(coverPath).into(coverImage, object : Callback {
                override fun onSuccess() {
                    progressBarCover.visibility = View.GONE
                }

                override fun onError(e: java.lang.Exception?) {
                    progressBarCover.visibility = View.GONE
                    Toasty.error(this@NewsDetailActivity, e!!.message.toString(), Toasty.LENGTH_SHORT).show()
                }
            })
        }

        title_on_appbar.text=mTitle
        subtitle_on_appbar.text=mUrl
        date.text = mDate
        textTitle.text=mTitle
        textTitle.textSize = Paper.book().read(TITLE_SIZE)

        textContent.text = mContent
        textContent.textSize = Paper.book().read(CONTENT_SIZE)

        if (Build.VERSION.SDK_INT >= 23)
        initWebView(mUrl)
        else
            web_card.visibility = View.GONE
    }

    private fun initWebView(url: String?) {

        webView.webChromeClient = CustomWebChromeClient(this, this)
        webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                webViewProgress.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webViewProgress.visibility = View.GONE
            }
        }

        webView.settings.javaScriptEnabled=false
        webView.settings.loadsImagesAutomatically=true
        webView.settings.domStorageEnabled=true
        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled=false
        webView.settings.builtInZoomControls=true
        webView.settings.displayZoomControls=true
        webView.scrollBarStyle=View.SCROLLBARS_INSIDE_OVERLAY
        webView.webViewClient= WebViewClient()
        webView.loadUrl(url)
        webViewProgress.progress = 0
    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportFinishAfterTransition()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onOffsetChanged(p0: AppBarLayout?, verticalOffset: Int) {
        val maxScroll = appbar!!.totalScrollRange
        val percentage = kotlin.math.abs(verticalOffset).toFloat() / maxScroll.toFloat()

        if (percentage == 1f && isHideToolbarView) {
            title_appbar.visibility = View.VISIBLE
            isHideToolbarView = !isHideToolbarView
        } else if (percentage < 1f && !isHideToolbarView) {
            title_appbar.visibility = View.GONE
            isHideToolbarView = !isHideToolbarView
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (themeId==0)
        menuInflater.inflate(R.menu.news_details_menu_dark, menu)
        else  menuInflater.inflate(R.menu.news_details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.internet_button->{
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(mUrl)
                startActivity(i)
                return true
            }
            R.id.share_button->{
                try {
                    val i = Intent(Intent.ACTION_SEND)
                    i.type = "text/plan"
                    i.putExtra(Intent.EXTRA_SUBJECT, mSource)
                    val body = "$mTitle\n$mUrl" + getString(R.string.share_from) + getString(R.string.app_name)
                    i.putExtra(Intent.EXTRA_TEXT, body)
                    startActivity(Intent.createChooser(i, getString(R.string.share_with)))
                } catch (e: java.lang.Exception) {
                    Toasty.error(this, getString(R.string.share_error), Toasty.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onUpdateProgress(progressValue: Int) {
        webViewProgress.progress = progressValue
        if (progressValue==100)
            webViewProgress.visibility = View.GONE
    }
}