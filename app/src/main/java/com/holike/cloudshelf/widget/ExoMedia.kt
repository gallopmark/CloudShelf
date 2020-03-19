package com.holike.cloudshelf.widget

import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.Surface
import cn.jzvd.JZMediaInterface
import cn.jzvd.Jzvd
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import com.holike.cloudshelf.R

//exoplayer
class ExoMedia(jzvd: Jzvd) : JZMediaInterface(jzvd), Player.EventListener, VideoListener {

    companion object {
        private const val TAG = "ExoMedia"
    }

    private var mExoPlayer: SimpleExoPlayer? = null
    private var mRunBuffering: BufferingUpdateTask? = null
    private var previousSeek: Long = 0


    override fun start() {
        mExoPlayer?.playWhenReady = true
    }

    override fun prepare() {
        Log.e(TAG, "prepare")
        val context = jzvd.context
        release()
        handler = Handler()
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory()
        val trackSelector: TrackSelector = DefaultTrackSelector(context, videoTrackSelectionFactory)

        // 2. Create the player
        val loadControl: LoadControl = DefaultLoadControl.Builder()
                .setAllocator(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
                .setBufferDurationsMs(360000, 600000, 1000, 5000)
                .setTargetBufferBytes(C.LENGTH_UNSET)
                .setPrioritizeTimeOverSizeThresholds(false).createDefaultLoadControl()
        mExoPlayer = SimpleExoPlayer.Builder(context, DefaultRenderersFactory(context))
                .setTrackSelector(trackSelector).setLoadControl(loadControl).build().apply {
                    val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context,
                            Util.getUserAgent(context, context.resources.getString(R.string.app_name)))
                    val currUrl = jzvd.jzDataSource.currentUrl.toString()
                    val videoSource: MediaSource
                    videoSource = if (!TextUtils.isEmpty(currUrl) && currUrl.contains(".m3u8")) {
                        HlsMediaSource.Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(currUrl))
                    } else {
                        ProgressiveMediaSource.Factory(dataSourceFactory)
                                .createMediaSource(Uri.parse(currUrl))
                    }
                    addVideoListener(this@ExoMedia)
                    addListener(this@ExoMedia)
                    repeatMode = if (jzvd.jzDataSource.looping) {
                        Player.REPEAT_MODE_ONE
                    } else {
                        Player.REPEAT_MODE_OFF
                    }
                    prepare(videoSource)
                    playWhenReady = true
                    setVideoSurface(Surface(jzvd.textureView.surfaceTexture))
                }
        mRunBuffering = BufferingUpdateTask()
    }


    override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
        handler?.post { jzvd.onVideoSizeChanged(width, height) }
    }

    override fun onRenderedFirstFrame() {
        Log.e(TAG, "onRenderedFirstFrame")
    }

    override fun pause() {
        mExoPlayer?.playWhenReady = false
    }

    override fun isPlaying(): Boolean {
        val player = mExoPlayer ?: return false
        return player.playWhenReady
    }

    override fun seekTo(time: Long) {
        if (time != previousSeek) {
            mExoPlayer?.seekTo(time)
            previousSeek = time
            jzvd.seekToInAdvance = time
        }
    }

    override fun release() {
        mExoPlayer?.let {
            it.release()
            SAVED_SURFACE = null
            mExoPlayer = null
        }
    }

    override fun getCurrentPosition(): Long {
        val player = mExoPlayer
        return player?.currentPosition ?: 0
    }

    override fun getDuration(): Long {
        val player = mExoPlayer
        return player?.duration ?: 0
    }

    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        mExoPlayer?.let {
            it.volume = leftVolume
            it.volume = rightVolume
        }
    }

    override fun setSpeed(speed: Float) {
        val playbackParameters = PlaybackParameters(speed, 1.0f)
        mExoPlayer?.setPlaybackParameters(playbackParameters)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_IDLE -> {
            }
            Player.STATE_BUFFERING -> {
                mRunBuffering?.let { handler?.post(it) }
            }
            Player.STATE_READY -> {
                handler?.post {
                    if (playWhenReady) {
                        jzvd.onStatePlaying()
                    }
                }
            }
            Player.STATE_ENDED -> {
                handler?.post { jzvd.onAutoCompletion() }
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        handler?.post { jzvd.onError(1000, 1000) }
    }

    override fun onPositionDiscontinuity(reason: Int) {}

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}

    override fun onSeekProcessed() {
        handler?.post { jzvd.onSeekComplete() }
    }

    override fun setSurface(surface: Surface?) {
        mExoPlayer?.setVideoSurface(surface)
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        if (SAVED_SURFACE == null) {
            SAVED_SURFACE = surface
            prepare()
        } else {
            jzvd.textureView.surfaceTexture = SAVED_SURFACE
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}

    private inner class BufferingUpdateTask : Runnable {
        override fun run() {
            mExoPlayer?.let { jzvd.setBufferProgress(it.bufferedPercentage) }
        }
    }
}