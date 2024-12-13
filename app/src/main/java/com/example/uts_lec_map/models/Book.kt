package com.example.uts_lec_map.models

import android.os.Parcel
import android.os.Parcelable

// Model data Book yang digunakan untuk mendefinisikan objek Buku
// Data class ini berfungsi untuk menyimpan informasi tentang buku yang akan ditampilkan dalam aplikasi
data class Book(
    val id: String = "",  // ID unik untuk buku
    val judul: String = "",  // Judul buku
    val penulis: String = "",  // Penulis buku
    val harga: Int = 0,  // Harga buku
    val cover: String = "",  // URL atau path gambar sampul buku
    val sinopsis: String = "",  // Sinopsis buku
    val isi_cerita: String = "",  // Isi cerita buku (mungkin untuk keperluan lain)
    val isPurchased: Boolean = false // Status pembelian buku (apakah sudah dibeli atau belum)
) : Parcelable {

    // Konstruktor yang digunakan untuk membaca data dari Parcel dan menginisialisasi objek Book
    // Parcel adalah mekanisme Android untuk menyimpan objek dan mengirimnya antar komponen (seperti Activity atau Fragment)
    constructor(parcel: Parcel) : this(
        cover = parcel.readString() ?: "",  // Membaca string untuk cover buku, jika null, di-set menjadi string kosong
        harga = parcel.readInt(),  // Membaca harga buku
        judul = parcel.readString() ?: "",  // Membaca judul buku
        penulis = parcel.readString() ?: "",  // Membaca penulis buku
        sinopsis = parcel.readString() ?: ""  // Membaca sinopsis buku
    )

    // Fungsi untuk menulis objek Book ke dalam Parcel
    // Digunakan ketika objek ini perlu dipassing antar komponen (misalnya untuk navigasi atau penyimpanan sementara)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cover)  // Menulis string cover buku ke dalam Parcel
        parcel.writeInt(harga)  // Menulis harga buku ke dalam Parcel
        parcel.writeString(judul)  // Menulis judul buku ke dalam Parcel
        parcel.writeString(penulis)  // Menulis penulis buku ke dalam Parcel
        parcel.writeString(sinopsis)  // Menulis sinopsis buku ke dalam Parcel
    }

    // Fungsi untuk menggambarkan isi dari objek ini
    // Biasanya digunakan untuk operasi internal terkait pengelolaan objek yang terparcelable
    override fun describeContents(): Int {
        return 0  // Karena objek ini tidak memiliki deskripsi lebih lanjut, nilai yang dikembalikan adalah 0
    }

    // Companion object digunakan untuk menyediakan cara untuk membuat objek Book dari Parcel
    // CREATOR adalah objek yang digunakan Android untuk membaca data dari Parcel dan membuat objek Book
    companion object CREATOR : Parcelable.Creator<Book> {
        // Fungsi untuk membuat objek Book dari Parcel
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)  // Mengembalikan objek Book yang dibaca dari Parcel
        }

        // Fungsi untuk membuat array dari objek Book
        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)  // Membuat array kosong dengan ukuran yang diberikan
        }
    }
}
