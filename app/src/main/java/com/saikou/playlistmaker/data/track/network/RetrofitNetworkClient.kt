package com.saikou.playlistmaker.data.track.network

import com.saikou.playlistmaker.data.track.entity.Response
import com.saikou.playlistmaker.data.track.entity.TrackRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitNetworkClient: NetworkClient {

    private val baseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(BackendApi::class.java)

    override fun doRequest(dto: Any): Response {
        try {
            if (dto is TrackRequest) {
                val resp = iTunesService.search(dto.expression).execute()

                val body = resp.body() ?: Response()

                return body.apply { resultCode = resp.code() }
            } else {
                return Response().apply { resultCode = 400 }
            }
        }catch (e: Exception) {
            return Response().apply { resultCode = 400 }
        }

    }
}