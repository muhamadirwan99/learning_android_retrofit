# Restaurant Review App

Aplikasi Android sederhana untuk menampilkan detail restaurant dan memberikan review menggunakan Retrofit API.

## 📱 Fitur Aplikasi

- **Tampilkan Detail Restaurant**: Menampilkan nama, gambar, dan deskripsi restaurant
- **List Review**: Menampilkan daftar review dari customer lain dengan RecyclerView
- **Kirim Review**: Menambahkan review baru ke restaurant
- **Loading Indicator**: Menampilkan progress bar saat melakukan request API
- **Edge-to-Edge Display**: Tampilan modern yang memanfaatkan seluruh layar
- **MVVM Architecture**: Menggunakan ViewModel dan LiveData untuk state management

## 🏗️ Arsitektur Aplikasi

### Struktur Package

```
com.dicoding.restaurantreview/
├── ui/
│   ├── MainActivity.kt          # Activity utama aplikasi
│   ├── MainViewModel.kt         # ViewModel untuk state management
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
- Observasi LiveData dari ViewModel untuk auto-update UI
- Mengirim review baru ke server
- Mengelola state loading
- Menyembunyikan keyboard setelah submit review

**Fungsi Penting:**
- `onCreate()`: Inisialisasi ViewModel dan observer LiveData
- `setRestaurantData()`: Menampilkan informasi restaurant di UI
- `setReviewData()`: Mengisi RecyclerView dengan list review
- `showLoading()`: Menampilkan/menyembunyikan loading indicator

#### 2. MainViewModel.kt
ViewModel yang menangani logika bisnis dan state management:
- Request API untuk mengambil data restaurant
- Mengirim review baru ke server
- Menyimpan state (restaurant, list review, loading)
- Data bertahan saat configuration change (rotasi layar)

**LiveData yang diexpose:**
- `restaurant`: Data restaurant (nama, deskripsi, gambar)
- `listReview`: List review dari customer
- `isLoading`: Status loading untuk progress bar

**Fungsi Penting:**
- `findRestaurant()`: Mengambil data restaurant dari API
- `postReview()`: Mengirim review baru ke server

#### 3. ReviewAdapter.kt
Adapter untuk RecyclerView yang menampilkan list review dengan fitur:
- Menggunakan `ListAdapter` dengan `DiffUtil` untuk performa optimal
- Otomatis mendeteksi perubahan data dan update hanya item yang berubah
- View Binding untuk akses view yang type-safe

#### 4. ApiConfig.kt
Konfigurasi Retrofit untuk komunikasi API:
- **Logging Interceptor**: Menampilkan detail request/response di Logcat (hanya di mode DEBUG)
- **Base URL**: `https://restaurant-api.dicoding.dev/`
- **Gson Converter**: Konversi otomatis JSON ke Kotlin object

#### 5. ApiService.kt
Interface yang mendefinisikan endpoint API:

**GET /detail/{id}**
- Mengambil detail restaurant berdasarkan ID
- Response: `RestaurantResponse`

**POST /review**
- Mengirim review baru
- Parameter: id (restaurant), name (user), review (text)
- Response: `PostReviewResponse` dengan list review terbaru

#### 6. RestaurantResponse.kt
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
- **LiveData & ViewModel**: State management dengan lifecycle-aware
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

## 📝 Konsep Penting

### MVVM Architecture dengan LiveData

Aplikasi ini menggunakan MVVM (Model-View-ViewModel) pattern:

```
View (Activity) <--> ViewModel <--> Model (API/Repository)
       ↓                ↓
   UI Logic        Business Logic
```

**Kenapa MVVM?**
- ✅ Separation of Concerns: UI logic terpisah dari business logic
- ✅ Testable: ViewModel bisa di-test tanpa UI
- ✅ Lifecycle-aware: Data tidak hilang saat rotasi layar
- ✅ Reactive: UI otomatis update saat data berubah (LiveData)

**Flow data:**
1. User membuka app → `MainActivity.onCreate()`
2. MainActivity membuat `MainViewModel`
3. ViewModel otomatis call `findRestaurant()` di `init{}`
4. API response → ViewModel update `LiveData`
5. LiveData notify observer → MainActivity update UI

### LiveData Observer Pattern

```kotlin
// ViewModel expose data sebagai LiveData
val restaurant: LiveData<Restaurant> = _restaurant

// Activity observe perubahan data
mainViewModel.restaurant.observe(this) { restaurant ->
    setRestaurantData(restaurant)  // Auto-called saat data berubah
}
```

**Keuntungan:**
- Auto-update UI saat data berubah
- Lifecycle-aware: tidak akan crash jika Activity sudah destroyed
- Tidak perlu manual unsubscribe

### Retrofit & Asynchronous Call

Aplikasi ini menggunakan Retrofit dengan callback pattern (`enqueue()`):
- Request dilakukan secara asynchronous (tidak memblokir UI thread)
- Callback `onResponse()` dipanggil saat berhasil mendapat response
- Callback `onFailure()` dipanggil saat terjadi error (network, timeout, dll)

```kotlin
client.enqueue(object : Callback<RestaurantResponse> {
    override fun onResponse(...) {
        // Handle success - update LiveData
        _restaurant.value = responseBody.restaurant
    }
    override fun onFailure(...) {
        // Handle error - log atau tampilkan pesan error
        Log.e(TAG, "onFailure: ${t.message}")
    }
})
```

### View Binding

Menggantikan `findViewById()` dengan binding otomatis:
```kotlin
// ❌ Tanpa View Binding
val textView = findViewById<TextView>(R.id.tvTitle)
textView.text = "Restaurant Name"

// ✅ Dengan View Binding
binding.tvTitle.text = "Restaurant Name"
```

**Keuntungan:**
- Type-safe: compile error jika salah type
- Null-safe: tidak akan null pointer exception
- Lebih ringkas dan mudah dibaca

## 🔄 ListAdapter vs RecyclerView.Adapter

### Kapan Menggunakan ListAdapter?

**✅ GUNAKAN ListAdapter jika:**
1. **Data bisa berubah secara dinamis**
   - List yang bisa di-add, remove, atau update item
   - Contoh: Chat messages, shopping cart, review list

2. **Ingin animasi otomatis**
   - Item baru muncul dengan animasi slide
   - Item yang update berkedip
   - Item yang dihapus fade out

3. **Perlu performa optimal**
   - DiffUtil otomatis compare old vs new data
   - Hanya item yang berubah yang di-render ulang
   - Efisien untuk list besar (ratusan item)

4. **Sumber data dari API/Database**
   - Data yang di-fetch dari server bisa berubah
   - Real-time update (Firebase, WebSocket)

**Contoh di aplikasi ini:**
```kotlin
class ReviewAdapter : ListAdapter<CustomerReviewsItem, MyViewHolder>(DIFF_CALLBACK) {
    // DiffUtil otomatis detect perubahan
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)  // getItem() dari ListAdapter
        holder.bind(review)
    }
}

// Update data cukup panggil submitList()
adapter.submitList(newReviewList)  // DiffUtil akan compare & animate
```

### Kapan Menggunakan RecyclerView.Adapter?

**✅ GUNAKAN RecyclerView.Adapter jika:**
1. **Data static/tidak berubah**
   - List kategori yang fixed
   - Menu settings
   - Onboarding slides

2. **Data sangat sederhana**
   - Hanya beberapa item (< 10)
   - Tidak perlu update setelah pertama kali load

3. **Tidak butuh animasi perubahan**
   - Cukup dengan `notifyDataSetChanged()`

4. **Custom logic update yang kompleks**
   - Butuh kontrol penuh kapan dan bagaimana update
   - Multi-selection dengan checkbox

**Contoh RecyclerView.Adapter:**
```kotlin
class SimpleAdapter(private var items: List<String>) : 
    RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {
    
    override fun getItemCount() = items.size  // Harus implement manual
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])  // Akses langsung dari list
    }
    
    fun updateData(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()  // Refresh semua item (tidak efisien)
    }
}
```

### Perbandingan Detail

| Aspek | ListAdapter | RecyclerView.Adapter |
|-------|------------|---------------------|
| **Setup** | Perlu DiffUtil.ItemCallback | Lebih sederhana |
| **Update Data** | `submitList(newList)` | Manual `notifyXxx()` |
| **Performa** | ⚡ Optimal (DiffUtil) | ⚠️ Bisa lambat jika manual |
| **Animasi** | ✅ Otomatis | ❌ Manual atau tanpa animasi |
| **getItemCount()** | ✅ Otomatis | ❌ Harus implement |
| **Boilerplate** | Lebih banyak (DiffUtil) | Lebih sedikit |
| **Use Case** | Dynamic list | Static list |

### DiffUtil: Kunci Efisiensi ListAdapter

```kotlin
val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CustomerReviewsItem>() {
    // Cek apakah dua item adalah objek yang sama (compare by ID)
    override fun areItemsTheSame(oldItem: CustomerReviewsItem, newItem: CustomerReviewsItem): Boolean {
        return oldItem.name == newItem.name  // Atau compare by unique ID
    }
    
    // Cek apakah konten dari item berubah
    override fun areContentsTheSame(oldItem: CustomerReviewsItem, newItem: CustomerReviewsItem): Boolean {
        return oldItem == newItem  // Data class auto-compare semua field
    }
}
```

**Cara kerja DiffUtil:**
1. User post review baru
2. Server return list review terbaru (old list + new review)
3. `adapter.submitList(newList)` dipanggil
4. DiffUtil compare di background thread:
   - `areItemsTheSame()`: Cari item yang sama by ID
   - `areContentsTheSame()`: Cek apakah kontennya berubah
5. DiffUtil generate "diff" (perubahan apa saja)
6. RecyclerView update hanya item yang berubah dengan animasi

### Analogi dengan Flutter

Jika Anda familiar dengan Flutter, berikut analoginya:

| Android | Flutter | Penjelasan |
|---------|---------|------------|
| **RecyclerView** | `ListView.builder()` | Efficient list rendering |
| **ListAdapter** | `ListView.builder()` + key | Auto-diff dengan Key |
| **RecyclerView.Adapter** | `ListView()` statik | List sederhana tanpa builder |
| **DiffUtil** | `Key` di widget | Deteksi perubahan item |
| **submitList()** | `setState()` di List | Update list data |
| **notifyDataSetChanged()** | `setState()` full rebuild | Re-render semua item |
| **ViewHolder** | Widget di `itemBuilder` | Representasi satu item |

**Contoh Flutter Equivalent:**

```dart
// Android ListAdapter ≈ Flutter ListView.builder dengan Key
ListView.builder(
  itemCount: reviews.length,
  itemBuilder: (context, index) {
    final review = reviews[index];
    return ReviewItem(
      key: ValueKey(review.id),  // Seperti DiffUtil.areItemsTheSame()
      review: review,
    );
  },
)

// Android RecyclerView.Adapter ≈ Flutter ListView statik
ListView(
  children: [
    ReviewItem(review: review1),
    ReviewItem(review: review2),
  ],
)
```

**State Management di Flutter:**
- Android **LiveData** ≈ Flutter **Stream/ValueNotifier**
- Android **ViewModel** ≈ Flutter **ChangeNotifier/BLoC**
- Android **observe()** ≈ Flutter **StreamBuilder/ValueListenableBuilder**

**Contoh equivalen MVVM:**

```dart
// Android: mainViewModel.listReview.observe(this) { reviews -> updateUI() }

// Flutter dengan Provider:
Consumer<ReviewViewModel>(
  builder: (context, viewModel, child) {
    return ListView.builder(
      itemCount: viewModel.reviews.length,
      itemBuilder: (context, index) => ReviewItem(viewModel.reviews[index]),
    );
  },
)
```

### Kesimpulan: Mana yang Dipilih?

**Pilih ListAdapter jika:**
- ✅ Data dari API/Database (seperti di app ini)
- ✅ List bisa berubah (add/remove/update item)
- ✅ Peduli dengan performa dan animasi
- ✅ List besar (> 20 item)

**Pilih RecyclerView.Adapter jika:**
- ✅ Data static/fixed
- ✅ List kecil (< 10 item)
- ✅ Tidak perlu animasi
- ✅ Ingin simple dan cepat implementasi

**Untuk aplikasi modern, ListAdapter adalah best practice!** 🚀

## 🔧 Konfigurasi

### Mengganti Restaurant ID
Edit konstanta di `MainViewModel.kt`:
```kotlin
private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
```

### Mengganti Base URL API
Edit base URL di `ApiConfig.kt`:
```kotlin
.baseUrl("https://restaurant-api.dicoding.dev/")
```

### Mengganti Nama Default User
Edit parameter name di method `postReview()` di `MainViewModel.kt`:
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
Filter Logcat dengan tag "MainViewModel" atau "MainActivity" untuk melihat log custom:
```kotlin
Log.e(TAG, "onFailure: ${response.message()}")
```

## 📚 Resources

- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Glide Documentation](https://bumptech.github.io/glide/)
- [Android RecyclerView Guide](https://developer.android.com/guide/topics/ui/layout/recyclerview)
- [ListAdapter & DiffUtil](https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter)
- [View Binding Guide](https://developer.android.com/topic/libraries/view-binding)
- [LiveData Overview](https://developer.android.com/topic/libraries/architecture/livedata)
- [ViewModel Overview](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Material Components](https://material.io/develop/android)

## 💡 Best Practices yang Diterapkan

1. ✅ **MVVM Architecture**: Separation of concerns antara UI dan business logic
2. ✅ **LiveData**: Reactive UI yang lifecycle-aware
3. ✅ **ViewModel**: Data bertahan saat configuration change
4. ✅ **View Binding**: Type-safe view access
5. ✅ **ListAdapter**: Efficient list updates dengan DiffUtil
6. ✅ **Retrofit**: Modern HTTP client untuk API
7. ✅ **Glide**: Image loading dengan caching
8. ✅ **Edge-to-Edge**: Modern UI dengan system bars handling
9. ✅ **Loading States**: User feedback saat fetching data
10. ✅ **Keyboard Management**: Auto-hide keyboard untuk better UX

## 📄 License

Proyek ini dibuat untuk tujuan pembelajaran.

## 👨‍💻 Pengembang

Dikembangkan sebagai bagian dari learning path Android Development dengan fokus pada:
- RESTful API consumption dengan Retrofit
- MVVM architecture pattern
- LiveData & ViewModel
- RecyclerView dengan ListAdapter
- Modern Android UI/UX

---

**Happy Coding! 🎉**
