package com.dicoding.restaurantreview.util

/**
 * Event Wrapper Class untuk menangani one-time events di LiveData
 *
 * MASALAH yang dipecahkan:
 * LiveData bersifat "sticky" - value terakhir akan dikirim ulang ke observer baru
 * Contoh: User post review → Snackbar muncul → User rotasi layar → Snackbar muncul lagi
 * Ini terjadi karena observer di-create ulang saat rotasi dan menerima value terakhir
 *
 * SOLUSI:
 * Event wrapper dengan flag "hasBeenHandled" untuk track apakah event sudah di-consume
 * Saat observer baru (setelah rotasi) coba ambil content, akan return null jika sudah handled
 *
 * Kenapa perlu ini?
 * - Snackbar/Toast seharusnya one-time, tidak muncul berulang
 * - Navigation events seharusnya tidak trigger ulang saat rotasi
 * - Prevent duplicate actions (misal: payment tidak di-submit dua kali)
 *
 * @param T tipe data yang dibungkus (String untuk Snackbar message, Int untuk navigation, dll)
 */
open class Event<out T>(private val content: T) {

    // Flag untuk track apakah event sudah pernah di-handle
    // Kenapa private set? Agar hanya class Event yang bisa ubah flag ini
    @Suppress("MemberVisibilityCanBePrivate")
    var hasBeenHandled = false
        private set

    /**
     * Mengambil content jika belum pernah di-handle
     *
     * Return:
     * - content: jika ini pertama kali dipanggil (hasBeenHandled = false)
     * - null: jika sudah pernah dipanggil sebelumnya (hasBeenHandled = true)
     *
     * Kenapa return null saat sudah handled?
     * Agar observer tidak melakukan aksi apapun untuk event yang sudah lewat
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            // Event sudah di-handle sebelumnya, return null agar tidak re-trigger
            null
        } else {
            // Event belum di-handle, tandai sebagai handled dan return content
            hasBeenHandled = true
            content
        }
    }

    /**
     * Mengintip content tanpa mengubah status hasBeenHandled
     *
     * Kenapa perlu fungsi ini?
     * Untuk case dimana kita perlu cek value tanpa "menghabiskan" event
     * Misal: untuk logging atau testing
     */
    fun peekContent(): T = content
}