package com.tuluss.mobileparkingsystem20

data class DataKendaraan(
    val id : String? = null,
    val emailUser : String? = null,
    val noKendaraan : String? = null,
    val hargaParkir : Int? = 0,
    val jenisKendaraan : String? = null,
    val lat : Double? = 0.0,
    val long : Double? = 0.0,
    val namaParkiran : String? = null,
    val status : String? = null,
)