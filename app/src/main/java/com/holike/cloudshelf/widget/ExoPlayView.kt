package com.holike.cloudshelf.widget

import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import cn.jzvd.JzvdStd
import com.holike.cloudshelf.R


class ExoPlayView : JzvdStd {
    private lateinit var mVoiceSeekBar: SeekBar
    private lateinit var mMinVoiceView: ImageView
    private lateinit var mMaxVoiceView: ImageView

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun getLayoutId(): Int = R.layout.include_exoplayer_content

    override fun init(context: Context) {
        super.init(context)
        mVoiceSeekBar = findViewById(R.id.bottom_seek_progress_violent)
        mVoiceSeekBar.setOnSeekBarChangeListener(this)
        mMinVoiceView = findViewById(R.id.iv_voice_min)
        mMaxVoiceView = findViewById(R.id.iv_voice_max)
        mMinVoiceView.setOnClickListener(this)
        mMaxVoiceView.setOnClickListener(this)
    }

    override fun startVideo() {
        super.startVideo()
        mVoiceSeekBar.max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        mVoiceSeekBar.progress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) //设置当前音量
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (seekBar.id == R.id.bottom_seek_progress_violent) {
            when {
                progress <= 0 -> {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                }
                progress >= mVoiceSeekBar.max -> {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
                }
                else -> {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                }
            }
        } else {
            super.onProgressChanged(seekBar, progress, fromUser)
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.iv_voice_min) {
            mVoiceSeekBar.progress = if (mVoiceSeekBar.progress > 0) mVoiceSeekBar.progress - 1 else 0
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVoiceSeekBar.progress, 0)
            //showToastSound(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)+"");
        } else if (v.id == R.id.iv_voice_max) {
            mVoiceSeekBar.progress = if (mVoiceSeekBar.progress < mVoiceSeekBar.max) mVoiceSeekBar.progress + 1 else mVoiceSeekBar.max
            if (mVoiceSeekBar.progress == 100) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
            } else {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVoiceSeekBar.progress, 0)
            }
        } else {
            super.onClick(v)
        }
    }
}