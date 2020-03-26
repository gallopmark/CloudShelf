package com.holike.cloudshelf.activity

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseActivity
import kotlinx.android.synthetic.main.activity_videoplayer.*
import pony.xcode.media.exo.ExoMediaInterface
import pony.xcode.media.jz.JZvd


class VideoPlayerActivity : BaseActivity() {

    companion object {
        fun open(act: BaseActivity, videoUrl: String?) {
            val intent = Intent(act, VideoPlayerActivity::class.java)
            intent.putExtra(
                    "videoUrl",
                    "https://file.holike.com/miniprogram/test/video/5f839692-8eeb-40e0-aa69-aee594e73ada.mp4"
            )
            act.openActivity(intent)
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.activity_videoplayer

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        initVideoView()
        videoView.setUp(intent.getStringExtra("videoUrl"), "", JZvd.SCREEN_NORMAL, ExoMediaInterface::class.java)
        Glide.with(this).load(R.mipmap.ic_video_default).into(videoView.thumbImageView)
    }

    private fun initVideoView() {
        val lp = videoView.layoutParams as FrameLayout.LayoutParams
        val videoWidth = (CurrentApp.getInstance().getMaxPixels() * 0.67f).toInt()
        val videoHeight = (videoWidth * 0.57f).toInt()
        lp.width = videoWidth
        lp.height = videoHeight
        videoView.layoutParams = lp
        videoView.setVideoNormalSize(videoWidth, videoHeight, lp)
    }

    override fun onResume() {
        super.onResume()
        JZvd.goOnPlayOnResume()
    }

    override fun onBackPressed() {
        if (videoView.backPressed()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        JZvd.goOnPlayOnPause()
    }

    override fun onDestroy() {
        JZvd.releaseAllVideos()
        super.onDestroy()
    }
}