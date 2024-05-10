package com.example.ebook1

class ModelPdf {

    //variables
    var uid:String = ""
    var id:String = ""
    var title:String = ""
    var description:String = ""
    var categoryId:String = ""
    var url:String = ""
    var timestamp:Long = 0
    var downloadsCount:Long = 0
    var viewsCount:Long = 0

    //empty constructor (required by firebase)
    constructor()

    //parameterised constructor
    constructor(
        uid: String,
        id: String,
        title: String,
        description: String,
        categoryId: String,
        url: String,
        timestamp: Long,
        downloadsCount: Long,
        viewsCount: Long
    ) {
        this.uid = uid
        this.id = id
        this.title = title
        this.description = description
        this.categoryId = categoryId
        this.url = url
        this.timestamp = timestamp
        this.downloadsCount = downloadsCount
        this.viewsCount = viewsCount
    }



}