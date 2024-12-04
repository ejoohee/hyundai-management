package com.example.carapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carapp.models.Car

class CarAdapter(private val onCarClick: (Car) -> Unit) :
    RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    private val cars = mutableListOf<Car>()

    fun submitList(newCars: List<Car>) {
        cars.clear()
        cars.addAll(newCars)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_car, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(cars[position])
    }

    override fun getItemCount() = cars.size

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val carImage: ImageView = itemView.findViewById(R.id.carImage)
        private val carName: TextView = itemView.findViewById(R.id.carName)
        private val carPrice: TextView = itemView.findViewById(R.id.carPrice)

        fun bind(car: Car) {
            // 이름과 가격 설정
            carName.text = car.name
            carPrice.text = "${car.price} 만원"

            // 이미지 로드 (리소스 ID)
            Glide.with(itemView.context)
                .load(car.imageResId)
                .into(carImage)

            // 아이템 클릭 리스너 설정
            itemView.setOnClickListener {
                onCarClick(car)
            }
        }
    }
}
