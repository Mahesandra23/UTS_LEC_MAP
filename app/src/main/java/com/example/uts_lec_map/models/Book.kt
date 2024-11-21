package com.example.uts_lec_map.models

import android.os.Parcel
import android.os.Parcelable

data class Book(
    val judul: String = "",
    val penulis: String = "",
    val harga: Int = 0,
    val cover: String = "",
    val sinopsis: String = "",
    val isi_cerita: String = "",
    val isPurchased: Boolean = false // Tambahkan status pembelian
) : Parcelable {
    constructor(parcel: Parcel) : this(
        cover = parcel.readString() ?: "",
        harga = parcel.readInt(),
        judul = parcel.readString() ?: "",
        penulis = parcel.readString() ?: "",
        sinopsis = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cover)
        parcel.writeInt(harga)
        parcel.writeString(judul)
        parcel.writeString(penulis)
        parcel.writeString(sinopsis)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}
