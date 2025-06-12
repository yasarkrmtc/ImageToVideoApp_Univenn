package com.imagetovideoapp.ui.generating

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.imagetovideoapp.base.BaseFragment
import com.imagetovideoapp.databinding.FragmentGeneratingBinding
import com.imagetovideoapp.domain.state.BaseResponse
import com.imagetovideoapp.ui.home.HomeFragmentDirections
import com.imagetovideoapp.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GeneratingFragment :
    BaseFragment<FragmentGeneratingBinding>(FragmentGeneratingBinding::inflate) {

    private val viewModel: GeneratingViewModel by viewModels()
    private var videoId :String?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        videoId = arguments?.getString(Constants.VIDEO_ID_ARGUMENT_KEY) ?: return
        viewModel.startPolling(videoId!!)
        observeProgress()
    }

    private fun observeProgress() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.progress.collect { response ->
                when (response) {
                    is BaseResponse.Loading -> {
                    }
                    is BaseResponse.Success -> {
                        val videoGenerationResult = response.data
                        val progress = videoGenerationResult.progress
                        binding.progressBar.progress = progress
                        if (progress == 100) {
                            val action = HomeFragmentDirections.actionHomeFragmentToGeneratingFragment(videoId!!)
                            findNavController().navigate(action)
                        }
                    }
                    is BaseResponse.Error -> {
                        showAlert(response.exception.message ?: Constants.UNKNOWN_ERROR_MESSAGE)
                    }
                }
            }
        }
    }
}
