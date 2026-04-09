package com.saikou.playlistmaker.search.data.network

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.saikou.playlistmaker.R
import com.saikou.playlistmaker.search.data.entity.Response
import com.saikou.playlistmaker.search.data.entity.TrackRequest
import com.saikou.playlistmaker.global.Const.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitNetworkClient(private val context: Context) : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(BackendApi::class.java)

    override fun doRequest(dto: Any): Response {

        if (isConnected() == false) {
            return Response().apply {
                resultCode = -1
                resultStateMessage = context.getString(R.string.search_error_network)
            }
        }
        if (dto !is TrackRequest) {
            return Response().apply {
                resultCode = 400
                resultStateMessage = context.getString(R.string.search_error_network)
            }
        }

        val response = iTunesService.search(dto.expression).execute()
        val body = response.body()
        return body?.apply {
            resultCode = response.code()
            if (this.results.isEmpty()) {
                resultStateMessage = context.getString(R.string.search_error_not_found)
            }
        } ?: Response().apply {
            resultCode = response.code()
            resultStateMessage = context.getString(R.string.search_error_not_found)

        }
    }

    override fun emptyMessage(): String? {
       return context.getString(R.string.search_error_not_found)
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}