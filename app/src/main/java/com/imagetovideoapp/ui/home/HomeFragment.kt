package com.imagetovideoapp.ui.home

import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import clickWithDebounce
import com.imagetovideoapp.R
import com.imagetovideoapp.base.BaseFragment
import com.imagetovideoapp.databinding.FragmentHomeBinding
import com.imagetovideoapp.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
    }


    private fun initListeners() {
        binding.apply {
            uploadArea.clickWithDebounce {
                pickImageLauncher.launch(Constants.IMAGE_PICKER_TYPE)
            }
            generateButton.clickWithDebounce {
                val prompt = binding.promptEditText.text?.toString()
                viewModel.viewState.value.selectedImage?.let {
                    viewModel.generateVideo(it, prompt)
                }
            }
            closeButton.clickWithDebounce {
                viewModel.deleteImage()
            }
        }

    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { viewState ->
                    binding.progressBar.visibility =if (viewState.isLoading) View.VISIBLE else View.GONE
                    Log.e("ress","viesState : $viewState")
                     if (!viewState.errorMessage.isNullOrEmpty()) {
                        showAlert(viewState.errorMessage)
                    } else {
                        viewState.selectedImage?.let {
                            binding.apply {
                                closeButton.visibility = View.VISIBLE
                                checkIcon.visibility = View.VISIBLE
                                uploadImage.load(it)
                                uploadText.apply {
                                    text = getText(R.string.uploaded)
                                    textSize = 12f
                                }
                            }
                        }?:also {
                            binding.apply {
                                closeButton.visibility = View.GONE
                                checkIcon.visibility = View.GONE
                                uploadImage.load(R.drawable.upload_icon)
                                uploadText.apply {
                                    text = getText(R.string.tap_here_to_upload_image)
                                    textSize = 16f
                                }
                            }
                        }

                        viewState.item?.let { demoResponses ->
                            binding.progressBar.visibility = View.GONE
                            val videoGenerationResult = demoResponses
                            val action =
                                HomeFragmentDirections.actionHomeFragmentToGeneratingFragment(
                                    videoGenerationResult.id
                                )
                            findNavController().navigate(action)
                        }
                    }
                    }
                }
            }
        }
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetState()
    }


        private val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    viewModel.setSelectedImage(it)
                }
            }
    }
