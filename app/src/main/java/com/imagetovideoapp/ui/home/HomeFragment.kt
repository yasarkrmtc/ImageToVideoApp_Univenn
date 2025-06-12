package com.imagetovideoapp.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import clickWithDebounce
import com.imagetovideoapp.base.BaseFragment
import com.imagetovideoapp.databinding.FragmentHomeBinding
import com.imagetovideoapp.domain.state.BaseResponse
import com.imagetovideoapp.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
    }


    private fun initListeners() {
        binding.uploadArea.clickWithDebounce {
            pickImageLauncher.launch(Constants.IMAGE_PICKER_TYPE)
        }

        binding.generateButton.setOnClickListener {
            val prompt = binding.promptEditText.text?.toString()
            selectedImageUri?.let {
                viewModel.generateVideo(it, prompt)
            }
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.viewState.collect { response ->
                when (response) {
                    is BaseResponse.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is BaseResponse.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val videoGenerationResult = response.data
                        val action = HomeFragmentDirections.actionHomeFragmentToGeneratingFragment(videoGenerationResult.id)
                        findNavController().navigate(action)
                    }
                    is BaseResponse.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showAlert(response.exception.message ?: Constants.ALERT_UNKNOWN_ERROR_MESSAGE)
                    }
                }
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.uploadImage.load(it)
        }
    }
}
