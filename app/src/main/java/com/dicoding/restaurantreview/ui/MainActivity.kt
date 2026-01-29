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

        // Membuat instance ViewModel yang akan bertahan meskipun configuration change (rotasi layar)
        // ViewModel memisahkan logika bisnis dari UI, sehingga data tidak hilang saat rotasi
        val mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get<MainViewModel>(
            MainViewModel::class.java
        )

        // Mengobservasi data restaurant dari ViewModel menggunakan LiveData
        // Setiap kali data restaurant berubah, UI akan otomatis update
        // Lifecycle-aware: observer hanya aktif saat Activity dalam state active (STARTED/RESUMED)
        mainViewModel.restaurant.observe(this) { restaurant ->
            setRestaurantData(restaurant)
        }

        // Mengatur RecyclerView agar menampilkan list review secara vertikal
        val layoutManager = LinearLayoutManager(this)
        binding.rvReview.layoutManager = layoutManager

        // Menambahkan garis pembatas antar item review agar lebih mudah dibedakan
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvReview.addItemDecoration(itemDecoration)

        // Mengobservasi list review dari ViewModel
        // Setiap ada perubahan (data pertama kali load atau setelah post review), list akan update otomatis
        // Observer pattern ini memastikan UI selalu sinkron dengan data terbaru
        mainViewModel.listReview.observe(this) { consumerReviews ->
            setReviewData(consumerReviews)
        }

        // Mengobservasi status loading untuk menampilkan/menyembunyikan progress bar
        // Memberikan feedback visual ke user bahwa aplikasi sedang memproses request
        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        // Menangani aksi ketika tombol kirim review ditekan
        binding.btnSend.setOnClickListener { view ->
            // Mengirim review ke server melalui ViewModel
            // ViewModel akan handle API call dan update LiveData secara otomatis
            // Data dari EditText langsung diambil dan dikirim ke API
            mainViewModel.postReview(binding.edReview.text.toString())

            // Menyembunyikan keyboard setelah tombol kirim ditekan
            // Ini meningkatkan UX karena user bisa langsung melihat hasil tanpa keyboard menghalangi
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    private fun setRestaurantData(restaurant: Restaurant) {
        // Menampilkan nama restaurant di TextView untuk header/title halaman
        binding.tvTitle.text = restaurant.name

        // Menampilkan deskripsi restaurant untuk memberi informasi detail ke user
        binding.tvDescription.text = restaurant.description

        // Menggunakan Glide library untuk load gambar dari URL
        // Kenapa Glide? Karena handle caching otomatis, loading async, dan error handling
        // Tanpa Glide, kita harus manual download gambar di background thread
        Glide.with(this@MainActivity)
            .load("https://restaurant-api.dicoding.dev/images/large/${restaurant.pictureId}")
            .into(binding.ivPicture)
    }

    private fun setReviewData(consumerReviews: List<CustomerReviewsItem>) {
        // Membuat adapter untuk menjembatani data (List) dengan RecyclerView (UI)
        // Adapter pattern: memisahkan logika tampilan item dari logika list
        val adapter = ReviewAdapter()

        // submitList() adalah cara ListAdapter menerima data baru
        // Kenapa submitList? Karena DiffUtil akan otomatis compare old vs new data
        // Hasilnya: hanya item yang berubah yang di-render ulang (lebih efisien!)
        adapter.submitList(consumerReviews)

        // Menghubungkan adapter dengan RecyclerView agar data ditampilkan
        // Tanpa ini, RecyclerView tidak tahu data apa yang harus ditampilkan
        binding.rvReview.adapter = adapter

        // Mengosongkan input field setelah review berhasil dikirim
        // UX: siap untuk input review berikutnya, user tidak perlu hapus manual
        binding.edReview.setText("")
    }

    private fun showLoading(isLoading: Boolean) {
        // Menampilkan atau menyembunyikan progress bar berdasarkan status loading
        // Kenapa perlu? Karena user harus tahu bahwa aplikasi sedang bekerja (fetching data)
        // Tanpa loading indicator, user akan bingung apakah aplikasi hang atau sedang proses
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}