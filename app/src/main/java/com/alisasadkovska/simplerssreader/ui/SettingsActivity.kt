package com.alisasadkovska.simplerssreader.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.alisasadkovska.simplerssreader.R
import com.alisasadkovska.simplerssreader.common.Common.CACHE
import com.alisasadkovska.simplerssreader.common.Common.CONTENT_SIZE
import com.alisasadkovska.simplerssreader.common.Common.FEED_SIZE
import com.alisasadkovska.simplerssreader.common.Common.THEME_ID
import com.alisasadkovska.simplerssreader.common.Common.TITLE_SIZE
import com.alisasadkovska.simplerssreader.common.Common.startContentSize
import com.alisasadkovska.simplerssreader.common.Common.startFeedSize
import com.alisasadkovska.simplerssreader.common.Common.startTitleSize
import com.alisasadkovska.simplerssreader.common.Utils
import com.alisasadkovska.simplerssreader.model.database.database.DataModel
import com.xw.repo.BubbleSeekBar
import es.dmoral.toasty.Toasty
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private var themeId:Int = 0
    private var feedSize = startFeedSize
    private var titleSize = startTitleSize
    private var contentSize = startContentSize

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Paper.book().contains(THEME_ID))
        themeId = Paper.book().read(THEME_ID)
        Utils.onActivityCreateSetTheme(this, themeId)
        setContentView(R.layout.activity_settings)

        toolbar.title = getString(R.string.settings)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        checkIfCacheExists()

        if (Paper.book().contains(FEED_SIZE))
            feedSize = Paper.book().read(FEED_SIZE)

        if (Paper.book().contains(TITLE_SIZE)){
            titleSize = Paper.book().read(TITLE_SIZE)
            textTitle.textSize = titleSize
        }

        if (Paper.book().contains(CONTENT_SIZE)){
            contentSize = Paper.book().read(CONTENT_SIZE)
            textContent.textSize = contentSize
        }


        seekBarFeedSize.setProgress(feedSize.toFloat())
        seekBarTitleSize.setProgress(titleSize)
        seekBarContentSize.setProgress(contentSize)

        seekBarFeedSize.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener{
            override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {
                Paper.book().write(FEED_SIZE, progress) }
            override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {}
            override fun getProgressOnFinally(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {}
        }
        seekBarTitleSize.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener{
            override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {
                Paper.book().write(TITLE_SIZE, progressFloat)
                textTitle.textSize = progressFloat }

            override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {}
            override fun getProgressOnFinally(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {}

        }
        seekBarContentSize.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener{
            override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {
                Paper.book().write(CONTENT_SIZE, progressFloat)
                textContent.textSize = progressFloat }

            override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {}
            override fun getProgressOnFinally(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {}

        }

        when(themeId){
            1->{
                themeSwitch.setIsNight(false)
                txtSwitchStatus.text = getString(R.string.day)
            }
            0->{
                themeSwitch.setIsNight(true)
                txtSwitchStatus.text = getString(R.string.night)
            }
        }

        themeSwitch.setListener { is_night ->
            if (is_night)
                darkModeOn() else darkModeOff()
        }
    }

    private fun checkIfCacheExists() {
        val listNews: MutableList<DataModel> = Paper.book().read(CACHE)

        deleteButton.setOnClickListener {
            for(i in listNews){
                Paper.book().delete(i.name)
            }
            deleteButton.isEnabled = false
            Toasty.info(this, getString(R.string.cache_was_deleted), Toasty.LENGTH_SHORT).show()
        }
    }

    private fun darkModeOff() {
        Paper.book().write(THEME_ID,1)
        txtSwitchStatus.text = getString(R.string.day)
        txtSwitchStatus.setTextColor(resources.getColor(android.R.color.primary_text_light))
        root.setBackgroundColor(resources.getColor(android.R.color.background_light))
        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        toolbar.setTitleTextColor(resources.getColor(android.R.color.primary_text_light))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
        textMax.setTextColor(resources.getColor(android.R.color.primary_text_light))
        textTitleSize.setTextColor(resources.getColor(android.R.color.primary_text_light))
        textTitle.setTextColor(resources.getColor(android.R.color.primary_text_light))
        textTitleContent.setTextColor(resources.getColor(android.R.color.primary_text_light))
        textContent.setTextColor(resources.getColor(android.R.color.primary_text_light))
        deleteButton.setBackgroundColor(resources.getColor(R.color.colorAccent))
    }
    private fun darkModeOn() {
        Paper.book().write(THEME_ID,0)
        txtSwitchStatus.text = getString(R.string.night)
        txtSwitchStatus.setTextColor(resources.getColor(android.R.color.primary_text_dark))
        root.setBackgroundColor(resources.getColor(android.R.color.background_dark))
        window.statusBarColor = resources.getColor(R.color.darkColorPrimaryDark)
        toolbar.setBackgroundColor(resources.getColor(R.color.darkColorPrimary))
        toolbar.setTitleTextColor(resources.getColor(android.R.color.primary_text_dark))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        textMax.setTextColor(resources.getColor(android.R.color.primary_text_dark))
        textTitleSize.setTextColor(resources.getColor(android.R.color.primary_text_dark))
        textTitle.setTextColor(resources.getColor(android.R.color.primary_text_dark))
        textTitleContent.setTextColor(resources.getColor(android.R.color.primary_text_dark))
        textContent.setTextColor(resources.getColor(android.R.color.primary_text_dark))
        deleteButton.setBackgroundColor(resources.getColor(R.color.darkColorAccent))
    }

    override fun onBackPressed() {
        goTpPreviousActivity()
    }
    private fun goTpPreviousActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> goTpPreviousActivity()
        }
        return super.onOptionsItemSelected(item)
    }
}
