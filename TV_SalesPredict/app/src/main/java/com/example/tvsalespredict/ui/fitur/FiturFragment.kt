package com.example.tvsalespredict.ui.fitur

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tvsalespredict.databinding.FragmentFiturBinding

class FiturFragment : Fragment() {

    private lateinit var binding: FragmentFiturBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFiturBinding.inflate(inflater, container, false)
        return binding.root
    }
}

