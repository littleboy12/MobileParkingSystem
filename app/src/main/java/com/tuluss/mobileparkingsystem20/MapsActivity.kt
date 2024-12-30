package com.tuluss.mobileparkingsystem20

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tuluss.mobileparkingsystem20.databinding.ActivityMapsBinding
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var myLocation : FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        myLocation = LocationServices.getFusedLocationProviderClient(this)
//        searchLocation()
        val vSearch = intent.getStringExtra("search")
//
        binding.rvNamaTempat.setText("${vSearch}")


    }

    private fun setLocationKendaraan(latKend: Double, longKend: Double) {
        val geocoder = Geocoder(this)
        var addressList: List<Address>?
        val vLocation = intent.getStringExtra("lokasiByKendaraan")
        val location = vLocation?.trim()

        binding.rvNamaTempat.setText("${vLocation}")
        try {
            addressList = geocoder.getFromLocation(latKend, longKend, 1)
            if (addressList.isNullOrEmpty()) {
                Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
                return
            }
        } catch (ioException: IOException) {
            Toast.makeText(this, "Kesalahan jaringan: ${ioException.message}", Toast.LENGTH_SHORT).show()
            return
        } catch (exception: Exception) {
            Toast.makeText(this, "Kesalahan tidak terduga: ${exception.message}", Toast.LENGTH_SHORT).show()
            return
        }

        if (addressList.isNullOrEmpty()) {
            Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        // Mengambil alamat pertama dari daftar
        val address = addressList[0]
        val city = address.subAdminArea
        val getName = address.getAddressLine(0).split(" ").take(2).joinToString(" ")
        val latLng = LatLng(address.latitude, address.longitude)
        binding.btnSetMap.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.apply {
                putExtra("latituded", address.latitude)
                putExtra("longitude", address.longitude)
                putExtra("Lokasi", "${getName}, ${city}")
            }
            startActivity(intent)
        }

        // Menambahkan marker di lokasi hasil pencarian
        mMap.addMarker(MarkerOptions().position(latLng).title(location))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))

        // Menampilkan alamat lengkap di `rvAddress`
        binding.rvAddress.setText(address.getAddressLine(0))

        binding.btnSetMap.text = "Kembali"

        val userLogin = intent.getStringExtra("userLogin")
        binding.btnSetMap.setOnClickListener{
            val intent = Intent(this, KendaraanActivity::class.java)
            intent.apply {
                putExtra("userLogin", userLogin)
                println("MENGIRIM ${userLogin}")
            }
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MainActivity.LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            myLocation.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val myLoc = LatLng(latitude, longitude)
                    val gecoder = Geocoder(this)
                    val address = gecoder.getFromLocation(latitude, longitude, 1)
                    if (address != null && address.isNotEmpty()) {
                        val address = address[0]
                        val city = address.subAdminArea
                        val country = address.subLocality
                        val getName = address.getAddressLine(0).split(" ").take(2).joinToString(" ")
//                        mMap.addMarker(MarkerOptions().position(myLoc).title("${getName}, ${city}"))
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc))
                    }
                } else {
                    Toast.makeText(this, "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        val latKend = intent.getDoubleExtra("latByKendaraan", 0.0)
        val longKend = intent.getDoubleExtra("longByKendaraan", 0.0)
        if (latKend != 0.0) {
            setLocationKendaraan(latKend, longKend)
        }

        searchLocation()
    }

    fun searchLocation() {
        val vSearch = intent.getStringExtra("search")
        val location = vSearch?.trim()

        if (location.isNullOrEmpty()) {
            Toast.makeText(this, "Masukkan nama lokasi", Toast.LENGTH_SHORT).show()
            return
        }

        val geocoder = Geocoder(this)
        var addressList: List<Address>?

        try {
            addressList = geocoder.getFromLocationName(location, 1)
            if (addressList.isNullOrEmpty()) {
                Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
                return
            }
        } catch (ioException: IOException) {
            Toast.makeText(this, "Kesalahan jaringan: ${ioException.message}", Toast.LENGTH_SHORT).show()
            return
        } catch (exception: Exception) {
            Toast.makeText(this, "Kesalahan tidak terduga: ${exception.message}", Toast.LENGTH_SHORT).show()
            return
        }

        if (addressList.isNullOrEmpty()) {
            Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        // Mengambil alamat pertama dari daftar
        val address = addressList[0]
        val city = address.subAdminArea
        val getName = address.getAddressLine(0).split(" ").take(2).joinToString(" ")
        val latLng = LatLng(address.latitude, address.longitude)

        val userLogin = intent.getStringExtra("userLogin")
        binding.btnSetMap.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.apply {
                putExtra("latituded", address.latitude)
                putExtra("longitude", address.longitude)
                putExtra("Lokasi", "${getName}, ${city}")
                putExtra("email", userLogin)
            }
            startActivity(intent)
        }

        // Menambahkan marker di lokasi hasil pencarian
        mMap.addMarker(MarkerOptions().position(latLng).title(location))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))

        // Menampilkan alamat lengkap di `rvAddress`
        binding.rvAddress.setText(address.getAddressLine(0))
    }

}