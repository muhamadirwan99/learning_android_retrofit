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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
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

        val mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get<MainViewModel>(
            MainViewModel::class.java
        )
        mainViewModel.restaurant.observe(this) { restaurant ->
            setRestaurantData(restaurant)
        }

        // Mengatur RecyclerView agar menampilkan list review secara vertikal
        val layoutManager = LinearLayoutManager(this)
        binding.rvReview.layoutManager = layoutManager

        // Menambahkan garis pembatas antar item review agar lebih mudah dibedakan
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvReview.addItemDecoration(itemDecoration)

        // Memanggil API untuk mengambil data restaurant dan review saat aplikasi pertama kali dibuka
        mainViewModel.listReview.observe(this) { consumerReviews ->
            setReviewData(consumerReviews)
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        // Menangani aksi ketika tombol kirim review ditekan
        binding.btnSend.setOnClickListener { view ->
            // Mengirim review ke server dengan isi dari input text user
            mainViewModel.postReview(binding.edReview.text.toString())

            // Menyembunyikan keyboard setelah tombol kirim ditekan agar UI lebih bersih
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
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
}