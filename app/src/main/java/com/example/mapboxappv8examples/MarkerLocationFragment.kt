package com.example.mapboxappv8examples

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.example.mapboxappv8examples.databinding.FragmentMarkerLocationBinding
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.Property.NONE
import com.mapbox.mapboxsdk.style.layers.Property.VISIBLE
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource


class MarkerLocationFragment : Fragment() {

    private var _binding: FragmentMarkerLocationBinding? = null
    private val binding get() = _binding!!

    private val DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(requireContext(), resources.getString(R.string.mapbox_access_token))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkerLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.mapView.getMapAsync { mapboxMap ->

            val casa = LatLng(-21.220721508825534, -50.41046343061402)
            val iconSize: Float = 0.30.toFloat()


            mapboxMap.cameraPosition = CameraPosition.Builder().target(casa).build()

            mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->

                style.addImage("dropped-icon-image", BitmapFactory.decodeResource(resources, R.drawable.red_marker))
                style.addSource(GeoJsonSource("dropped-marker-source-id"))
                style.addLayer(SymbolLayer(DROPPED_MARKER_LAYER_ID, "dropped-marker-source-id").withProperties(
                    iconImage("dropped-icon-image"),
                    visibility(NONE),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true),
                    iconSize(iconSize)
                ))


                style.getLayer(DROPPED_MARKER_LAYER_ID)?.also { layer ->
                    val source = style.getSourceAs<GeoJsonSource>("dropped-marker-source-id")

                    source?.also { geoJsonSource ->

                        geoJsonSource.setGeoJson(Point.fromLngLat(casa.longitude, casa.latitude))
                    }

                    val droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID)

                    droppedMarkerLayer?.setProperties(visibility(VISIBLE))

                }
            }
        }
    }
}