package com.tayyib.savelocation

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.tayyib.savelocation.roomdb.Address
import com.tayyib.savelocation.viewModel.MapsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapsFragment : Fragment(),GoogleMap.OnMapLongClickListener {

    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private lateinit var addressObject: Address

    private lateinit var mMap: GoogleMap

    private lateinit var sharedPreferences: SharedPreferences
    var bool: Boolean?=null

    private lateinit var viewModel: MapsViewModel




    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {

                bool = sharedPreferences.getBoolean("bool",false)

                if(bool == false)
                {
                    googleMap.clear()
                    val userLocation = LatLng(location.latitude, location.longitude)

                    googleMap.addMarker(MarkerOptions().position(userLocation).title("Konumunuz"))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                    sharedPreferences.edit().putBoolean("bool",true).apply()
                }

            }
        }


        if(ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {

            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(requireView(), "konum izni gerekli", Snackbar.LENGTH_INDEFINITE)
                    .setAction("İzin Ver") {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

                    }.show()
            }

            else{

                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

            }



        }


        else
        {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(lastLocation != null)
            {
                val lastLatLng   = LatLng(lastLocation.latitude,lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng,15f))

            }

        }




    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        registerLauncher()
        sharedPreferences = requireContext().getSharedPreferences("com.example.myapplication",
            Context.MODE_PRIVATE
        )
        bool = false

        viewModel = ViewModelProvider(this).get(MapsViewModel::class.java)


    }

    private fun registerLauncher()
    {

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        {
                result->

            if(result)
            {
                if(ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if(lastLocation != null)
                    {
                        val lastLatLng   = LatLng(lastLocation.latitude,lastLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng,15f))



                    }
                }
            }
            else
            {
                Toast.makeText(requireContext(),"izin verilmedi", Toast.LENGTH_LONG).show()
            }

        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()

        lifecycleScope.launch {

            val address = getAddressFromLatLng(p0)
            if (address.isNotEmpty()) {
                println("maps fragment ${address}")

                // Adresi kullanıcıya gösterip kaydetmek isteyip istemediğini soran dialog
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Adres Bilgisi")
                builder.setMessage("Bu adresi kaydetmek istiyor musunuz? \n\n$address")
                builder.setPositiveButton("Evet") { dialog, which ->
                    // Adresi SharedPreferences içine kaydedin
                    viewModel.addDataSqlite(addressObject)



                    Toast.makeText(requireContext(), "Adres kaydedildi", Toast.LENGTH_LONG).show()
                }
                builder.setNegativeButton("Hayır") { dialog, which ->
                    dialog.dismiss()
                }
                builder.show()

                mMap.addMarker(MarkerOptions().position(p0).title(address))
            }
        }
    }

    private suspend fun getAddressFromLatLng(latLng: LatLng): String = withContext(Dispatchers.IO) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        var addressStr = ""
        try {
            val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addressList != null) {
                if (addressList.isNotEmpty()) {
                    println("maps fragmenttttt ${addressList.first()}")
                    val firstAddress = addressList.first()
                    val countryName = firstAddress.countryName ?: ""
                    val thoroughfare = firstAddress.thoroughfare ?: ""
                    val subThoroughfare = firstAddress.subThoroughfare ?: ""
                    val subLocality = firstAddress.subLocality ?: ""
                    val adminArea = firstAddress.adminArea ?: ""




                    addressStr = "$countryName $thoroughfare $subThoroughfare $subLocality $adminArea"

                    addressObject = Address(thoroughfare,subLocality,adminArea,subThoroughfare,countryName,addressStr)
                    println("adresstr ${addressStr}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        addressStr
    }
}