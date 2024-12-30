package com.tuluss.mobileparkingsystem20

data class Data(
    val namaTempat: String = "",
    val kapasitasMobil: Int? = null,
    val kapasitasMotor: Int? = null,
    val idLokasi: String? = null,
    val harga: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val id_parkiran: String = ""
)