package com.example.mapboxappv8examples

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.mapboxappv8examples.databinding.FragmentCurrentLocationBinding
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style


class CurrentLocationFragment : Fragment(), PermissionsListener {

    private var _binding: FragmentCurrentLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapboxMap: MapboxMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(requireContext(), resources.getString(R.string.mapbox_access_token))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            val res = checkSelfPermission(requireContext(), android.Manifest.permission.READ_PHONE_STATE)

            if (res != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_PHONE_STATE), 123)
            } else {
                binding.mapView.onCreate(savedInstanceState)
            }
        }

        binding.mapView.getMapAsync { mapboxMap ->
            this.mapboxMap = mapboxMap

            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                enableLocationComponent(it)
            }

        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {


            val locationComponent = mapboxMap.locationComponent
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                    .build()
            )
            locationComponent.isLocationComponentEnabled = true
            locationComponent.renderMode = RenderMode.COMPASS
            locationComponent.cameraMode = CameraMode.TRACKING_GPS
        } else {
            val permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(requireActivity())
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(requireContext(), "User explanation", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxMap.getStyle { style -> enableLocationComponent(style) }
        } else {
            Toast.makeText(requireContext(), "Sem permissao", Toast.LENGTH_LONG)
                .show()
            requireActivity().finish()
        }
    }

}