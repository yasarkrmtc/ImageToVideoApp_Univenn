package com.imagetovideoapp.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import clickWithDebounce
import com.imagetovideoapp.base.BaseFragment
import com.imagetovideoapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private var selectedImageUri: Uri? = null


    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.uploadImage.load(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        collectFlows()

        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        requireActivity().window.statusBarColor = android.graphics.Color.TRANSPARENT
        requireActivity().window.navigationBarColor = android.graphics.Color.TRANSPARENT

        WindowCompat.getInsetsController(requireActivity().window, view)?.apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }



    }


    private fun setupListeners() {
        binding.uploadArea.clickWithDebounce {
            pickImageLauncher.launch("image/*")
        }

        binding.generateButton.setOnClickListener {
            val prompt = binding.promptEditText.text?.toString()
            selectedImageUri?.let {
                viewModel.generateVideo(it, prompt)
            }
        }
    }

    private fun collectFlows() {
        lifecycleScope.launchWhenStarted {
            viewModel.videoId.collectLatest { videoId ->
                Toast.makeText(requireContext(), "Video ID: $videoId", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
