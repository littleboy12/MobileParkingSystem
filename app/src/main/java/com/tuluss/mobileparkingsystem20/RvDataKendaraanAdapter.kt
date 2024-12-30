package com.tuluss.mobileparkingsystem20

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tuluss.mobileparkingsystem20.databinding.RvKendaraanBinding

class RvDataKendaraanAdapter(private var dataList: ArrayList<DataKendaraan>, private val onItemClick: (DataKendaraan) -> Unit, private val onButtonClick: (DataKendaraan) -> Unit) :
    RecyclerView.Adapter<RvDataKendaraanAdapter.ViewHolder>() {

    class ViewHolder(private val binding: RvKendaraanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataKendaraan, onItemClick: (DataKendaraan) -> Unit,  onButtonClick: (DataKendaraan) -> Unit) {
            binding.apply {
                tvPlatNomor.text = data.noKendaraan ?: "Tidak diketahui"
                tvJenisKendaraan.text = data.jenisKendaraan ?: "Tidak diketahui"
                tvLokasi.text = data.namaParkiran ?: "Tidak diketahui"
                val iconRes = when (data.jenisKendaraan?.lowercase()) {
                    "motor" -> R.drawable.fontisto_motorcycle
                    "Motor" -> R.drawable.fontisto_motorcycle
                    "mobil" -> R.drawable.mdi_car
                    "Mobil" -> R.drawable.mdi_car
                    else -> R.drawable.icon_park_parking
                }
                imgIconJenis.setImageResource(iconRes)

                btnPoint.setOnClickListener{
                    onButtonClick(data)
                }

                root.setOnClickListener{
                    onItemClick(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvKendaraanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], onItemClick, onButtonClick)
    }

    fun updateData(newDataList: List<DataKendaraan>) {
        dataList = newDataList as ArrayList<DataKendaraan>
        notifyDataSetChanged()
    }
}
