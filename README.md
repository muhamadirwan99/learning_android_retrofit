# Restaurant Review App

Aplikasi Android sederhana untuk menampilkan detail restaurant dan memberikan review menggunakan Retrofit API.

## 📱 Fitur Aplikasi

- **Tampilkan Detail Restaurant**: Menampilkan nama, gambar, dan deskripsi restaurant
- **List Review**: Menampilkan daftar review dari customer lain
- **Kirim Review**: Menambahkan review baru ke restaurant
- **Loading Indicator**: Menampilkan progress bar saat melakukan request API
- **Edge-to-Edge Display**: Tampilan modern yang memanfaatkan seluruh layar

## 🏗️ Arsitektur Aplikasi

### Struktur Package

```
com.dicoding.restaurantreview/
├── ui/
│   ├── MainActivity.kt          # Activity utama aplikasi
│   └── ReviewAdapter.kt         # Adapter untuk RecyclerView review
├── data/
│   ├── response/
│   │   └── RestaurantResponse.kt  # Data class untuk response API
│   └── retrofit/
│       ├── ApiConfig.kt         # Konfigurasi Retrofit
│       └── ApiService.kt        # Interface endpoint API
```

### Komponen Utama

#### 1. MainActivity.kt
Activity utama yang menangani:
- Inisialisasi RecyclerView untuk menampilkan list review
- Request API untuk mengambil data restaurant
- Mengirim review baru ke server
- Mengelola state loading
- Menyembunyikan keyboard setelah submit review

**Fungsi Penting:**
- `findRestaurant()`: Mengambil data restaurant dari API
- `setRestaurantData()`: Menampilkan informasi restaurant di UI
- `setReviewData()`: Mengisi RecyclerView dengan list review
- `postReview()`: Mengirim review baru ke server
- `showLoading()`: Menampilkan/menyembunyikan loading indicator

#### 2. ReviewAdapter.kt
Adapter untuk RecyclerView yang menampilkan list review dengan fitur:
- Menggunakan `ListAdapter` dengan `DiffUtil` untuk performa optimal
- Otomatis mendeteksi perubahan data dan update hanya item yang berubah
- View Binding untuk akses view yang type-safe

#### 3. ApiConfig.kt
Konfigurasi Retrofit untuk komunikasi API:
- **Logging Interceptor**: Menampilkan detail request/response di Logcat (hanya di mode DEBUG)
- **Base URL**: `https://restaurant-api.dicoding.dev/`
- **Gson Converter**: Konversi otomatis JSON ke Kotlin object

#### 4. ApiService.kt
Interface yang mendefinisikan endpoint API:

**GET /detail/{id}**
- Mengambil detail restaurant berdasarkan ID
- Response: `RestaurantResponse`

**POST /review**
- Mengirim review baru
- Parameter: id (restaurant), name (user), review (text)
- Response: `PostReviewResponse` dengan list review terbaru

#### 5. RestaurantResponse.kt
Data class untuk merepresentasikan response API:
- `RestaurantResponse`: Response GET detail restaurant
- `Restaurant`: Model data restaurant
- `CustomerReviewsItem`: Model data satu review
- `PostReviewResponse`: Response POST review baru

## 🛠️ Teknologi yang Digunakan

### Libraries & Dependencies

- **Retrofit**: HTTP client untuk komunikasi API REST
- **OkHttp Logging Interceptor**: Logging request/response untuk debugging
- **Gson**: Parsing JSON ke Kotlin object
- **Glide**: Loading dan caching gambar dari URL
- **RecyclerView**: Menampilkan list review secara efisien
- **ViewBinding**: Akses view tanpa findViewById (type-safe)
- **Material Components**: UI components dengan Material Design

## 🚀 Cara Menjalankan

### Prerequisites
- Android Studio (versi terbaru)
- JDK 8 atau lebih tinggi
- Android SDK (API Level 21+)
- Koneksi internet untuk API call

### Langkah-langkah

1. **Clone repository**
   ```bash
   git clone <repository-url>
   cd RestaurantReview
   ```

2. **Buka project di Android Studio**
   - File → Open → Pilih folder RestaurantReview

3. **Sync Gradle**
   - Android Studio akan otomatis sync dependencies
   - Atau klik "Sync Project with Gradle Files"

4. **Jalankan aplikasi**
   - Pilih emulator atau device fisik
   - Klik tombol Run (▶️)

## 📝 Catatan Pengembangan

### Konsep Penting

#### Retrofit & Asynchronous Call
Aplikasi ini menggunakan Retrofit dengan callback pattern (`enqueue()`):
- Request dilakukan secara asynchronous (tidak memblokir UI thread)
- Callback `onResponse()` dipanggil saat berhasil mendapat response
- Callback `onFailure()` dipanggil saat terjadi error (network, timeout, dll)

#### View Binding
Menggantikan `findViewById()` dengan binding otomatis:
```kotlin
// Tanpa View Binding
val textView = findViewById<TextView>(R.id.tvTitle)

// Dengan View Binding
binding.tvTitle.text = "Restaurant Name"
```

#### DiffUtil di RecyclerView
`ListAdapter` dengan `DiffUtil` membuat update list lebih efisien:
- Hanya item yang berubah yang di-refresh
- Animasi otomatis saat ada perubahan
- Performa lebih baik untuk list besar

#### Edge-to-Edge Display
`enableEdgeToEdge()` membuat konten sampai ke tepi layar:
- Window insets dihandle dengan `ViewCompat.setOnApplyWindowInsetsListener`
- Padding dinamis ditambahkan agar konten tidak tertutup status bar

## 🔧 Konfigurasi

### Mengganti Restaurant ID
Edit konstanta di `MainActivity.kt`:
```kotlin
private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
```

### Mengganti Base URL API
Edit base URL di `ApiConfig.kt`:
```kotlin
.baseUrl("https://restaurant-api.dicoding.dev/")
```

### Mengganti Nama Default User
Edit parameter name di method `postReview()` di `MainActivity.kt`:
```kotlin
val client = ApiConfig.getApiService().postReview(RESTAURANT_ID, "NamaAnda", review)
```

## 🐛 Debugging

### Melihat Log API
Log API hanya muncul di mode DEBUG. Untuk melihatnya:
1. Buka Logcat di Android Studio
2. Filter dengan tag "OkHttp"
3. Log akan menampilkan detail request/response

### Melihat Log Custom
Filter Logcat dengan tag "MainActivity" untuk melihat log custom:
```kotlin
Log.e(TAG, "onFailure: ${response.message()}")
```

## 📚 Resources

- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Glide Documentation](https://bumptech.github.io/glide/)
- [Android RecyclerView Guide](https://developer.android.com/guide/topics/ui/layout/recyclerview)
- [View Binding Guide](https://developer.android.com/topic/libraries/view-binding)
- [Material Components](https://material.io/develop/android)

## 📄 License

Proyek ini dibuat untuk tujuan pembelajaran.

## 👨‍💻 Pengembang

Dikembangkan sebagai bagian dari learning path Android Development.

---

**Happy Coding! 🎉**
