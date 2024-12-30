package com.tuluss.mobileparkingsystem20

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tuluss.mobileparkingsystem20.databinding.RvRiwayatParkirBinding

class RvDataRiwayatAdapter(private var dataList: ArrayList<DataKendaraan>) :
    RecyclerView.Adapter<RvDataRiwayatAdapter.ViewHolder>() {

    class ViewHolder(private val binding: RvRiwayatParkirBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataKendaraan) {
            binding.apply {
                tvRPlatNomor.text = data.noKendaraan ?: "Tidak diketahui"
                tvRJenisKendaraan.text = data.jenisKendaraan ?: "Tidak diketahui"
                tvRLokasi.text = data.namaParkiran ?: "Tidak diketahui"
                val iconRes = when (data.jenisKendaraan?.lowercase()) {
                    "motor" -> R.drawable.fontisto_motorcycle
                    "Motor" -> R.drawable.fontisto_motorcycle
                    "mobil" -> R.drawable.mdi_car
                    "Mobil" -> R.drawable.mdi_car
                    else -> R.drawable.icon_park_parking
                }
                imgIconJenis.setImageResource(iconRes)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvRiwayatParkirBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    fun updateData(newDataList: List<DataKendaraan>) {
        dataList = newDataList as ArrayList<DataKendaraan>
        notifyDataSetChanged()
    }
}
