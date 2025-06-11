package com.imagetovideoapp.ui.generating

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.imagetovideoapp.base.BaseFragment
import com.imagetovideoapp.databinding.FragmentGeneratingBinding
import com.imagetovideoapp.ui.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class GeneratingFragment :
    BaseFragment<FragmentGeneratingBinding>(FragmentGeneratingBinding::inflate) {

    private val viewModel: GeneratingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        requireActivity().window.statusBarColor = android.graphics.Color.TRANSPARENT
        requireActivity().window.navigationBarColor = android.graphics.Color.TRANSPARENT

        WindowCompat.getInsetsController(requireActivity().window, view)?.apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        val videoId = arguments?.getString("id") ?: return
        viewModel.startPolling(videoId)
        observeProgress()
    }

    private fun observeProgress() {
        lifecycleScope.launchWhenStarted {
            viewModel.progress.collectLatest { status ->
                binding.progressBar.progress = status.progress
                if (status.status == "SUCCEEDED") {
                    val action = GeneratingFragmentDirections.actionGeneratingFragmentToImageToVideoFragment()
                    findNavController().navigate(action)
                }
            }
        }
    }
}
