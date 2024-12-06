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
import com.naver.maps.map.overlay.InfoWindow
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

        val initialPosition = LatLng(37.481048, 126.882556) // 초기 위치
        val cameraPosition = CameraPosition(initialPosition, 16.0) // 줌 레벨 16.0
        naverMap.cameraPosition = cameraPosition

        // 초기 위치에 마커 추가
        val marker = Marker()
        marker.position = initialPosition
        marker.map = naverMap

        // 마커에 "내 위치" 텍스트 추가
        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return "내 차 위치"
            }
        }
        infoWindow.open(marker)

        // 이미지뷰 클릭 이벤트에 따른 장소 데이터 추가
        gasImageView.setOnClickListener {
            showMarkers(getGasStationLocations())
        }

        parkingImageView.setOnClickListener {
            showMarkers(getParkingLocations())
        }

        washImageView.setOnClickListener {
            showMarkers(getCarWashLocations())
        }
    }

    // 장소 데이터를 제공하는 함수들
    private fun getGasStationLocations(): List<Pair<LatLng, String>> {
        return listOf(
            Pair(LatLng(37.482836, 126.875342), "S-OIL 구광주유소"),
            Pair(LatLng(37.481737, 126.887544), "해피차지 가산본점(전기차 급속충전소)")
        )
    }

    private fun getParkingLocations(): List<Pair<LatLng, String>> {
        return listOf(
            Pair(LatLng(37.483300, 126.882744), "가산디지털역환승 노상주차장"),
            Pair(LatLng(37.482687, 126.882744), "가산주차장"),
            Pair(LatLng(37.477471, 126.895610), "가산동공영주차장")
        )
    }

    private fun getCarWashLocations(): List<Pair<LatLng, String>> {
        return listOf(
            Pair(LatLng(37.478498, 126.880555), "세홍세차기"),
            Pair(LatLng(
                37.477795, 126.879300), "쎌차디테일링"),
            Pair(LatLng(37.478259, 126.889294), "스팀파워가산점")
        )
    }

//    fun addMarker() {
//        val marker = Marker()
//        marker.position = LatLng(37.570559, 126.982993)
//        marker.map = naverMap
//    }

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

        // 카메라를 해당 위치로 이동
        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(lat, lng)))
    }

    // 마커 표시 함수
    private fun showMarkers(locations: List<Pair<LatLng, String>>) {
        // 기존 마커 삭제
        for (marker in markers) {
            marker.map = null
        }
        markers.clear()

        // 새 마커 추가
        for ((location, name) in locations) {
            val marker = Marker()
            marker.position = location
            marker.map = naverMap

            // InfoWindow 추가
            val infoWindow = InfoWindow()
            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return name
                }
            }
            infoWindow.open(marker)

            markers.add(marker)
        }

        // 첫 번째 위치로 카메라 이동
        if (locations.isNotEmpty()) {
            naverMap.moveCamera(CameraUpdate.scrollTo(locations.first().first))
        }
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