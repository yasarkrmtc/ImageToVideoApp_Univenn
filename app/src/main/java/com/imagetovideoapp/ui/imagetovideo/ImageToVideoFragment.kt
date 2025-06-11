package com.imagetovideoapp.ui.imagetovideo

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.imagetovideoapp.R
import com.imagetovideoapp.base.BaseFragment
import com.imagetovideoapp.databinding.FragmentImageToVideoBinding
import com.imagetovideoapp.type.StatusEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@AndroidEntryPoint
class ImageToVideoFragment :
    BaseFragment<FragmentImageToVideoBinding>(FragmentImageToVideoBinding::inflate) {

    private val viewModel: ImageToVideoViewModel by viewModels()

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var timer: Timer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchUserVideos(StatusEnum.SUCCEEDED)
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        requireActivity().window.statusBarColor = android.graphics.Color.TRANSPARENT
        requireActivity().window.navigationBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.getInsetsController(requireActivity().window, view)?.apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        initObservers()

        setupVideoView()
        setupControls()
        setupClickListeners()
    }

    private fun initObservers(){
            lifecycleScope.launchWhenStarted {
                viewModel.userVideos.collectLatest { videos ->

                    Log.e("cccccccc",videos.toString())
                    if (videos.isNotEmpty()) {
                    } else {
                        binding.videoDescription.text = "No videos available"
                    }
                }
            }
    }

    private fun setupVideoView() {
        val videoUri = Uri.parse("android.resource://${requireContext().packageName}/raw/sample_video") // örnek video

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
            // Share logic goes here
        }

        binding.uploadButton.setOnClickListener {
            // Upload logic goes here
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
