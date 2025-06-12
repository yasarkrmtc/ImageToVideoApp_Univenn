package com.imagetovideoapp.ui.imagetovideo

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.imagetovideoapp.R
import com.imagetovideoapp.base.BaseFragment
import com.imagetovideoapp.databinding.FragmentImageToVideoBinding
import com.imagetovideoapp.type.StatusEnum
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
        videoId = arguments?.getString("videoId") ?: ""
        initObservers()
        setupClickListeners()
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { viewState ->
                    if (viewState.errorMessage != null) {
                        showAlert(viewState.errorMessage)
                    } else {

                        viewState.itemList?.let { demoResponses ->
                            binding.progressBar.visibility = View.GONE
                            val video = demoResponses.find { it.id == videoId }
                            video?.url?.let { setupVideoView(it) }
                            setupControls()
                        }
                    }
                }
            }
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

    }



    private fun setupVideoView(videoUrl: String) {
        val videoUri = Uri.parse(videoUrl)
        binding.videoView.setVideoURI(videoUri)

        binding.videoView.setOnPreparedListener { mediaPlayer ->
            this.mediaPlayer = mediaPlayer
            binding.seekBar.max = mediaPlayer.duration
            mediaPlayer.setOnCompletionListener {
                binding.playPauseButton.setImageResource(R.drawable.start_icon)
                isPlaying = false
                timer?.cancel()
            }

            binding.videoView.start()
            startSeekBarUpdate()
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
                    binding.timeStamp.text = formatTime(progress)
                    if (!isPlaying) {
                        mediaPlayer?.start()
                        binding.playPauseButton.setImageResource(R.drawable.pause_icon)
                        isPlaying = true
                        startSeekBarUpdate()
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }



    private fun startSeekBarUpdate() {
        val handler = Handler(Looper.getMainLooper())
        val updateSeekBarRunnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        val currentPosition = mp.currentPosition
                        binding.seekBar.progress = currentPosition
                        binding.timeStamp.text = formatTime(currentPosition)
                    }
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(updateSeekBarRunnable, 1000)
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    private fun setupClickListeners() {
        binding.closeButton.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }


        binding.shareButton.setOnClickListener {
        }

        binding.uploadButton.setOnClickListener {
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer = null
        timer?.cancel()
    }


}
