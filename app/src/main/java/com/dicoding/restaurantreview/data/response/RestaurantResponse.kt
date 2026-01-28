package com.dicoding.restaurantreview.data.response

import com.google.gson.annotations.SerializedName

// Data class untuk response API saat mengambil detail restaurant
// @SerializedName: mapping nama field JSON ke property Kotlin
data class RestaurantResponse(

	@field:SerializedName("restaurant")
	val restaurant: Restaurant,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

// Data class untuk merepresentasikan satu item review dari customer
data class CustomerReviewsItem(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("review")
	val review: String,

	@field:SerializedName("name")
	val name: String
)

// Data class untuk merepresentasikan data restaurant lengkap
data class Restaurant(

	@field:SerializedName("customerReviews")
	val customerReviews: List<CustomerReviewsItem>,

	@field:SerializedName("pictureId")
	val pictureId: String,

	@field:SerializedName("name")
	val name: String,

	// rating menggunakan tipe Any karena bisa berupa String atau Number dari API
	@field:SerializedName("rating")
	val rating: Any,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("id")
	val id: String
)

// Data class untuk response API saat POST review baru
// Server mengembalikan list review terbaru setelah review ditambahkan
data class PostReviewResponse(

    @field:SerializedName("customerReviews")
    val customerReviews: List<CustomerReviewsItem>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)