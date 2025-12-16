package com.saikou.playlistmaker

import com.saikou.playlistmaker.entity.ResponseWrapper
import com.saikou.playlistmaker.entity.Track
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BackendApi {
    @GET("/search?entity=song" )
    fun search(@Query("term") text: String): Call<ResponseWrapper>
}