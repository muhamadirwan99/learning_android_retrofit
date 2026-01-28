package com.dicoding.restaurantreview.data.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.dicoding.restaurantreview.BuildConfig

// Class untuk konfigurasi Retrofit (library untuk komunikasi API/HTTP)
class ApiConfig {
    companion object  {
        fun getApiService(): ApiService {
            // Membuat interceptor untuk logging request/response HTTP
            // Interceptor ini akan menampilkan detail API call di Logcat untuk debugging
            val loggingInterceptor = if(BuildConfig.DEBUG) {
                // Jika mode DEBUG, tampilkan detail lengkap (body, header, dll)
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                // Jika mode RELEASE, tidak perlu logging untuk performa dan keamanan
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }

            // Membuat OkHttpClient dengan interceptor untuk handle HTTP request
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            // Membuat instance Retrofit dengan konfigurasi:
            // - Base URL: endpoint utama API
            // - Converter: untuk convert JSON response menjadi Kotlin object
            // - Client: OkHttpClient yang sudah dikonfigurasi
            val retrofit = Retrofit.Builder()
                .baseUrl("https://restaurant-api.dicoding.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            // Mengembalikan implementasi ApiService dari interface yang sudah didefinisikan
            return retrofit.create(ApiService::class.java)
        }
    }
}