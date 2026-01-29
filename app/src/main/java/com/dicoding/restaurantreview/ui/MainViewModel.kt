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

    // Private MutableLiveData untuk diubah dari dalam ViewModel (internal state)
    // Kenapa private? Agar hanya ViewModel yang bisa mengubah data, mencegah perubahan dari luar
    private val _restaurant = MutableLiveData<Restaurant>()

    // Public LiveData (read-only) untuk diobserve oleh Activity/Fragment
    // Kenapa expose sebagai LiveData? Agar UI tidak bisa mengubah data, hanya bisa "mendengarkan" perubahan
    // Pattern ini disebut "encapsulation" - melindungi internal state
    val restaurant: LiveData<Restaurant> = _restaurant

    // Private MutableLiveData untuk menyimpan list review
    // Akan diupdate setiap kali data review berubah (initial load atau setelah post review baru)
    private val _listReview = MutableLiveData<List<CustomerReviewsItem>>()

    // Public LiveData untuk list review yang diobserve oleh RecyclerView
    // Ketika value berubah, RecyclerView otomatis update tampilan
    val listReview: LiveData<List<CustomerReviewsItem>> = _listReview

    // Private MutableLiveData untuk status loading
    // Kenapa perlu? Untuk koordinasi antara network request dan UI loading indicator
    private val _isLoading = MutableLiveData<Boolean>()

    // Public LiveData untuk status loading yang diobserve oleh ProgressBar
    // true = show loading, false = hide loading
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        // TAG untuk logging, memudahkan filter di Logcat saat debugging
        private const val TAG = "MainViewModel"

        // Restaurant ID yang di-hardcode untuk demo
        // Dalam aplikasi production, ini biasanya dikirim dari screen sebelumnya via Intent/Navigation
        private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
    }

    // init block dipanggil otomatis saat ViewModel pertama kali dibuat
    // Kenapa panggil findRestaurant() di sini? Agar data langsung di-fetch begitu ViewModel dibuat
    // Dengan begitu, Activity tidak perlu manual trigger load data
    init {
        findRestaurant()
    }

    private fun findRestaurant() {
        // Menampilkan loading indicator karena proses request ke server butuh waktu
        // User harus tahu bahwa aplikasi sedang bekerja, bukan hang
        _isLoading.value = true

        // Membuat request ke API untuk mendapatkan detail restaurant berdasarkan ID
        // ApiConfig.getApiService() memberikan instance Retrofit yang sudah dikonfigurasi
        val client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID)

        // Menjalankan request secara asynchronous agar tidak memblokir UI thread
        // Kenapa enqueue? Karena network request tidak boleh di main thread (akan crash)
        // Callback akan dipanggil di main thread setelah response diterima
        client.enqueue(object : Callback<RestaurantResponse> {
            override fun onResponse(
                call: Call<RestaurantResponse?>,
                response: Response<RestaurantResponse?>
            ) {
                // Menyembunyikan loading karena response sudah diterima (berhasil atau gagal)
                // Penting untuk selalu hide loading di sini, bukan hanya di success case
                _isLoading.value = false

                // Mengecek apakah response dari server berhasil (status code 200-299)
                // Kenapa perlu cek? Karena onResponse() dipanggil meskipun server return error 4xx/5xx
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    // Memastikan data yang diterima tidak null sebelum digunakan
                    // Kenapa bisa null? Server bisa return empty body meskipun status 200
                    if (responseBody != null) {
                        // Update LiveData dengan data restaurant
                        // Begitu value di-set, semua observer (Activity) akan otomatis notified
                        _restaurant.value = responseBody.restaurant

                        // Update LiveData dengan list review dari response
                        // RecyclerView akan otomatis refresh tampilannya
                        _listReview.value = responseBody.restaurant.customerReviews
                    }
                } else {
                    // Mencatat error ke Logcat jika request gagal untuk debugging
                    // response.message() berisi HTTP status message (e.g., "Not Found", "Internal Server Error")
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(
                call: Call<RestaurantResponse?>,
                t: Throwable
            ) {
                // Menyembunyikan loading jika terjadi error koneksi
                // onFailure dipanggil untuk error sebelum sampai server (no internet, timeout, parsing error)
                _isLoading.value = false

                // Mencatat detail error (misal: no internet, timeout, UnknownHostException) untuk debugging
                // t.message berisi error message dari exception yang terjadi
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun postReview(review: String) {
        // Menampilkan loading saat mengirim review ke server
        // Memberikan feedback ke user bahwa proses pengiriman sedang berjalan
        _isLoading.value = true

        // Membuat request POST untuk mengirim review baru ke API
        // Parameter: ID restaurant (mana restaurant yang direview), nama user (siapa yang review), dan isi review
        // "Dicoding" di-hardcode untuk demo, dalam production biasanya dari user profile
        val client = ApiConfig.getApiService().postReview(RESTAURANT_ID, "Dicoding", review)

        // Enqueue untuk async request - proses di background thread
        client.enqueue(object : Callback<PostReviewResponse> {
            override fun onResponse(
                call: Call<PostReviewResponse?>,
                response: Response<PostReviewResponse?>
            ) {
                // Menyembunyikan loading setelah mendapat response dari server
                // Baik berhasil atau gagal, loading harus di-hide
                _isLoading.value = false

                val responseBody = response.body()

                // Jika review berhasil dikirim, update tampilan dengan list review terbaru
                // Kenapa perlu update? Agar user langsung melihat review yang baru saja dikirim
                if (response.isSuccessful && responseBody != null) {
                    // Server mengirim kembali semua review termasuk yang baru ditambahkan
                    // Kenapa server return semua? Agar client tidak perlu merge manual old + new review
                    // Begitu di-set, LiveData akan notify observer → RecyclerView auto-refresh
                    _listReview.value = responseBody.customerReviews
                } else {
                    // Mencatat error jika pengiriman review gagal
                    // Bisa karena validation error (review kosong), server error, dll
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(
                call: Call<PostReviewResponse?>,
                t: Throwable
            ) {
                // Menyembunyikan loading jika terjadi error koneksi saat POST review
                // onFailure dipanggil jika request gagal sebelum sampai server
                _isLoading.value = false

                // Mencatat detail error untuk debugging
                // Bisa karena no internet, timeout, atau server unreachable
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}