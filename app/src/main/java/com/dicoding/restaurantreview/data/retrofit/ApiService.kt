package com.dicoding.restaurantreview.data.retrofit

import com.dicoding.restaurantreview.data.response.PostReviewResponse
import com.dicoding.restaurantreview.data.response.RestaurantResponse
import retrofit2.Call
import retrofit2.http.*

// Interface yang mendefinisikan endpoint API yang akan digunakan
// Retrofit akan membuat implementasi otomatis dari interface ini
interface ApiService {
    // Endpoint GET untuk mengambil detail restaurant berdasarkan ID
    // @Path("id") akan mengganti {id} di URL dengan parameter yang dikirim
    @GET("detail/{id}")
    fun getRestaurant(
        @Path("id") id : String
    ) : Call<RestaurantResponse>

    // Endpoint POST untuk mengirim review baru ke restaurant
    // @FormUrlEncoded: data dikirim dalam format form (bukan JSON)
    // @Headers: menambahkan header Authorization untuk autentikasi API
    @FormUrlEncoded
    @Headers("Authorization: token 12345")
    @POST("review")
    fun postReview(
        // @Field: parameter yang dikirim sebagai form field
        @Field("id") id: String,
        @Field("name") name: String,
        @Field("review") review: String,
    ) : Call<PostReviewResponse>
}