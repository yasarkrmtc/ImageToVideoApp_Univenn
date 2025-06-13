package com.imagetovideoapp.ui.imagetovideo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.imagetovideoapp.R
import com.imagetovideoapp.base.BaseFragment
import com.imagetovideoapp.databinding.FragmentImageToVideoBinding
import com.imagetovideoapp.type.StatusEnum
import com.imagetovideoapp.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.core.view.WindowCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

@AndroidEntryPoint
class ImageToVideoFragment :
    BaseFragment<FragmentImageToVideoBinding>(FragmentImageToVideoBinding::inflate) {

    private val viewModel: ImageToVideoViewModel by viewModels()
    private var exoPlayer: ExoPlayer? = null
    private var isPlaying = false
    private var videoId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoId = arguments?.getString("videoId") ?: ""
        initObservers()
        setupClickListeners()
        viewModel.fetchUserVideos(StatusEnum.SUCCEEDED)
        makeNavigationBarTransparent()
        startSeekBarUpdate()
    }

    private fun makeNavigationBarTransparent() {
        activity?.window?.navigationBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.getInsetsController(activity?.window!!, activity?.window?.decorView!!).apply {
            isAppearanceLightNavigationBars = false
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { viewState ->
                    viewState.errorMessage?.let {
                        showAlert("${Constants.ERROR_MESSAGE_PREFIX} $it")
                    } ?: run {
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
    }

    private fun setupVideoView(videoUrl: String) {
        exoPlayer = ExoPlayer.Builder(requireContext()).build()

        val playerView = binding.videoView as PlayerView
        playerView.player = exoPlayer

        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()

        exoPlayer?.play()

        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    binding.playPauseButton.setImageResource(R.drawable.start_icon)
                    isPlaying = false
                }
            }
        })
    }

    private fun setupControls() {
        binding.playPauseButton.setOnClickListener {
            exoPlayer?.let {
                if (isPlaying) {
                    it.pause()
                    binding.playPauseButton.setImageResource(R.drawable.start_icon)
                } else {
                    it.play()
                    binding.playPauseButton.setImageResource(R.drawable.pause_icon)
                }
                isPlaying = !isPlaying
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    exoPlayer?.seekTo(progress.toLong())
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
                exoPlayer?.let { player ->
                    if (player.isPlaying) {
                        val currentPosition = player.currentPosition
                        binding.seekBar.progress = currentPosition.toInt()
                        binding.timeStamp.text = formatTime(currentPosition.toInt())
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
        return String.format(Constants.TIME_FORMAT, minutes, seconds)
    }

    private fun downloadVideo(videoUrl: String, context: Context) {
        try {
            val url = URL(videoUrl)
            val connection = url.openConnection()
            connection.connect()

            val inputStream: InputStream = connection.getInputStream()
            val fileName = "video_${videoId ?: "unknown"}.mp4"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName)

            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var len: Int
            while (inputStream.read(buffer).also { len = it } > 0) {
                outputStream.write(buffer, 0, len)
            }
            outputStream.close()
            inputStream.close()

            Toast.makeText(context, "${Constants.DOWNLOAD_TEXT}: ${file.absolutePath}", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, Constants.ERROR_DOWNLOAD_VIDEO_TEXT, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClickListeners() {
        binding.closeButton.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        binding.uploadButton.setOnClickListener {
            viewModel.viewState.value.itemList?.find { it.id == videoId }?.url?.let { videoUrl ->
                downloadVideo(videoUrl, requireContext())
            }
        }

        binding.shareButton.setOnClickListener {
            viewModel.viewState.value.itemList?.find { it.id == videoId }?.url?.let { videoUrl ->
                shareVideo(videoUrl)
            }
        }
    }

    private fun shareVideo(videoUrl: String) {
        val videoUri = Uri.parse(videoUrl)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "video/*"
            putExtra(Intent.EXTRA_STREAM, videoUri)
        }
        val chooser = Intent.createChooser(shareIntent, Constants.SHARE_TEXT)
        startActivity(chooser)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer?.release()
    }
}