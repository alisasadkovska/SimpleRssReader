package com.alisasadkovska.simplerssreader.common
import com.google.firebase.database.FirebaseDatabase

object Common {
    const val API_KEY = "&api_key=hirycfldd61k0dcydlqmberfdxb4ligzq69x1hdt"
    const val RSS_to_json_API = "https://api.rss2json.com/v1/api.json?rss_url="
    const val COUNT = "&count="

    const val startFeedSize = 25
    const val startTitleSize = 17f
    const val startContentSize = 14f
    const val defaultFont = "fonts/Roboto.ttf"

    lateinit var coverPath: String

    private var mDatabase: FirebaseDatabase? = null

    val database: FirebaseDatabase
        get() {
            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance()
            return mDatabase!!
        }

    //database
    const val DATA_REFERENCE = "ukraine"

    //paper DB
    const val FIRST_RUN = "firstRun"
    const val CACHE = "cache"
    const val FAVORITE = "favorite"
    const val THEME_ID = "theme_id"
    const val FEED_SIZE = "feedSize"
    const val TITLE_SIZE = "titleSize"
    const val CONTENT_SIZE = "contentSize"

}