package com.tuluss.mobileparkingsystem20

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.tuluss.mobileparkingsystem20.databinding.ActivityKendaraanBinding

class KendaraanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKendaraanBinding
    private lateinit var rvAdapter: RvDataKendaraanAdapter
    private lateinit var rvRiwayat : RvDataRiwayatAdapter
    private lateinit var dataList : ArrayList<DataKendaraan>
    private lateinit var firebaseref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKendaraanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val email = intent.getStringExtra("userLogin").toString()
        dataList = ArrayList()

        rvRiwayat = RvDataRiwayatAdapter(dataList)

        rvAdapter = RvDataKendaraanAdapter(dataList,
            onButtonClick = { selectedData ->
                val intent = Intent(this, CheckOutActivity::class.java).apply {
                    putExtra("id", selectedData.id)
                    putExtra("emailUser", selectedData.emailUser)
                    putExtra("noKendaraan", selectedData.noKendaraan)
                    putExtra("harga", selectedData.hargaParkir)
                    putExtra("jenisKendaraan", selectedData.jenisKendaraan)
                    putExtra("lat", selectedData.lat)
                    putExtra("long", selectedData.long)
                    putExtra("namaParkiran", selectedData.namaParkiran)
                    putExtra("status", selectedData.status)
                    putExtra("userLogin", email)
                }
                startActivity(intent)
            },
            onItemClick = { selectedData ->
                val intent = Intent(this, MapsActivity::class.java).apply {
                    putExtra("latByKendaraan", selectedData.lat)
                    putExtra("longByKendaraan", selectedData.long)
                    putExtra("lokasiByKendaraan", selectedData.namaParkiran)
                    putExtra("userLogin", email)
                }
                startActivity(intent)
                Toast.makeText(this, "Ke Mapas: ${selectedData.lat}, ${selectedData.long}", Toast.LENGTH_SHORT).show()
            }
        )


        println("INI EMAIL USER DATA KENDARAAN ${email}")

        binding.rvDataKendaraan.layoutManager = LinearLayoutManager(this)
        binding.rvDataRiwayat.layoutManager = LinearLayoutManager(this)

        binding.rvDataRiwayat.adapter = rvRiwayat
        binding.rvDataKendaraan.adapter = rvAdapter
        getData(email)
    }

    private fun getData(email: String) {
        firebaseref = FirebaseDatabase.getInstance().getReference("dataParkir")

        firebaseref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                if (snapshot.exists()) {
                    for (dataSnap in snapshot.children) {
                        val data = dataSnap.getValue(DataKendaraan::class.java)
                        if (data != null && data.emailUser == email) {
                            dataList.add(data)
                            println("KENDARAAN ${data.noKendaraan} ${data.emailUser}")
                        }
                    }

                    val parkingSet = dataList.filter { it.status == "True" }
                    val riwayatSet = dataList.filter { it.status == "False" }
                    rvAdapter.updateData(parkingSet)
                    rvRiwayat.updateData(riwayatSet)
                    binding.rvDataKendaraan.visibility = View.VISIBLE
                    binding.rvDataRiwayat.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this@KendaraanActivity, "Data Kosong!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@KendaraanActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
