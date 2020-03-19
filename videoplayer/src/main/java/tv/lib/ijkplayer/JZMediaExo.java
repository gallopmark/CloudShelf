package tv.lib.ijkplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;

import cn.jzvd.JZMediaInterface;
import cn.jzvd.Jzvd;

public class JZMediaExo extends JZMediaInterface implements Player.EventListener, VideoListener {
    private SimpleExoPlayer simpleExoPlayer;
    private Runnable callback;
    private long previousSeek = 0;

    public JZMediaExo(Jzvd jzvd) {
        super(jzvd);
    }

    @Override
    public void start() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void prepare() {
        final Context context = jzvd.getContext();
        release();
        mMediaHandlerThread = new HandlerThread("JZVD");
        mMediaHandlerThread.start();
        mMediaHandler = new Handler(mMediaHandlerThread.getLooper());//主线程还是非主线程，就在这里
        handler = new Handler();
        mMediaHandler.post(() -> {
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory();
            TrackSelector trackSelector = new DefaultTrackSelector(context, videoTrackSelectionFactory);

            // 2. Create the player
            final LoadControl loadControl = new DefaultLoadControl.Builder()
                    .setAllocator(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
                    .setBufferDurationsMs(360000, 600000, 1000, 5000)
                    .setTargetBufferBytes(C.LENGTH_UNSET)
                    .setPrioritizeTimeOverSizeThresholds(false).createDefaultLoadControl();
//            RenderersFactory renderersFactory = new DefaultRenderersFactory(context);
            simpleExoPlayer = new SimpleExoPlayer.Builder(context, new DefaultRenderersFactory(context))
                    .setTrackSelector(trackSelector).setLoadControl(loadControl).build();
            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));
            String currUrl = jzvd.jzDataSource.getCurrentUrl().toString();
            MediaSource videoSource;
            if (!TextUtils.isEmpty(currUrl) && currUrl.contains(".m3u8")) {
                videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(currUrl));
                videoSource.addEventListener(handler, null);
//                        .createMediaSource(Uri.parse(currUrl), handler, null);
            } else {
                videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(currUrl));
            }
            simpleExoPlayer.addVideoListener(this);

            simpleExoPlayer.addListener(this);
            if (jzvd.jzDataSource.looping) {
                simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            } else {
                simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
            }
            simpleExoPlayer.prepare(videoSource);
            simpleExoPlayer.setPlayWhenReady(true);
            callback = new onBufferingUpdate();

            simpleExoPlayer.setVideoSurface(new Surface(jzvd.textureView.getSurfaceTexture()));
        });

    }

    @Override
    public void onVideoSizeChanged(final int width, final int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    jzvd.onVideoSizeChanged(width, height);
                }
            });
        }
    }

    @Override
    public void pause() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public boolean isPlaying() {
        if (simpleExoPlayer == null) return false;
        return simpleExoPlayer.getPlayWhenReady();
    }

    @Override
    public void seekTo(long time) {
        if (simpleExoPlayer != null) {
            if (time != previousSeek) {
                simpleExoPlayer.seekTo(time);
                previousSeek = time;
                jzvd.seekToInAdvance = time;
            }
        }
    }

    @Override
    public void release() {
        if (mMediaHandler != null && mMediaHandlerThread != null && simpleExoPlayer != null) {//不知道有没有妖孽
            JZMediaInterface.SAVED_SURFACE = null;
            mMediaHandler.post(new Runnable() {
                @Override
                public void run() {
                    simpleExoPlayer.release();//release就不能放到主线程里，界面会卡顿
                    mMediaHandlerThread.quit();
                }
            });
            simpleExoPlayer = null;
        }
    }

    @Override
    public long getCurrentPosition() {
        if (simpleExoPlayer != null)
            return simpleExoPlayer.getCurrentPosition();
        else return 0;
    }

    @Override
    public long getDuration() {
        if (simpleExoPlayer != null)
            return simpleExoPlayer.getDuration();
        else return 0;
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setVolume(leftVolume);
            simpleExoPlayer.setVolume(rightVolume);
        }
    }

    @Override
    public void setSpeed(float speed) {
        if(simpleExoPlayer !=null){
            PlaybackParameters playbackParameters = new PlaybackParameters(speed, 1.0F);
            simpleExoPlayer.setPlaybackParameters(playbackParameters);
        }
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onPlayerStateChanged(final boolean playWhenReady, final int playbackState) {
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    switch (playbackState) {
                        case Player.STATE_IDLE: {
                        }
                        break;
                        case Player.STATE_BUFFERING: {
                            handler.post(callback);
                        }
                        break;
                        case Player.STATE_READY: {
                            if (playWhenReady) {
                                jzvd.onStatePlaying();
                            }
                        }
                        break;
                        case Player.STATE_ENDED: {
                            jzvd.onAutoCompletion();
                        }
                        break;
                    }
                }
            });
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    jzvd.onError(1000, 1000);
                }
            });
        }
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    jzvd.onSeekComplete();
                }
            });
        }
    }

    @Override
    public void setSurface(Surface surface) {
        simpleExoPlayer.setVideoSurface(surface);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (SAVED_SURFACE == null) {
            SAVED_SURFACE = surface;
            prepare();
        } else {
            jzvd.textureView.setSurfaceTexture(SAVED_SURFACE);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private class onBufferingUpdate implements Runnable {
        @Override
        public void run() {
            if (simpleExoPlayer != null) {
                final int percent = simpleExoPlayer.getBufferedPercentage();
                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            jzvd.setBufferProgress(percent);
                        }
                    });
                    if (percent < 100) {
                        handler.postDelayed(callback, 300);
                    } else {
                        handler.removeCallbacks(callback);
                    }
                }
            }
        }
    }
}
