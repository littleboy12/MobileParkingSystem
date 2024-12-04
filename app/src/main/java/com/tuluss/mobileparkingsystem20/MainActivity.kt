package com.tuluss.mobileparkingsystem20

import android.content.Intent
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tuluss.mobileparkingsystem20.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var rvAdapter: RvDataAdapter
    private lateinit var dataList: ArrayList<Data>
    private lateinit var firebaseref: DatabaseReference
    private lateinit var myLocation : FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol navigasi ke MapsActivity


        // Inisialisasi RecyclerView
        dataList = ArrayList()
        rvAdapter = RvDataAdapter(dataList)
        binding.rvData.layoutManager = LinearLayoutManager(this)
        binding.rvData.adapter = rvAdapter

        // Ambil data dari Firebase
        getData()

        myLocation = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()


        binding.maps.setOnClickListener {
            val searchLocation = binding.editLocation.text.toString()
            val intent = Intent(this, MapsActivity::class.java)
            intent.apply {
                putExtra("search", searchLocation)
            }
            startActivity(intent)
        }

    }

    private fun getData() {
        firebaseref = FirebaseDatabase.getInstance().getReference("parkiran")

        firebaseref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                if (snapshot.exists()) {
                    for (dataSnap in snapshot.children) {
                        val data = dataSnap.getValue(Data::class.java)
                        if (data != null) {
                            dataList.add(data)
                        }
                    }
                    rvAdapter.notifyDataSetChanged()
                    binding.rvData.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this@MainActivity, "Data Kosong!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SuspiciousIndentation")
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            myLocation.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    val gecoder = Geocoder(this)

                    val address = gecoder.getFromLocation(latitude, longitude, 1)
                    if (address != null && address.isNotEmpty()) {
                        val address = address[0]
                        val city = address.subAdminArea
                        val country = address.subLocality

                        val getName = address.getAddressLine(0).split(" ").take(2).joinToString(" ")
                        binding.editLocation.setText("${getName}, ${city}")
                    }
                } else {
                    Toast.makeText(this, "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                getCurrentLocation()
//            } else {
//                Toast.makeText(this, "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
