package com.dicoding.restaurantreview.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dicoding.restaurantreview.R
import com.dicoding.restaurantreview.data.response.CustomerReviewsItem
import com.dicoding.restaurantreview.data.response.PostReviewResponse
import com.dicoding.restaurantreview.data.response.Restaurant
import com.dicoding.restaurantreview.data.response.RestaurantResponse
import com.dicoding.restaurantreview.data.retrofit.ApiConfig
import com.dicoding.restaurantreview.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    // Menggunakan View Binding untuk mengakses view tanpa findViewById (lebih aman dan efisien)
    private lateinit var binding: ActivityMainBinding

    companion object {
        // Tag untuk logging, memudahkan filter log di Logcat
        private const val TAG = "MainActivity"
        // ID restaurant yang akan ditampilkan (hardcoded untuk demo)
        private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mengaktifkan tampilan edge-to-edge (konten sampai ke tepi layar)
        enableEdgeToEdge()
        // Inisialisasi View Binding untuk mengakses view di layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Menangani padding untuk system bars (status bar & navigation bar)
        // Agar konten tidak tertutup oleh system UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Menyembunyikan action bar untuk tampilan yang lebih clean
        supportActionBar?.hide()

        // Mengatur RecyclerView agar menampilkan list review secara vertikal
        val layoutManager = LinearLayoutManager(this)
        binding.rvReview.layoutManager = layoutManager

        // Menambahkan garis pembatas antar item review agar lebih mudah dibedakan
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvReview.addItemDecoration(itemDecoration)

        // Memanggil API untuk mengambil data restaurant dan review saat aplikasi pertama kali dibuka
        findRestaurant()

        // Menangani aksi ketika tombol kirim review ditekan
        binding.btnSend.setOnClickListener { view ->
            // Mengirim review ke server dengan isi dari input text user
            postReview(binding.edReview.text.toString())

            // Menyembunyikan keyboard setelah tombol kirim ditekan agar UI lebih bersih
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun findRestaurant() {
        // Menampilkan loading indicator karena proses request ke server butuh waktu
        showLoading(true)

        // Membuat request ke API untuk mendapatkan detail restaurant berdasarkan ID
        val client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID)

        // Menjalankan request secara asynchronous agar tidak memblokir UI thread
        client.enqueue(object : Callback<RestaurantResponse> {
            override fun onResponse(
                call: Call<RestaurantResponse?>,
                response: Response<RestaurantResponse?>
            ) {
                // Menyembunyikan loading karena response sudah diterima (berhasil atau gagal)
                showLoading(false)

                // Mengecek apakah response dari server berhasil (status code 200-299)
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    // Memastikan data yang diterima tidak null sebelum digunakan
                    if (responseBody != null) {
                        // Menampilkan informasi restaurant (nama, gambar, deskripsi)
                        setRestaurantData(responseBody.restaurant)
                        // Menampilkan daftar review dari customer lain
                        setReviewData(responseBody.restaurant.customerReviews)
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
                showLoading(false)
                // Mencatat detail error (misal: no internet, timeout) untuk debugging
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })

    }

    private fun setRestaurantData(restaurant: Restaurant) {
        // Menampilkan nama restaurant di TextView
        binding.tvTitle.text = restaurant.name
        // Menampilkan deskripsi restaurant di TextView
        binding.tvDescription.text = restaurant.description

        // Menggunakan Glide library untuk load gambar dari URL dan menampilkannya
        // Glide menangani caching dan loading secara otomatis
        Glide.with(this@MainActivity)
            .load("https://restaurant-api.dicoding.dev/images/large/${restaurant.pictureId}")
            .into(binding.ivPicture)
    }

    private fun setReviewData(consumerReviews: List<CustomerReviewsItem>) {
        // Membuat adapter untuk menampilkan list review di RecyclerView
        val adapter = ReviewAdapter()
        // Mengirim data review ke adapter menggunakan submitList (untuk DiffUtil)
        adapter.submitList(consumerReviews)
        // Menghubungkan adapter dengan RecyclerView agar data ditampilkan
        binding.rvReview.adapter = adapter
        // Mengosongkan input field setelah review ditampilkan
        binding.edReview.setText("")
    }

    private fun showLoading(isLoading: Boolean) {
        // Menampilkan atau menyembunyikan progress bar berdasarkan status loading
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }

    }

    private fun postReview(review: String) {
        // Menampilkan loading saat mengirim review ke server
        showLoading(true)

        // Membuat request POST untuk mengirim review baru ke API
        // Parameter: ID restaurant, nama user (hardcoded), dan isi review
        val client = ApiConfig.getApiService().postReview(RESTAURANT_ID, "Dicoding", review)
        client.enqueue(object : Callback<PostReviewResponse> {
            override fun onResponse(
                call: Call<PostReviewResponse?>,
                response: Response<PostReviewResponse?>
            ) {
                // Menyembunyikan loading setelah mendapat response dari server
                showLoading(false)

                val responseBody = response.body()

                // Jika review berhasil dikirim, update tampilan dengan list review terbaru
                if (response.isSuccessful && responseBody != null) {
                    // Server mengirim kembali semua review termasuk yang baru ditambahkan
                    setReviewData(responseBody.customerReviews)
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
                showLoading(false)
                // Mencatat detail error untuk debugging
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}