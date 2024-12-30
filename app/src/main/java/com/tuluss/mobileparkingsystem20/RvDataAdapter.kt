package com.tuluss.mobileparkingsystem20

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tuluss.mobileparkingsystem20.databinding.RvDataItemBinding

class RvDataAdapter(private val dataList: ArrayList<Data>, private val onItemClick: (Data) -> Unit  ) : RecyclerView.Adapter<RvDataAdapter.ViewHolder>() {

    class ViewHolder(private val binding: RvDataItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data, onItemClick: (Data) -> Unit) {
            binding.apply {
                idNamaParkir.text = data.namaTempat
                idMaxMobil.text = data.kapasitasMobil.toString()
                idMaxMotor.text = data.kapasitasMotor.toString()
                idBiaya.text = "Rp ${data.harga.toString()}"

                root.setOnClickListener{
                    onItemClick(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvDataItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], onItemClick)
    }

    override fun getItemCount(): Int = dataList.size
}
