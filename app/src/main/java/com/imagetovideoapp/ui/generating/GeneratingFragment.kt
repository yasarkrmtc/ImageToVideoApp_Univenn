package com.imagetovideoapp.ui.generating

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import com.imagetovideoapp.base.BaseFragment
import com.imagetovideoapp.databinding.FragmentGeneratingBinding

class GeneratingFragment :
    BaseFragment<FragmentGeneratingBinding>(FragmentGeneratingBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        requireActivity().window.statusBarColor = android.graphics.Color.TRANSPARENT
        requireActivity().window.navigationBarColor = android.graphics.Color.TRANSPARENT

        WindowCompat.getInsetsController(requireActivity().window, view)?.apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
}