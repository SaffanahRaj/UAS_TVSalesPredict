package com.example.tvsalespredict.ui.dataset

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tvsalespredict.R
import java.io.BufferedReader
import java.io.InputStreamReader

class DatasetFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DatasetAdapter
    private val datasetList = mutableListOf<List<String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_dataset, container, false)

        // Set judul, deskripsi, dan link
        view.findViewById<TextView>(R.id.tvDatasetTitle).text = getString(R.string.dataset_title)
        view.findViewById<TextView>(R.id.tvDatasetDescription).text = getString(R.string.dataset_description)

        val linkView = view.findViewById<TextView>(R.id.tvDatasetLink)
        linkView.text = getString(R.string.dataset_link_text)
        linkView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.dataset_link_text)))
            startActivity(intent)
        }

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.rvDataset)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DatasetAdapter(datasetList)
        recyclerView.adapter = adapter

        loadCSVFromAssets()

        return view
    }

    private fun loadCSVFromAssets() {
        val inputStream = resources.assets.open("Date and model wise sale.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.useLines { lines ->
            lines.drop(1).take(10).forEach { line ->
                val row = line.split(",").map { it.trim() }
                if (row.size >= 3) {
                    datasetList.add(row.take(3))
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

}
