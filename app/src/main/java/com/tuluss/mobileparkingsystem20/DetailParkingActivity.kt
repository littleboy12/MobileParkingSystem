package com.tuluss.mobileparkingsystem20

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tuluss.mobileparkingsystem20.databinding.ActivityDetailParkingBinding
import java.io.IOException
import java.util.UUID
import kotlin.system.exitProcess

class DetailParkingActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailParkingBinding
    lateinit var firebaseref: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        var userLogin = intent.getStringExtra("userLogin")
        super.onCreate(savedInstanceState)
        binding = ActivityDetailParkingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()
        getAddress()

        binding.rvViewParkir.setOnClickListener{
            binding.rvFormParkir.visibility = View.VISIBLE
        }

        binding.btnCancel.setOnClickListener(){
            binding.rvFormParkir.visibility = View.INVISIBLE
        }
        
        binding.btnSubmit.setOnClickListener{
            val noKendaraan = binding.edtNoKen.text.toString()
            val jenisKendaraan = binding.edtJenisKen.text.toString()

            if (noKendaraan.isEmpty()) {
                Toast.makeText(this, "Data no. Kendaraan Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (jenisKendaraan.isEmpty()) {
                Toast.makeText(this, "Data Jenis Kendaraan Tidak Boleh Kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            submitParkir(userLogin)
        }
    }

    private fun submitParkir(userLogin: String?) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val dataParkir: DatabaseReference = database.getReference("dataParkir")

        val id = "${UUID.randomUUID()}-${System.currentTimeMillis()}"
        val emailUser = userLogin.toString()
        val namaParkiran = intent.getStringExtra("namaTempat")
        val hargaParkir = intent.getIntExtra("biaya", 0)
        val lat = intent.getDoubleExtra("lat", 0.0)
        val long = intent.getDoubleExtra("long", 0.0)
        val noKendaraan = binding.edtNoKen.text.toString()
        val jenisKendaraan = binding.edtJenisKen.text.toString()
        val status = "True"
        val encodedeNoKend = noKendaraan.replace(".", "_").replace("@", "_").replace(" ", "_")
        val dataParkiran = HashMap<String, Any>()
        dataParkiran["id"] = id
        dataParkiran["emailUser"] = emailUser
        dataParkiran["namaParkiran"] = namaParkiran.toString()
        dataParkiran["hargaParkir"] = hargaParkir
        dataParkiran["lat"] = lat
        dataParkiran["long"] = long
        dataParkiran["noKendaraan"] = noKendaraan
        dataParkiran["jenisKendaraan"] = jenisKendaraan
        dataParkiran["status"] = status

        dataParkir.child(id).setValue(dataParkiran)
            .addOnSuccessListener {
                Toast.makeText(this@DetailParkingActivity, "KENDARAAN SUDAH TERDATA", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@DetailParkingActivity, MainActivity::class.java)
                intent.apply {
                    putExtra("email", emailUser)
                }
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@DetailParkingActivity, "Gagal menambahkan data: ${exception.message}", Toast.LENGTH_LONG).show()
            }

        println()
        println(" ${noKendaraan}  ${jenisKendaraan} ${emailUser}")
    }

    private fun getAddress() {
        val lat = intent.getDoubleExtra("lat", 0.0)
        val long = intent.getDoubleExtra("long", 0.0)

        val gecoder = Geocoder(this)
        val address = gecoder.getFromLocation(lat, long, 1)
        // Mengambil alamat pertama dari daftar
        if (address!= null && address.isNotEmpty()) {
            val address = address[0]
            val city = address.subAdminArea
            val country = address.subLocality
            val getName = address.getAddressLine(0).split(",").getOrNull(1)?.split(" ")?.take(3)?.joinToString(" ")


            print(address.getAddressLine(0))

            binding.rvAlamat.text = getName
        }

    }

    private fun getData() {
        val namaTempat = intent.getStringExtra("namaTempat")
        val maxMobil = intent.getIntExtra("mobil", 0)
        val maxMotor = intent.getIntExtra("motor", 0)
        val harga = intent.getIntExtra("biaya", 0)

        binding.rvNamaTempat.text = namaTempat
        binding.tvCarCapacity.text = maxMobil.toString()
        binding.rvMaxMotor.text = maxMotor.toString()
        binding.rvHarga.text = "Rp ${harga.toString()}"

        print(intent.getDoubleExtra("long", 0.0))
    }
}