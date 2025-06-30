package com.example.tvsalespredict.ui.prediksi

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tvsalespredict.databinding.FragmentPrediksiBinding
import com.example.tvsalespredict.model.TvSales
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PrediksiFragment : Fragment() {

    private var _binding: FragmentPrediksiBinding? = null
    private val binding get() = _binding!!
    private lateinit var tflite: Interpreter
    private lateinit var fullData: List<Float>

    // nilai min dan max untuk denormalisasi
    private val minValue = 0f   // ubah sesuai data asli
    private val maxValue = 1000f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPrediksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fullData = loadData("Date and model wise sale.csv")
        tflite = loadModel("tvSales.tflite")

        val last60 = fullData.takeLast(60)
        if (last60.size < 60) {
            binding.selectedDayText.text = "Data tidak cukup untuk prediksi!"
            return
        }

        val output = predictTVSales(last60)
        val tanggal = generateFutureDates("2025-06-29", output.size)

        val resultData = tanggal.zip(output).map { (tgl, nilai) -> TvSales(tgl, nilai) }

        binding.seekBar.max = resultData.size
        binding.seekBar.progress = 30
        binding.selectedDayText.text = "Jumlah Hari: 30"
        updateUI(resultData, 30)

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.selectedDayText.text = "Jumlah Hari: $progress"
                updateUI(resultData, progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateUI(data: List<TvSales>, days: Int) {
        val subData = data.take(days)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = PrediksiAdapter(subData)

        val entries = subData.mapIndexed { index, item -> Entry(index.toFloat(), item.nilai) }

        val dataSet = LineDataSet(entries, "Prediksi Penjualan").apply {
            color = resources.getColor(android.R.color.holo_blue_dark, null)
            valueTextSize = 10f
            setDrawCircles(false)
        }

        binding.lineChart.data = LineData(dataSet)
        binding.lineChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(subData.map { it.tanggal })
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            labelRotationAngle = -45f
            setDrawGridLines(false)
        }
        binding.lineChart.axisRight.isEnabled = false
        binding.lineChart.description.isEnabled = false
        binding.lineChart.invalidate()
    }

    private fun loadData(fileName: String): List<Float> {
        val list = mutableListOf<Float>()
        val reader = BufferedReader(InputStreamReader(requireContext().assets.open(fileName)))
        reader.readLine() // skip header
        reader.forEachLine { line ->
            val count = line.split(",").getOrNull(2)?.toFloatOrNull()
            if (count != null) list.add(count)
        }
        return list
    }

    private fun loadModel(modelFile: String): Interpreter {
        val modelStream = requireContext().assets.open(modelFile)
        val modelBytes = modelStream.readBytes()
        val buffer = ByteBuffer.allocateDirect(modelBytes.size).apply {
            order(ByteOrder.nativeOrder())
            put(modelBytes)
            rewind()
        }
        return Interpreter(buffer)
    }

    private fun predictTVSales(last60: List<Float>): List<Float> {
        val input = Array(1) { Array(60) { i -> FloatArray(1) { last60[i] } } }
        val output = Array(1) { FloatArray(120) } // prediksi 120 hari

        tflite.run(input, output)

        // denormalisasi
        return output[0].map { it * (maxValue - minValue) + minValue }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateFutureDates(start: String, count: Int): List<String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val baseDate = LocalDate.parse(start, formatter)
        return List(count) { baseDate.plusDays(it.toLong()).format(formatter) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
