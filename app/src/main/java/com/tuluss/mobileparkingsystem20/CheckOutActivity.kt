package com.tuluss.mobileparkingsystem20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tuluss.mobileparkingsystem20.databinding.ActivityCheckOutBinding

class CheckOutActivity : AppCompatActivity() {
    lateinit var binding : ActivityCheckOutBinding
    private lateinit var firebaseref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idKendaraan = intent.getStringExtra("id")

        binding.btnKeluar.setOnClickListener{
            updateData(idKendaraan)
        }
    }

    private fun updateData(idKendaraan: String?) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val dataParkir: DatabaseReference = database.getReference("dataParkir")



        val dataUser = intent.getStringExtra("userLogin").toString()
        val status = "False"
        val dataParkiran = HashMap<String, Any>()
        dataParkiran["id"] = idKendaraan.toString()
        dataParkiran["emailUser"] = intent.getStringExtra("emailUser").toString()
        dataParkiran["namaParkiran"] = intent.getStringExtra("namaParkiran").toString()
        dataParkiran["hargaParkir"] = intent.getIntExtra("harga", 0)
        dataParkiran["lat"] = intent.getDoubleExtra("lat", 0.0)
        dataParkiran["long"] = intent.getDoubleExtra("long", 0.0)
        dataParkiran["noKendaraan"] = intent.getStringExtra("noKendaraan").toString()
        dataParkiran["jenisKendaraan"] = intent.getStringExtra("jenisKendaraan").toString()
        dataParkiran["status"] = status

        dataParkir.child(idKendaraan.toString()).setValue(dataParkiran)
            .addOnSuccessListener {
                Toast.makeText(this@CheckOutActivity, "KENDARAAN SUDAH KELUAR", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@CheckOutActivity, KendaraanActivity::class.java)
                intent.apply {
                    putExtra("userLogin", dataUser)
                }
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@CheckOutActivity, "Gagal Mengubah data: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}