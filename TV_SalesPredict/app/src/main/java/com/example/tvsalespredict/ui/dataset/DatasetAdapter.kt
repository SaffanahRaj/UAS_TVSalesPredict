package com.example.tvsalespredict.ui.dataset

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tvsalespredict.R

class DatasetAdapter(private val items: List<List<String>>) :
    RecyclerView.Adapter<DatasetAdapter.DatasetViewHolder>() {

    inner class DatasetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvModel: TextView = view.findViewById(R.id.tvModel)
        val tvJumlah: TextView = view.findViewById(R.id.tvJumlah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatasetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dataset_card, parent, false)
        return DatasetViewHolder(view)
    }

    override fun onBindViewHolder(holder: DatasetViewHolder, position: Int) {
        val row = items[position]
        if (row.size >= 3) {
            holder.tvTanggal.text = row[0]
            holder.tvModel.text = row[1]
            holder.tvJumlah.text = row[2]
        }
    }

    override fun getItemCount(): Int = items.size
}
