package com.tuluss.mobileparkingsystem20

import android.Manifest
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
                        mMap.addMarker(MarkerOptions().position(myLoc).title("${getName}, ${city}"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc))
                    }
                } else {
                    Toast.makeText(this, "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
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
        var addressList: List<Address>? = null

        try {
            // Mengambil daftar lokasi berdasarkan nama
            addressList = geocoder.getFromLocationName(location, 1)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error mendapatkan lokasi: ${e.message}", Toast.LENGTH_SHORT).show()
            return
        }

        if (addressList.isNullOrEmpty()) {
            Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        // Mengambil alamat pertama dari daftar
        val address = addressList[0]
        val latLng = LatLng(address.latitude, address.longitude)

        // Menambahkan marker di lokasi hasil pencarian
        mMap.addMarker(MarkerOptions().position(latLng).title(location))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))

        // Menampilkan alamat lengkap di `rvAddress`
        binding.rvAddress.setText(address.getAddressLine(0))
    }

}