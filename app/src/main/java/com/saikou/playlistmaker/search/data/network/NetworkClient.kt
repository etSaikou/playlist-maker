package com.saikou.playlistmaker.search.data.network

import com.saikou.playlistmaker.search.data.entity.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
    fun emptyMessage(): String?
}