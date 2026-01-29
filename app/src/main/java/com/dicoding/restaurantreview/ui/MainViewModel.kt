package com.dicoding.restaurantreview.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.restaurantreview.data.response.CustomerReviewsItem
import com.dicoding.restaurantreview.data.response.PostReviewResponse
import com.dicoding.restaurantreview.data.response.Restaurant
import com.dicoding.restaurantreview.data.response.RestaurantResponse
import com.dicoding.restaurantreview.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val _restaurant = MutableLiveData<Restaurant>()
    val restaurant: LiveData<Restaurant> = _restaurant

    private val _listReview = MutableLiveData<List<CustomerReviewsItem>>()
    val listReview: LiveData<List<CustomerReviewsItem>> = _listReview

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "MainViewModel"
        private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
    }

    init {
        findRestaurant()
    }

    private fun findRestaurant() {
        // Menampilkan loading indicator karena proses request ke server butuh waktu
        _isLoading.value = true

        // Membuat request ke API untuk mendapatkan detail restaurant berdasarkan ID
        val client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID)

        // Menjalankan request secara asynchronous agar tidak memblokir UI thread
        client.enqueue(object : Callback<RestaurantResponse> {
            override fun onResponse(
                call: Call<RestaurantResponse?>,
                response: Response<RestaurantResponse?>
            ) {
                // Menyembunyikan loading karena response sudah diterima (berhasil atau gagal)
                _isLoading.value = false

                // Mengecek apakah response dari server berhasil (status code 200-299)
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    // Memastikan data yang diterima tidak null sebelum digunakan
                    if (responseBody != null) {
                        // Menampilkan informasi restaurant (nama, gambar, deskripsi)
                        _restaurant.value = responseBody.restaurant
                        // Menampilkan daftar review dari customer lain
                        _listReview.value = responseBody.restaurant.customerReviews
                    }
                } else {
                    // Mencatat error ke Logcat jika request gagal untuk debugging
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(
                call: Call<RestaurantResponse?>,
                t: Throwable
            ) {
                // Menyembunyikan loading jika terjadi error koneksi
                _isLoading.value = false
                // Mencatat detail error (misal: no internet, timeout) untuk debugging
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun postReview(review: String) {
        // Menampilkan loading saat mengirim review ke server
        _isLoading.value = true

        // Membuat request POST untuk mengirim review baru ke API
        // Parameter: ID restaurant, nama user (hardcoded), dan isi review
        val client = ApiConfig.getApiService().postReview(RESTAURANT_ID, "Dicoding", review)
        client.enqueue(object : Callback<PostReviewResponse> {
            override fun onResponse(
                call: Call<PostReviewResponse?>,
                response: Response<PostReviewResponse?>
            ) {
                // Menyembunyikan loading setelah mendapat response dari server
                _isLoading.value = false

                val responseBody = response.body()

                // Jika review berhasil dikirim, update tampilan dengan list review terbaru
                if (response.isSuccessful && responseBody != null) {
                    // Server mengirim kembali semua review termasuk yang baru ditambahkan
                    _listReview.value = responseBody.customerReviews
                } else {
                    // Mencatat error jika pengiriman review gagal
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(
                call: Call<PostReviewResponse?>,
                t: Throwable
            ) {
                // Menyembunyikan loading jika terjadi error koneksi saat POST review
                _isLoading.value = false
                // Mencatat detail error untuk debugging
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}