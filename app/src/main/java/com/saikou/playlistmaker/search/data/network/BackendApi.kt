package com.saikou.playlistmaker.search.data.network

import com.saikou.playlistmaker.search.data.entity.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BackendApi {
    @GET("/search?entity=song" )
    fun search(@Query("term") text: String): Call<TrackSearchResponse>
}