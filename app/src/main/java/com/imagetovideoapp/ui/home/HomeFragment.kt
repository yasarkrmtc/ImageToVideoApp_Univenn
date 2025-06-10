package com.imagetovideoapp.ui.home

import android.os.Bundle
import android.view.View
import com.imagetovideoapp.base.BaseFragment
import com.imagetovideoapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

class HomeFragment :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}