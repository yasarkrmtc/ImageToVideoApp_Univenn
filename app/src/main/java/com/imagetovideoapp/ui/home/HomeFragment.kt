package com.imagetovideoapp.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import java.io.InputStream

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
                val prompt = promptEditText.text?.toString()
                viewModel.viewState.value.selectedImage?.let {
                    viewModel.generateVideo(it, prompt)
                } ?: run {
                    showToast(Constants.PLEASE_UPLOAD_IMAGE_FIRST)
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
                    binding.progressBar.visibility = if (viewState.isLoading) View.VISIBLE else View.GONE
                    viewState.errorMessage?.takeIf { it.isNotEmpty() }?.let {
                        showAlert(it)
                    }

                    viewState.selectedImage?.let { selectedImage ->
                        binding.uploadArea.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.prompt_background)

                        binding.apply {
                            closeButton.visibility = View.VISIBLE
                            checkIcon.visibility = View.VISIBLE
                            val layoutParams = binding.uploadImage.layoutParams
                            layoutParams.width = dpToPx(40)
                            layoutParams.height = dpToPx(40)
                            binding.uploadImage.layoutParams = layoutParams
                            uploadImage.load(selectedImage)
                            uploadText.apply {
                                text = getText(R.string.uploaded)
                                textSize = 12f
                            }
                        }
                    } ?: run {
                        binding.uploadArea.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.outlined_background)

                        binding.apply {
                            val layoutParams = binding.uploadImage.layoutParams
                            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                            binding.uploadImage.layoutParams = layoutParams
                            closeButton.visibility = View.GONE
                            checkIcon.visibility = View.GONE
                            uploadImage.load(R.drawable.upload_icon)
                            uploadText.apply {
                                text = getText(R.string.tap_here_to_upload_image)
                                textSize = 14f
                            }
                        }
                    }

                    viewState.item?.let { demoResponses ->
                        binding.progressBar.visibility = View.GONE
                        val videoGenerationResult = demoResponses
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToGeneratingFragment(videoGenerationResult.id)
                        findNavController().navigate(action)
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
                val resizedBitmap = resizeImage(it)

                resizedBitmap?.let { bitmap ->
                    viewModel.setSelectedImage(bitmap)
                }
            }
        }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun resizeImage(uri: Uri): Bitmap? {
        val inputStream: InputStream? = context?.contentResolver?.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        val width = originalBitmap.width
        val height = originalBitmap.height

        if (width == height) {
            return Bitmap.createScaledBitmap(originalBitmap, 720, 720, false)
        }

        if (width > height) {
            return Bitmap.createScaledBitmap(originalBitmap, 1280, 720, false)
        }

        if (height > width) {
            return Bitmap.createScaledBitmap(originalBitmap, 720, 1280, false)
        }

        return null
    }
}