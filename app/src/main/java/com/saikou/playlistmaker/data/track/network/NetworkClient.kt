package com.saikou.playlistmaker.data.track.network

import com.saikou.playlistmaker.data.track.entity.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}