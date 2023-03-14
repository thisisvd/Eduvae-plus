package com.digitalinclined.edugate.models.youtubemodel

data class Item(
    val etag: String,
    val id: Id,
    val kind: String,
    val snippet: Snippet
)