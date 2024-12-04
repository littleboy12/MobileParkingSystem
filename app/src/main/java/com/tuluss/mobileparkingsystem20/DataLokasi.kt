package com.tuluss.mobileparkingsystem20

data class DataLokasi(
    val id: String = "",
    val namaLokasi: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val keterangan: String? = ""
)