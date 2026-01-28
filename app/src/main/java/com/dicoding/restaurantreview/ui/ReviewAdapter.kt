package com.dicoding.restaurantreview.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.restaurantreview.data.response.CustomerReviewsItem
import com.dicoding.restaurantreview.databinding.ItemReviewBinding

// ListAdapter digunakan untuk efisiensi update list dengan DiffUtil
// DiffUtil akan otomatis mendeteksi perubahan data dan hanya update item yang berubah
class ReviewAdapter : ListAdapter<CustomerReviewsItem, ReviewAdapter.MyViewHolder>(DIFF_CALBACK) {

    // Dipanggil saat RecyclerView butuh ViewHolder baru (item baru muncul di layar)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.MyViewHolder {
        // Inflate layout item_review.xml menggunakan View Binding
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    // Dipanggil saat RecyclerView perlu menampilkan data ke ViewHolder tertentu
    override fun onBindViewHolder(holder: ReviewAdapter.MyViewHolder, position: Int) {
        // Mengambil data review berdasarkan posisi di list
        val review = getItem(position)
        // Mengikat data review ke view holder
        holder.bind(review)
    }

    class MyViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: CustomerReviewsItem){
            // Menampilkan review dan nama reviewer dalam format: "review - nama"
            binding.tvItem.text = "${review.review}\n- ${review.name}"
        }
    }

    companion object {
        // DiffUtil Callback untuk membandingkan item lama dan baru secara efisien
        // Ini membuat RecyclerView tidak perlu refresh seluruh list saat ada perubahan
        val DIFF_CALBACK = object : DiffUtil.ItemCallback<CustomerReviewsItem>(){
            // Mengecek apakah dua item adalah item yang sama (biasanya compare ID)
            override fun areItemsTheSame(
                oldItem: CustomerReviewsItem,
                newItem: CustomerReviewsItem
            ): Boolean {
                return oldItem == newItem
            }

            // Mengecek apakah konten/isi dari dua item sama
            // Jika berbeda, item akan di-update dengan animasi
            override fun areContentsTheSame(
                oldItem: CustomerReviewsItem,
                newItem: CustomerReviewsItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}