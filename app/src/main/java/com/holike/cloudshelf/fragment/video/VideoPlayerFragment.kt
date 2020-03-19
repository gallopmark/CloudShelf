//package com.holike.cloudshelf.fragment.video
//
//import android.content.Context
//import android.media.AudioManager
//import android.os.Bundle
//import android.os.Handler
//import android.text.TextUtils
//import android.view.MotionEvent
//import android.view.View
//import android.widget.SeekBar
//import com.bumptech.glide.Glide
//import com.holike.cloudshelf.R
//import com.holike.cloudshelf.base.BaseFragment
//import com.holike.cloudshelf.util.FormatUtils
//import kotlinx.android.synthetic.main.fragment_video_player.*
////import tv.danmaku.ijk.media.player.IMediaPlayer
////import tv.lib.ijkplayer.IRenderView
//
//class VideoPlayerFragment : BaseFragment() {
//
//    companion object {
//        /**
//         * @param videoUrl 视频url
//         * @param thumbUrl 视频缩略图
//         */
//        fun newInstance(videoUrl: String?, thumbUrl: String?, title: String?): VideoPlayerFragment {
//            val bundle = Bundle().apply {
//                putString("videoUrl", videoUrl)
//                putString("thumbUrl", thumbUrl)
//                putString("title", title)
//            }
//            val fragment = VideoPlayerFragment()
//            fragment.arguments = bundle
//            return fragment
//        }
//    }
//
//    private var mVideoPrepared = false  //是否是否准备完毕
//    private var mHandler: Handler? = null
//    private var mAudioManager: AudioManager? = null
//    private var mShowing = false
//    private val sDefaultTimeout = 5000L
//
//    override fun getLayoutResourceId(): Int = R.layout.fragment_video_player
//
//    override fun init(view: View, savedInstanceState: Bundle?) {
//        val arg = arguments
//        arg?.let {
//            val videoUrl = it.getString("videoUrl")
//            val thumbUrl = it.getString("thumbUrl")
//            titleTextView.text = it.getString("title")
//            initPlayer(thumbUrl, videoUrl)
//        }
//    }
//
//    private fun initPlayer(thumbUrl: String?, videoUrl: String?) {
//        //https://file.holike.com/miniprogram/test/video/4aa0637b-e062-4419-b9b8-f1d4daa08615.mp4
//        //https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583929172217&di=51a52f359c26b6de020fa1ebc392ac69&imgtype=0&src=http%3A%2F%2F00.minipic.eastday.com%2F20170426%2F20170426145543_d286b1d11013bff1d01c48c2a30b9586_10.jpeg
//        if (TextUtils.isEmpty(videoUrl)) return
//        videoView.setBufferingIndicator(indicatorView)
//        videoView.setThumbUrl(thumbUrl) { context, imageView, url ->
//            Glide.with(context).load(url).into(imageView)
//        }
//        videoView.setVideoPath(videoUrl)
//        videoView.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT)
//        videoView.setOnPreparedListener(mOnPreparedListener)
//        videoView.setOnBufferingUpdateListener(mOnBufferUpdateListener)
//        videoView.setOnCompletionListener(mOnCompleteListener)
//        videoView.setOnErrorListener(mOnErrorListener)
//        mHandler = Handler()
//        sbVideoProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar) {
//                if (mShowing) {
//                    mHandler?.removeCallbacks(mFadeOut)
//                }
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar) {
//                if (mShowing) {
//                    show()
//                }
//                if (videoView.isPlaying) {
//                    videoView.seekTo(seekBar.progress)
//                }
//            }
//        })
//        initAudio()
//        containerLayout.setOnTouchListener { _, ev ->
//            if (ev.action == MotionEvent.ACTION_DOWN) {
//                if (mVideoPrepared && !mShowing) {
//                    show()
//                }
//            }
//            false
//        }
//        playStateImageView.setOnClickListener {
//            if (mShowing) {
//                show()
//            }
//            if (videoView.isPlaying) {
//                pause()
//            } else {
//                start()
//            }
//        }
//        playCenterView.setOnClickListener {
//            playCenterView.visibility = View.GONE
//            initPlayer(thumbUrl, videoUrl)
//        }
//    }
//
//    private val mOnPreparedListener: IMediaPlayer.OnPreparedListener =
//            IMediaPlayer.OnPreparedListener {
//                mVideoPrepared = true
//                start()
//                mHandler?.post(mShowProgress)
//            }
//
//    private val mOnBufferUpdateListener = IMediaPlayer.OnBufferingUpdateListener { _, percent ->
//        val secondaryProgress = (videoView.duration * (percent / 100f)).toInt()
//        sbVideoProgress.secondaryProgress = secondaryProgress
//    }
//
//    private val mOnCompleteListener = IMediaPlayer.OnCompletionListener { onComplete() }
//
//    private val mOnErrorListener = IMediaPlayer.OnErrorListener { _, _, _ ->
//        showLongToast(R.string.video_loading_failed)
//        onComplete()
//        true
//    }
//
//    private val mShowProgress: Runnable = object : Runnable {
//        override fun run() {
//            if (videoView.isPlaying) {
//                val currentPosition = videoView.currentPosition
//                val duration = videoView.duration
//                val timerText = "${FormatUtils.generateTime(currentPosition.toLong())}/${FormatUtils.generateTime(duration.toLong())}"
//                timerTextView.text = timerText
//                sbVideoProgress.max = duration
//                sbVideoProgress.progress = currentPosition
//                mHandler?.postDelayed(this, (1000 - (currentPosition % 1000)).toLong())
//            }
//        }
//    }
//
//    private fun initAudio() {
//        mAudioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        mAudioManager?.let {
//            //获取系统最大音量
//            val maxVolume = it.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
//            sbVoiceProgress.max = maxVolume
//            //获取当前音量
//            val currentVolume = it.getStreamVolume(AudioManager.STREAM_MUSIC)
//            sbVoiceProgress.progress = currentVolume
//        }
//        sbVoiceProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                if (fromUser) {
//                    //设置系统音量
//                    mAudioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
//                }
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                if (mShowing) {
//                    mHandler?.removeCallbacks(mFadeOut)
//                }
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                if (mShowing) {
//                    show()
//                }
//            }
//        })
//    }
//
//    private val mFadeOut = Runnable { hide() }
//
//    private fun show() {
//        titleTextView.visibility = View.VISIBLE
//        controllerLayout.visibility = View.VISIBLE
//        mHandler?.removeCallbacks(mShowProgress)
//        mHandler?.post(mShowProgress)
//        mHandler?.removeCallbacks(mFadeOut)
//        mHandler?.postDelayed(mFadeOut, sDefaultTimeout)
//        mShowing = true
//    }
//
//    private fun hide() {
//        titleTextView.visibility = View.GONE
//        controllerLayout.visibility = View.GONE
//        mHandler?.removeCallbacks(mShowProgress)
//        mShowing = false
//    }
//
//    //播放
//    private fun start() {
//        videoView.start()
//        playStateImageView.setImageResource(R.mipmap.ic_videos_suspend)
//    }
//
//    //暂停
//    private fun pause() {
//        videoView.pause()
//        playStateImageView.setImageResource(R.mipmap.ic_videos_play)
//    }
//
//    //播放完毕或播放出错
//    private fun onComplete() {
//        hide()
//        playStateImageView.setImageResource(R.mipmap.ic_videos_play)
//        playCenterView.visibility = View.VISIBLE
//        mVideoPrepared = false
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (mVideoPrepared) {
//            start()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        if (mVideoPrepared) {
//            pause()
//        }
//    }
//
//    override fun onDestroyView() {
//        mHandler?.removeCallbacksAndMessages(null)
//        mHandler = null
//        videoView.stopPlayback()
//        super.onDestroyView()
//    }
//}