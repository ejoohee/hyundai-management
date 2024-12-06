package com.example.carapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.carapp.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var naverMap: NaverMap
    private lateinit var mapView: MapView
    private lateinit var gasImageView: ImageView
    private lateinit var parkingImageView: ImageView
    private lateinit var washImageView: ImageView
    private val markers = mutableListOf<Marker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // ImageView 초기화
        gasImageView = view.findViewById(R.id.offImageView)
        parkingImageView = view.findViewById(R.id.middleImageView)
        washImageView = view.findViewById(R.id.strongImageView)

        return view
    }
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        val initialPosition = LatLng(37.481048, 126.882556)
        val cameraPosition = CameraPosition(initialPosition, 16.0) // 16.0은 줌 레벨입니다. 필요에 따라 조정하세요.
        naverMap.cameraPosition = cameraPosition

        // 초기 위치에 마커 추가 (선택 사항)
        val marker = Marker()
        marker.position = initialPosition
        marker.map = naverMap

        // 주유소 버튼 클릭 시 마커 추가
        gasImageView.setOnClickListener {
            addMarker(37.482616, 126.875186) // 주유소 위치
        }

        // 주차장 버튼 클릭 시 마커 추가
        parkingImageView.setOnClickListener {
            addMarker(37.482992, 126.882481) // 주차장 위치
        }

        // 세차장 버튼 클릭 시 마커 추가
        washImageView.setOnClickListener {
            addMarker(37.477609, 126.878919) // 세차장 위치
//            addMarker()
        }
    }

    fun addMarker() {
        val marker = Marker()
        marker.position = LatLng(37.570559, 126.982993)
        marker.map = naverMap
    }

    private fun addMarker(lat: Double, lng: Double) {
        // 기존 마커를 제거
        for (marker in markers) {
            marker.map = null
        }
        markers.clear()

        // 새로운 마커 추가
        val marker = Marker()
        marker.position = LatLng(lat, lng)
        marker.map = naverMap
        markers.add(marker)

        // 카메라를 해당 위치로 이동 (선택 사항)
        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(lat, lng)))
    }



    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}