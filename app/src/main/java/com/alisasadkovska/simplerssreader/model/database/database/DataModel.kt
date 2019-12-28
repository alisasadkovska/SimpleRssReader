package com.alisasadkovska.simplerssreader.model.database.database

class DataModel {
    var image: String? = null
    var name: String? = null
    var rss: String? = null
    var smallIcon: String? = null

    constructor() {}

    constructor(
        image: String?,
        name: String?,
        rss: String?,
        smallIcon: String?
    ) {
        this.image = image
        this.name = name
        this.rss = rss
        this.smallIcon = smallIcon
    }

}