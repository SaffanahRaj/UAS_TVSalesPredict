package com.example.tvsalespredict.ui.prediksi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tvsalespredict.databinding.ItemPrediksiBinding
import com.example.tvsalespredict.model.TvSales
import java.text.NumberFormat
import java.util.*

class PrediksiAdapter(private val data: List<TvSales>) :
    RecyclerView.Adapter<PrediksiAdapter.PredictionViewHolder>() {

    inner class PredictionViewHolder(private val binding: ItemPrediksiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TvSales) {
            binding.tvTanggal.text = item.tanggal
            val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
            binding.tvNilai.text = formatter.format(item.nilai)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPrediksiBinding.inflate(inflater, parent, false)
        return PredictionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size
}

