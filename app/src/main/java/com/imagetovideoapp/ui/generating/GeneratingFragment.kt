package com.imagetovideoapp.ui.generating

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.imagetovideoapp.base.BaseFragment
import com.imagetovideoapp.databinding.FragmentGeneratingBinding
import com.imagetovideoapp.type.StatusEnum
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


        videoId = arguments?.getString(Constants.VIDEO_ID_ARGUMENT_KEY) ?: ""
        viewModel.startPolling(videoId!!)
        observeProgress()
    }

    private fun observeProgress() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.progress.collect { response ->
                binding.progressBar.progress = response
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.status.collect { response ->
                if (response == StatusEnum.SUCCEEDED.name){
                    val action = GeneratingFragmentDirections.actionGeneratingFragmentToImageToVideoFragment(videoId!!)
                    findNavController().navigate(action)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collect { response ->
                if (!response.isNullOrEmpty()){
                    showAlert(response)
                }
            }
        }
    }
}