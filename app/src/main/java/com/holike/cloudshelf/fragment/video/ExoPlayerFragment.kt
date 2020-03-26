package com.holike.cloudshelf.fragment.video

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.holike.cloudshelf.R
import com.holike.cloudshelf.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_exoplayer.*
import pony.xcode.media.exo.ExoMediaInterface
import pony.xcode.media.jz.JZvd


class ExoPlayerFragment : BaseFragment() {
    companion object {
        /**
         * @param videoUrl 视频url
         * @param thumbUrl 视频缩略图
         */
        fun newInstance(videoUrl: String?, thumbUrl: String?, title: String?): ExoPlayerFragment {
            val bundle = Bundle().apply {
                putString("videoUrl", videoUrl)
                putString("thumbUrl", thumbUrl)
                putString("title", title)
            }
            val fragment = ExoPlayerFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.fragment_exoplayer
    override fun init(view: View, savedInstanceState: Bundle?) {
        val arg = arguments
        arg?.let {
            val videoUrl = it.getString("videoUrl")
            val thumbUrl = it.getString("thumbUrl")
            initPlayer(thumbUrl, videoUrl)
        }
    }

    private fun initPlayer(thumbUrl: String?, videoUrl: String?) {
        videoView.setFullscreenEnabled(false)
        videoView.setUp(videoUrl,"",JZvd.SCREEN_NORMAL, ExoMediaInterface::class.java)
        Glide.with(mContext).load(thumbUrl)
            .apply(RequestOptions().skipMemoryCache(true).error(R.mipmap.ic_video_default))
            .into(videoView.thumbImageView)
    }

    override fun onResume() {
        super.onResume()
        JZvd.goOnPlayOnResume()
    }

    override fun onPause() {
        super.onPause()
        JZvd.goOnPlayOnPause()
    }

    override fun onDestroyView() {
        JZvd.releaseAllVideos()
        super.onDestroyView()
    }
}