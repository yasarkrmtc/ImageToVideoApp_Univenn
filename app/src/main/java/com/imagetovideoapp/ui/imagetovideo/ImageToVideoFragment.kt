package com.imagetovideoapp.ui.imagetovideo

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.imagetovideoapp.R
import com.imagetovideoapp.base.BaseFragment
import com.imagetovideoapp.databinding.FragmentImageToVideoBinding
import com.imagetovideoapp.domain.state.BaseResponse
import com.imagetovideoapp.type.StatusEnum
import com.imagetovideoapp.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ImageToVideoFragment :
    BaseFragment<FragmentImageToVideoBinding>(FragmentImageToVideoBinding::inflate) {

    private val viewModel: ImageToVideoViewModel by viewModels()

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var timer: Timer? = null
    private var videoId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchUserVideos(StatusEnum.SUCCEEDED)
        initObservers()
        setupClickListeners()
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userVideos.collect { response ->
                when (response) {
                    is BaseResponse.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is BaseResponse.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val video = response.data.find { it.id == videoId }
                        video?.url?.let { setupVideoView(it) }
                        setupControls()
                    }
                    is BaseResponse.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showAlert(response.exception.message ?: Constants.ALERT_UNKNOWN_ERROR_MESSAGE)
                    }
                }
            }
        }
    }


    private fun setupVideoView(videoUrl: String) {
        val videoUri = Uri.parse(videoUrl)

        binding.videoView.setVideoURI(videoUri)
        binding.videoView.setOnPreparedListener {
            mediaPlayer = it
            binding.seekBar.max = it.duration
            it.setOnCompletionListener {
                binding.playPauseButton.setImageResource(R.drawable.start_icon)
                isPlaying = false
                timer?.cancel()
            }
        }
    }


    private fun setupControls() {
        binding.playPauseButton.setOnClickListener {
            mediaPlayer?.let {
                if (isPlaying) {
                    it.pause()
                    binding.playPauseButton.setImageResource(R.drawable.start_icon)
                    timer?.cancel()
                } else {
                    it.start()
                    binding.playPauseButton.setImageResource(R.drawable.pause_icon)
                    startSeekBarUpdate()
                }
                isPlaying = !isPlaying
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
                binding.timeStamp.text = formatTime(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun startSeekBarUpdate() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    mediaPlayer?.let {
                        binding.seekBar.progress = it.currentPosition
                        binding.timeStamp.text = formatTime(it.currentPosition)
                    }
                }
            }
        }, 0, 500)
    }

    private fun setupClickListeners() {
        binding.closeButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.shareButton.setOnClickListener {
        }

        binding.uploadButton.setOnClickListener {
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format(Constants.TIME_FORMAT, minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
