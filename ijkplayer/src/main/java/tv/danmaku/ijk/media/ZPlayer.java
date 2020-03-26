package tv.danmaku.ijk.media;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.utils.NetUtils;
import tv.danmaku.ijk.media.utils.ZPlayerUtils;

/**
 * 视频播放控制类
 */
@SuppressLint("SourceLockedOrientationActivity")
public class ZPlayer extends RelativeLayout implements Handler.Callback {
    /**
     * fitParent:scale the video uniformly (maintain the video's aspect ratio)
     * so that both dimensions (width and height) of the video will be equal to
     * or **less** than the corresponding dimension of the view. like
     * ImageView's `CENTER_INSIDE`.等比缩放,画面填满view。
     */
    public static final String SCALETYPE_FITPARENT = "fitParent";
    /**
     * fillParent:scale the video uniformly (maintain the video's aspect ratio)
     * so that both dimensions (width and height) of the video will be equal to
     * or **larger** than the corresponding dimension of the view .like
     * ImageView's `CENTER_CROP`.等比缩放,直到画面宽高都等于或小于view的宽高。
     */
    public static final String SCALETYPE_FILLPARENT = "fillParent";
    /**
     * wrapContent:center the video in the view,if the video is less than view
     * perform no scaling,if video is larger than view then scale the video
     * uniformly so that both dimensions (width and height) of the video will be
     * equal to or **less** than the corresponding dimension of the view.
     * 将视频的内容完整居中显示，如果视频大于view,则按比例缩视频直到完全显示在view中。
     */
    public static final String SCALETYPE_WRAPCONTENT = "wrapContent";
    /**
     * fitXY:scale in X and Y independently, so that video matches view
     * exactly.不剪裁,非等比例拉伸画面填满整个View
     */
    public static final String SCALETYPE_FITXY = "fitXY";
    /**
     * 16:9:scale x and y with aspect ratio 16:9 until both dimensions (width
     * and height) of the video will be equal to or **less** than the
     * corresponding dimension of the view.不剪裁,非等比例拉伸画面到16:9,并完全显示在View中。
     */
    public static final String SCALETYPE_16_9 = "16:9";
    /**
     * 4:3:scale x and y with aspect ratio 4:3 until both dimensions (width and
     * height) of the video will be equal to or **less** than the corresponding
     * dimension of the view.不剪裁,非等比例拉伸画面到4:3,并完全显示在View中。
     */
    public static final String SCALETYPE_4_3 = "4:3";
    private static final int MESSAGE_SHOW_PROGRESS = 1;
    private static final int MESSAGE_FADE_OUT = 2;
    private static final int MESSAGE_SEEK_NEW_POSITION = 3;
    private static final int MESSAGE_HIDE_CENTER_BOX = 4;
    private static final int MESSAGE_RESTART_PLAY = 5;
    private Activity activity;
    private Context context;
    private View contentView;
    private IjkVideoView videoView;
    private SeekBar seekBar;
    private AudioManager mAudioManager;
    private int mMaxVolume;
    private SeekBar mVolumeSeekBar;
    private boolean playerSupport;
    private String mVideoUrl;  //视频url
    private Query mQuery;
    private int STATUS_ERROR = -1;
    private int STATUS_IDLE = 0;
    private int STATUS_LOADING = 1;
    private int STATUS_PLAYING = 2;
    private int STATUS_PAUSE = 3;
    private int STATUS_COMPLETED = 4;
    private boolean isFullScreen;
    private int status = STATUS_IDLE;
    private boolean isLive = false;// 是否为直播
    private boolean isShowLoading = true;  //是否显示正在加载框
    private boolean isAlwaysHideControl = false;//是否一直隐藏视频控制栏
    private boolean isAlwaysShowControl = false;//是否一直显示视频控制栏
    private boolean isShowErrorControl = true;  //是否显示错误提示
    private boolean isShowTopControl = true;//是否显示头部显示栏，true：竖屏也显示 false：竖屏不显示，横屏显示
    private boolean isSupportGesture = true;//是否支持手势操作，false
    private boolean isVideoPrepared = false;// 是否已经初始化播放
    private boolean isNetListener = true;// 是否添加网络监听 (默认是监听)
    private boolean isAspectRatioEnable;//是否支持双击切换纵横比
    private boolean isSupportOrientationEvent;//是否支持重力感应
    private boolean isSupportScreenSwitch = true; //是否支持横竖屏切换

    private OnClickListener onClickSetting;//是否显示设置按钮 设置监听就显示 否则不显示  默认全屏显示设置
    private OnClickListener onClickShare;//是否显示分享按钮  设置监听就显示，否则不显示 默认小屏显示分享
    private OnFullScreenListener onFullscreenListener;//全屏小屏切换监听
    // 网络监听回调
    private NetChangeReceiver netChangeReceiver;
    private OnNetChangeListener onNetChangeListener;
    private OnFullScreenClick onFullScreenClick;

    private OrientationEventListener orientationEventListener;
    private int defaultTimeout = 3000;
    private int screenWidthPixels;

    private int initWidth = 0;
    private int initHeight = 0;
    private boolean isShowLiveNumber;
    private Handler mHandler;
    //volume
    private Dialog mVolumeDialog;
    private ImageView mDialogVolumeImageView;
    private TextView mDialogVolumeTextView;
    private ProgressBar mDialogVolumeProgressBar;
    //brightness
    private Dialog mBrightnessDialog;
    private ProgressBar mDialogBrightnessProgressBar;
    private TextView mDialogBrightnessTextView;
    //progress
    private Dialog mProgressDialog;
    private ImageView mDialogIcon;
    private TextView mDialogSeekTimeTextView;
    private TextView mDialogTotalTimeTextView;

    private ImageView mThumbImageView;
    private boolean mThumbEnabled;

    public ZPlayer(Context context) {
        this(context, null);
    }

    public ZPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        activity = (Activity) this.context;
        mHandler = new Handler(Looper.getMainLooper(), this);
        //初始化view和其他相关的
        initView();
    }

    /**
     * 相应点击事件
     */
    private final OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.view_jky_player_fullscreen) {
                if (onFullScreenClick == null || !onFullScreenClick.onClick()) {
                    toggleFullScreen();
                }
            } else if (v.getId() == R.id.app_video_play) {
                doPauseResume();
                show(defaultTimeout);
            } else if (v.getId() == R.id.view_jky_player_center_control) {
                // videoView.seekTo(0);
                // videoView.start();
                doPauseResume();
                show(defaultTimeout);
                mQuery.id(R.id.view_jky_player_center_control).gone();
            } else if (v.getId() == R.id.app_video_finish) {
                if (!fullScreenOnly && !isPortrait) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    activity.finish();
                }
            } else if (v.getId() == R.id.view_jky_player_tv_continue) {
                isNetListener = false;// 取消网络的监听
                mQuery.id(R.id.view_jky_player_tip_control).gone();
                play(mVideoUrl, currentPosition);
            } else if (v.getId() == R.id.view_jky_play_iv_setting) {
                if (onClickSetting != null) {
                    onClickSetting.onClick(v);
                }
            } else if (v.getId() == R.id.view_jky_player_iv_share) {
                if (onClickShare != null) {
                    onClickShare.onClick(v);
                }
            }
            if (v.getId() == R.id.iv_voice_min) {
                decreaseVolume();
            } else if (v.getId() == R.id.iv_voice_max) {
                increaseVolume();
            }
        }
    };
    private boolean isShowing;
    private boolean isPortrait;
    private float brightness = -1;
    private int volume = -1;
    private long newPosition = -1;
    private long defaultRetryTime = 5000;
    private OnErrorListener onErrorListener;
    private OnCompleteListener onCompleteListener;
    private OnInfoListener onInfoListener;
    private OnPreparedListener onPreparedListener;

    /**
     * try to play when error(only for live video)
     *
     * @param defaultRetryTime millisecond,0 will stop retry,default is 5000 millisecond
     */
    public ZPlayer setDefaultRetryTime(long defaultRetryTime) {
        this.defaultRetryTime = defaultRetryTime;
        return this;
    }

    private int currentPosition;
    private boolean fullScreenOnly;

    public ZPlayer setTitle(CharSequence title) {
        mQuery.id(R.id.app_video_title).text(title);
        return this;
    }

    private void doPauseResume() {
        if (status == STATUS_IDLE) {
            play(mVideoUrl);
        } else if (status == STATUS_COMPLETED) {
            play(mVideoUrl);
        } else if (videoView.isPlaying()) {
            statusChange(STATUS_PAUSE);
            videoView.pause();
        } else {
            videoView.start();
        }
        updatePausePlay();
    }

    /**
     * 更新暂停状态的控件显示
     */
    private void updatePausePlay() {
        if (videoView.isPlaying()) {
            mQuery.id(R.id.app_video_play).image(R.drawable.superplayer_ic_pause);
        } else {
            mQuery.id(R.id.app_video_play).image(R.drawable.superplayer_ic_play);
        }
    }

    private void show(int timeout) {
        if (isAlwaysHideControl) {
            showBottomControl(false);
            showTopControl(false);
            return;
        }
        if (!isShowing && isVideoPrepared) {
            if (!isShowTopControl) {
                showTopControl(false);
            } else {
                showTopControl(true);
            }
            showBottomControl(true);
            if (isSupportScreenSwitch && !fullScreenOnly) {
                mQuery.id(R.id.view_jky_player_fullscreen).visible();
            }
            isShowing = true;
        }
        updatePausePlay();
        mHandler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        mHandler.removeMessages(MESSAGE_FADE_OUT);
        if (timeout != 0 && status == STATUS_PLAYING) {
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_FADE_OUT), timeout);
        }
    }

    /**
     * 隐藏显示底部控制栏
     *
     * @param show true ： 显示 false ： 隐藏
     */
    private void showBottomControl(boolean show) {
        mQuery.id(R.id.app_video_bottom_box).visibility(show ? View.VISIBLE : View.GONE);
        if (isLive) {// 直播需要隐藏和显示一些底部的一些控件
            mQuery.id(R.id.app_video_play).gone();
            mQuery.id(R.id.app_video_time).gone();
//            mQuery.id(R.id.app_video_currentTime).gone();
//            mQuery.id(R.id.app_video_endTime).gone();
            mQuery.id(R.id.app_video_seekBar).gone();
            if (isShowLiveNumber) {
                mQuery.id(R.id.view_jky_player_tv_number).visible();
            }
        }

    }

    /**
     * 隐藏和显示头部的一些控件
     */
    private void showTopControl(boolean show) {
        mQuery.id(R.id.app_video_top_box).visibility(show ? View.VISIBLE : View.GONE);
    }

    private final SeekBar.OnSeekBarChangeListener mVolumeSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress <= 0) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            } else if (progress >= mVolumeSeekBar.getMax()) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mMaxVolume, 0);
            } else {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    /**
     * 减小音量
     */
    private void decreaseVolume() {
        int progress = mVolumeSeekBar.getProgress();
        mVolumeSeekBar.setProgress(progress > 0 ? progress - 1 : 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeSeekBar.getProgress(), 0);
    }

    /**
     * 增大音量
     */
    private void increaseVolume() {
        int progress = mVolumeSeekBar.getProgress();
        mVolumeSeekBar.setProgress(progress < mMaxVolume ? progress + 1 : mMaxVolume);
        if (mVolumeSeekBar.getProgress() == 100) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mMaxVolume, 0);
        } else {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeSeekBar.getProgress(), 0);
        }
    }

    private long duration;
    //    private boolean instantSeeking;
    private boolean isDragging;
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser)
                return;
            mQuery.id(R.id.view_jky_player_tip_control).gone();// 移动时隐藏掉状态image
//            int newPosition = (int) ((duration * progress * 1.0) / 1000);
//            String time = generateTime(newPosition);
//            if (instantSeeking) {
//                videoView.seekTo(newPosition);
//            }
//            mQuery.id(R.id.app_video_currentTime).text(time);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isDragging = true;
            show(3600000);
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
//            if (instantSeeking) {
//                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
//            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
//            if (!instantSeeking) {
//                videoView.seekTo((int) ((duration * seekBar.getProgress() * 1.0) / 1000));
//            }
            videoView.seekTo((int) ((duration * seekBar.getProgress() * 1.0) / 1000));
            show(defaultTimeout);
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            isDragging = false;
            mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
        }
    };

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MESSAGE_FADE_OUT:
                hide(false);
                break;
            case MESSAGE_HIDE_CENTER_BOX:
                if (mVolumeDialog != null) {
                    mVolumeDialog.dismiss();
                }
                if (mBrightnessDialog != null) {
                    mBrightnessDialog.dismiss();
                }
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                break;
            case MESSAGE_SEEK_NEW_POSITION:
                if (!isLive && newPosition >= 0) {
                    videoView.seekTo((int) newPosition);
                    newPosition = -1;
                }
                break;
            case MESSAGE_SHOW_PROGRESS:
                int pos = setProgress();
                if (!isDragging && isShowing && isPlaying()) {
                    msg = mHandler.obtainMessage(MESSAGE_SHOW_PROGRESS);
                    mHandler.sendMessageDelayed(msg, 1000 - (pos % 1000));
                    updatePausePlay();
                }
                break;
            case MESSAGE_RESTART_PLAY:
                play(mVideoUrl);
                break;
        }
        return false;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            playerSupport = true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;
        screenWidthPixels = Math.max(width, height);
        mQuery = new Query();
        contentView = View.inflate(context, R.layout.view_super_player, this);
        videoView = contentView.findViewById(R.id.video_view);
        videoView.setEnableSurfaceView(false);
        videoView.setEnableTextureView(true);
        videoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                isVideoPrepared = false;
                statusChange(STATUS_COMPLETED);
                if (onCompleteListener != null)
                    onCompleteListener.onComplete();
            }
        });
        videoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
                statusChange(STATUS_ERROR);
                if (onErrorListener != null) {
                    onErrorListener.onError(what, extra);
                }
                return true;
            }
        });
        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        statusChange(STATUS_LOADING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        statusChange(STATUS_PLAYING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        // 显示 下载速度
                        // Toast.makeText(activity,"download rate:" +
                        // extra,Toast.LENGTH_SHORT).show();
                        break;
                }
                if (onInfoListener != null) {
                    onInfoListener.onInfo(what, extra);
                }
                return false;
            }
        });
        videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                isVideoPrepared = true;
                removeThumbView();
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        hide(false);
//                        show(defaultTimeout);
//                    }
//                }, 500);
                mQuery.id(R.id.replay_text).gone();
                if (onPreparedListener != null) {
                    onPreparedListener.onPrepared();
                }
                if (isLive) {
                    videoView.seekTo(0);
                } else if (currentPosition > 0) {
                    seekTo(currentPosition, false);
                }
                videoView.start();
            }
        });
        mThumbImageView = contentView.findViewById(R.id.thumbImageView);
        seekBar = contentView.findViewById(R.id.app_video_seekBar);
        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(mSeekListener);
        mQuery.id(R.id.app_video_play).clicked(onClickListener);
        mQuery.id(R.id.view_jky_player_fullscreen).clicked(onClickListener);
        mQuery.id(R.id.app_video_finish).clicked(onClickListener);
        mQuery.id(R.id.view_jky_player_center_control).clicked(onClickListener);
        mQuery.id(R.id.view_jky_player_tv_continue).clicked(onClickListener);
        mQuery.id(R.id.view_jky_play_iv_setting).clicked(onClickListener);
        mQuery.id(R.id.view_jky_player_iv_share).clicked(onClickListener);
        mQuery.id(R.id.iv_voice_min).clicked(onClickListener);
        mQuery.id(R.id.iv_voice_max).clicked(onClickListener);
        mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        //noinspection ConstantConditions
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolumeSeekBar = contentView.findViewById(R.id.bottom_seek_progress_violent);
        mVolumeSeekBar.setMax(mMaxVolume);
        mVolumeSeekBar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)); //设置当前音量
        mVolumeSeekBar.setOnSeekBarChangeListener(mVolumeSeekListener);
        final GestureDetector gestureDetector = new GestureDetector(activity, new PlayerGestureListener());
        View container = contentView.findViewById(R.id.app_video_box);
        container.setClickable(true);
        container.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!isVideoPrepared) return false;  //视频未准备 手势不可用
                if (gestureDetector.onTouchEvent(motionEvent))
                    return true;
                // 处理手势结束
                if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    endGesture();
                }
                return false;
            }
        });
        /*
         * 监听手机重力感应的切换屏幕的方向
         */
        orientationEventListener = new OrientationEventListener(activity) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation >= 0 && orientation <= 30 || orientation >= 330 || (orientation >= 150 && orientation <= 210)) {
                    // 竖屏
                    if (isPortrait) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                } else if ((orientation >= 90 && orientation <= 120) || (orientation >= 240 && orientation <= 300)) {
                    if (!isPortrait) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                }
            }
        };

        if (fullScreenOnly) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        isPortrait = getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        hideAll();
        if (!playerSupport) {
            showStatus(activity.getResources().getString(R.string.IjkPlayer_not_support), "重试");
        }
        updateFullScreenButton();
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        volume = -1;
        brightness = -1f;
        if (newPosition >= 0) {
            mHandler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
            mHandler.sendEmptyMessage(MESSAGE_SEEK_NEW_POSITION);
        }
        mHandler.removeMessages(MESSAGE_HIDE_CENTER_BOX);
        mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_CENTER_BOX, 500);
    }

    /**
     * 视频播放状态的改变
     */
    private void statusChange(int newStatus) {
        status = newStatus;
        if (!isLive && newStatus == STATUS_COMPLETED) {// 当视频播放完成的时候
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
            hideAll();
            //显示重播按钮
            mQuery.id(R.id.view_jky_player_center_control).visible();
            mQuery.id(R.id.view_jky_player_center_play).image(R.drawable.superplayer_replay_selector);
            mQuery.id(R.id.replay_text).visible();
            updatePausePlay();
            if (mThumbEnabled) {
                mThumbImageView.setVisibility(View.VISIBLE);
            }
        } else if (newStatus == STATUS_ERROR) {
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
            hideAll();
            if (isShowErrorControl) {
                if (isLive) {
                    showStatus(activity.getResources().getString(R.string.IjkPlayer_small_problem_alive), "重试");
                } else {
                    showStatus(activity.getResources().getString(R.string.IjkPlayer_small_problem), "重试");
                }
            }
            if (isLive) {
                mHandler.removeMessages(MESSAGE_RESTART_PLAY);
                mHandler.sendEmptyMessageDelayed(MESSAGE_RESTART_PLAY, defaultRetryTime);
            }
        } else if (newStatus == STATUS_LOADING) {
            hideAll();
            if (isShowLoading) mQuery.id(R.id.app_video_loading).visible();
        } else if (newStatus == STATUS_PLAYING) {
            hideAll();
        }
    }

    /**
     * 隐藏全部的控件
     */
    private void hideAll() {
        mQuery.id(R.id.app_video_loading).gone();
        mQuery.id(R.id.view_jky_player_tip_control).gone();
        hide(true);
        //      showBottomControl(false);
        //      showTopControl(false);
    }

    private void doOnConfigurationChanged(final boolean portrait) {
        if (videoView != null && !fullScreenOnly) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    tryFullScreen(!portrait);
                    if (portrait) {
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        if (initWidth != 0) {
                            layoutParams.width = initWidth;
                        }
                        if (initHeight == 0) {
                            layoutParams.height = ZPlayerUtils.getScreenWidth(activity) * 9 / 16;
                        } else {
                            layoutParams.height = initHeight;
                        }
                        setLayoutParams(layoutParams);
                        requestLayout();
                    } else {
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        setLayoutParams(layoutParams);
                    }
                    updateFullScreenButton();
                    hide(false);
                    show(defaultTimeout);
                }
            });
            if (isSupportOrientationEvent) {
                orientationEventListener.enable();
            }
        }
    }

    private void tryFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
        setFullScreen(fullScreen);
        if (onFullscreenListener != null) {
            onFullscreenListener.onFullScreen(fullScreen);
        }
    }

    private void setFullScreen(boolean fullScreen) {
        if (activity != null) {
            if (fullScreen) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                //                activity.getWindow().addFlags(
                //                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                //                activity.getWindow().clearFlags(
                //                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }
    }

    /**
     * 在activity中的onResume中需要回调
     */
    public void onResume() {
        if (status == STATUS_PLAYING) {
            if (isLive) {
                videoView.seekTo(0);
            } else {
                if (currentPosition > 0) {
                    videoView.seekTo(currentPosition);
                }
            }
            videoView.start();
        }
    }

    /**
     * 暂停
     * 在activity中的onPause中需要回调
     */
    public void onPause() {
        show(0);// 把系统状态栏显示出来
        if (status == STATUS_PLAYING) {
            videoView.pause();
            if (!isLive) {
                currentPosition = videoView.getCurrentPosition();
            }
        }
    }

    /**
     * 监听全屏跟非全屏
     * 在activity中的onConfigurationChanged中需要回调
     */
    public void onConfigurationChanged(final Configuration newConfig) {
        isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(isPortrait);
    }

    /**
     * 在activity中的onDestroy中需要回调
     */
    public void onDestroy() {
        unregisterNetReceiver();// 取消网络变化的监听
        orientationEventListener.disable();
        mHandler.removeCallbacksAndMessages(null);
        videoView.stopPlayback();
    }

    public boolean onBackPressed() {
        if (!fullScreenOnly && getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }

    /**
     * 显示错误信息
     *
     * @param statusText 错误提示
     * @param btnText    错误按钮提示
     */
    private void showStatus(String statusText, String btnText) {
        mQuery.id(R.id.view_jky_player_tip_control).visible();
        mQuery.id(R.id.view_jky_player_tip_text).text(statusText);
        mQuery.id(R.id.view_jky_player_tv_continue).text(btnText);
        isVideoPrepared = false;// 设置点击不能出现控制栏
    }

    /**
     * 开始播放
     *
     * @param url 播放视频的地址
     */
    public void play(String url) {
        if (url != null) {
            play(url, 0);
        }
    }

    /**
     * @param url             开始播放(可播放指定位置)
     * @param currentPosition 指定位置的大小(0-1000)
     *                        一般用于记录上次播放的位置或者切换视频源
     */
    public void play(String url, int currentPosition) {
        this.mVideoUrl = url;
        this.currentPosition = currentPosition;
        hideAll();
        if (!isNetListener) {// 如果设置不监听网络的变化，则取消监听网络变化的广播
            unregisterNetReceiver();
        } else {
            // 注册网路变化的监听
            registerNetReceiver();
        }
        if (videoView != null) {
            release();
        }
        if (isNetListener && (NetUtils.getNetworkType(activity) == 2 || NetUtils.getNetworkType(activity) == 4)) {// 手机网络的情况下
            mQuery.id(R.id.view_jky_player_tip_control).visible();
        } else {
            if (playerSupport) {
                if (isShowLoading) mQuery.id(R.id.app_video_loading).visible();
                if (videoView == null) return;
                videoView.setVideoPath(url);
            }
        }
    }

    /**
     * 播放切换视频源地址
     */
    public void playSwitch(String url) {
        this.mVideoUrl = url;
        if (videoView.isPlaying()) {
            getCurrentPosition();
        }
        play(url, currentPosition);
    }

    /**
     * 格式化显示的时间
     */
    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
                : String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private int getScreenOrientation() {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
                && height > width || (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_0:
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        } else {
            switch (rotation) {
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_0:
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    private Dialog createDialogWithView(View localView) {
        Dialog dialog = new Dialog(activity, R.style.superplayer_style_dialog_progress);
        dialog.setContentView(localView);
        Window window = dialog.getWindow();
        if (window != null) {
            window.addFlags(Window.FEATURE_ACTION_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams localLayoutParams = window.getAttributes();
            localLayoutParams.gravity = Gravity.CENTER;
            window.setAttributes(localLayoutParams);
        }
        return dialog;
    }

    /**
     * 滑动改变声音大小
     */
    private void onVolumeSlide(float percent) {
        if (volume == -1) {
            volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0)
                volume = 0;
        }
        hide(true);

        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        mVolumeSeekBar.setProgress(index);
        // 变更进度条
        int i = (int) (index * 1.0 / mMaxVolume * 100);
        String s = i + "%";
        if (i == 0) {
            s = "0%";
        }
        // 显示
        if (mVolumeDialog == null) {
            View view = View.inflate(activity, R.layout.super_player_dialog_volume, null);
            mDialogVolumeImageView = view.findViewById(R.id.volume_image_tip);
            mDialogVolumeTextView = view.findViewById(R.id.tv_volume);
            mDialogVolumeProgressBar = view.findViewById(R.id.volume_progressbar);
            mVolumeDialog = createDialogWithView(view);
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }
        mDialogVolumeImageView.setImageResource(i == 0 ? R.drawable.superplayer_close_volume : R.drawable.superplayer_add_volume);
        mDialogVolumeTextView.setText(s);
        mDialogVolumeProgressBar.setMax(100);
        mDialogVolumeProgressBar.setProgress(i);
    }

    private void onProgressSlide(float percent) {
        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);

        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition = 0;
            delta = -position;
        }
        int showDelta = (int) delta / 1000;
        if (showDelta != 0) {
            if (mProgressDialog == null) {
                View view = View.inflate(activity, R.layout.super_player_dialog_progress, null);
                mDialogIcon = view.findViewById(R.id.duration_image_tip);
                mDialogSeekTimeTextView = view.findViewById(R.id.app_video_fastForward_target);
                mDialogTotalTimeTextView = view.findViewById(R.id.app_video_fastForward_all);
                mProgressDialog = createDialogWithView(view);
            }
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
            mDialogIcon.setImageResource(showDelta > 0 ? R.drawable.superplayer_forward_icon : R.drawable.superplayer_backward_icon);
            String seekText = generateTime(newPosition) + "/";
            mDialogSeekTimeTextView.setText(seekText);
            mDialogTotalTimeTextView.setText(generateTime(duration));
        }
    }

    /**
     * 滑动改变亮度
     */
    private void onBrightnessSlide(float percent) {
        if (brightness < 0) {
            brightness = activity.getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        if (mBrightnessDialog == null) {
            View view = View.inflate(activity, R.layout.super_player_dialog_brightness, null);
            mDialogBrightnessTextView = view.findViewById(R.id.tv_brightness);
            mDialogBrightnessProgressBar = view.findViewById(R.id.brightness_progressbar);
            mBrightnessDialog = createDialogWithView(view);
        }
        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.show();
        }
        WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        String text = ((int) (lpa.screenBrightness * 100)) + "%";
        mDialogBrightnessTextView.setText(text);
        mDialogBrightnessProgressBar.setProgress(100);
        mDialogBrightnessProgressBar.setProgress((int) (lpa.screenBrightness * 100));
        activity.getWindow().setAttributes(lpa);
    }

    private int setProgress() {
        if (isDragging) {
            return 0;
        }
        int position = videoView.getCurrentPosition();
        int duration = videoView.getDuration();
        if (seekBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = videoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent * 10);
        }

        this.duration = duration;
        String text = generateTime(position) + "/" + generateTime(this.duration);
        mQuery.id(R.id.app_video_time).text(text);
//        mQuery.id(R.id.app_video_currentTime).text(generateTime(position));
//        mQuery.id(R.id.app_video_endTime).text(generateTime(this.duration));
        return position;
    }

    public void hide(boolean force) {
        if ((force || isShowing) && !isAlwaysShowControl) {
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
            showBottomControl(false);
            showTopControl(false);
            if (!isSupportScreenSwitch) {
                mQuery.id(R.id.view_jky_player_fullscreen).gone();
            } else {
                mQuery.id(R.id.view_jky_player_fullscreen).invisible();
            }
            isShowing = false;
        }
    }

    public void toggleFullscreenButton(boolean isFullScreen) {
        if (isFullScreen) {
            mQuery.id(R.id.view_jky_player_fullscreen).image(R.drawable.superplayer_shrink);
        } else {
            mQuery.id(R.id.view_jky_player_fullscreen).image(R.drawable.superplayer_enlarge);
        }
    }

    /**
     * 更新全屏按钮
     */
    private void updateFullScreenButton() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {// 全屏幕
            mQuery.id(R.id.view_jky_player_fullscreen).image(R.drawable.superplayer_shrink);
            mQuery.id(R.id.view_jky_player_iv_share).gone();
            if (onClickSetting != null) {
                mQuery.id(R.id.view_jky_play_iv_setting).visible();
            } else {
                mQuery.id(R.id.view_jky_play_iv_setting).gone();
            }
        } else {
            mQuery.id(R.id.view_jky_player_fullscreen).image(R.drawable.superplayer_enlarge);
            if (onClickShare != null) {
                mQuery.id(R.id.view_jky_player_iv_share).visible();
            } else {
                mQuery.id(R.id.view_jky_player_iv_share).gone();
            }
            mQuery.id(R.id.view_jky_play_iv_setting).gone();
        }
    }


    /**
     * using constants in GiraffePlayer,eg: GiraffePlayer.SCALETYPE_FITPARENT
     */
    public ZPlayer setScaleType(String scaleType) {
        if (SCALETYPE_FITPARENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
        } else if (SCALETYPE_FILLPARENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT);
        } else if (SCALETYPE_WRAPCONTENT.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_ASPECT_WRAP_CONTENT);
        } else if (SCALETYPE_FITXY.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_MATCH_PARENT);
        } else if (SCALETYPE_16_9.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_16_9_FIT_PARENT);
        } else if (SCALETYPE_4_3.equals(scaleType)) {
            videoView.setAspectRatio(IRenderView.AR_4_3_FIT_PARENT);
        }
        return this;
    }

    /**
     * 是否显示左上导航图标(一般有actionbar or appToolbar时需要隐藏)
     */
    public ZPlayer setShowNavIcon(boolean show) {
        mQuery.id(R.id.app_video_finish).visibility(show ? View.VISIBLE : View.GONE);
        return this;
    }

    public void start() {
        if (videoView != null) videoView.start();
    }

    public void pause() {
        if (videoView != null) videoView.pause();
    }

    public void setLiveNumber(boolean showLiveNumber) {
        isShowLiveNumber = showLiveNumber;
        if (showLiveNumber && isLive) {
            mQuery.id(R.id.view_jky_player_tv_number).visibility(View.VISIBLE);
        } else {
            mQuery.id(R.id.view_jky_player_tv_number).visibility(View.GONE);
        }
    }

    public void setLiveNumber(String number) {
        mQuery.id(R.id.view_jky_player_tv_number).text(number);
    }

    public class Query {
        private View view;

        public Query id(int id) {
            view = contentView.findViewById(id);
            return this;
        }

        public Query image(int resId) {
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(resId);
            }
            return this;
        }

        public Query visible() {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            return this;
        }

        public Query gone() {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
            return this;
        }

        public Query invisible() {
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
            return this;
        }

        public Query clicked(OnClickListener handler) {
            if (view != null) {
                view.setOnClickListener(handler);
            }
            return this;
        }

        public Query text(CharSequence text) {
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        public Query visibility(int visible) {
            if (view != null) {
                view.setVisibility(visible);
            }
            return this;
        }
    }

    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean firstTouch;
        private boolean volumeControl;
        private boolean toSeek;

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!isVideoPrepared) {// 视频没有初始化点击屏幕不起作用
                return false;
            }
            if (isAspectRatioEnable) {
                videoView.toggleAspectRatio();
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;
            return super.onDown(e);
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isSupportGesture) {
                return true;
            }
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (firstTouch) {
                toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl = mOldX > screenWidthPixels * 0.5f;
                firstTouch = false;
            }

            if (toSeek) {
                if (!isLive) {
                    onProgressSlide(-deltaX / videoView.getWidth());
                }
            } else {
                float percent = deltaY / videoView.getHeight();
                if (volumeControl) {
                    onVolumeSlide(percent);
                } else {
                    onBrightnessSlide(percent);
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (!isVideoPrepared) {// 视频没有初始化点击屏幕不起作用
                return false;
            }
            if (isShowing) {
                hide(false);
            } else {
                show(defaultTimeout);
            }
            return true;
        }
    }

    /**
     * is player support this device
     */
    public boolean isPlayerSupport() {
        return playerSupport;
    }

    /**
     * 是否正在播放
     */
    public boolean isPlaying() {
        return videoView != null && videoView.isPlaying();
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        videoView.release(true);
        videoView.seekTo(0);
    }

    /**
     * 设置全屏切换监听
     */
    public ZPlayer setOnFullScreenListener(OnFullScreenListener onFullScreenListener) {
        this.onFullscreenListener = onFullScreenListener;
        return this;
    }

    /**
     * seekTo position
     *
     * @param msec millisecond
     */
    public ZPlayer seekTo(int msec, boolean showControlPanle) {
        videoView.seekTo(msec);
        if (showControlPanle) {
            show(defaultTimeout);
        }
        return this;
    }

    /**
     * 快退快退（取决于传进来的percent）
     */
    public ZPlayer forward(float percent) {
        if (isLive || percent > 1 || percent < -1) {
            return this;
        }
        onProgressSlide(percent);
        showBottomControl(true);
        mHandler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        endGesture();
        return this;
    }

    /**
     * 获取当前播放的currentPosition
     */
    public int getCurrentPosition() {
        if (!isLive) {
            currentPosition = videoView.getCurrentPosition();
        } else {// 直播
            currentPosition = -1;
        }
        return currentPosition;
    }

    /**
     * 获取视频的总长度
     */
    public int getDuration() {
        return videoView.getDuration();
    }

    /**
     * 是否支持横竖屏切换
     */
    public ZPlayer setSupportScreenSwitch(boolean isSupport) {
        this.isSupportScreenSwitch = isSupport;
        return this;
    }

    /*
     * 全屏播放，和{@link #setFullScreenOnly(boolean)}不同的时候，这个只是默认全屏播放，可以返回到小屏页面
     */
    public ZPlayer playInFullScreen(boolean fullScreen) {
        if (fullScreen) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            updateFullScreenButton();
        }
        return this;
    }

    /**
     * 设置只能全屏
     *
     * @param fullScreenOnly true ： 只能全屏 false ： 小屏幕显示
     */
    public ZPlayer setFullScreenOnly(boolean fullScreenOnly) {
        this.fullScreenOnly = fullScreenOnly;
        tryFullScreen(fullScreenOnly);
        if (fullScreenOnly) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        updateFullScreenButton();
        return this;
    }

    /**
     * 设置播放视频的是否是全屏
     */
    public void toggleFullScreen() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {// 转小屏
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (isShowTopControl) {
                showTopControl(false);
            }
        } else {// 转全屏
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            showTopControl(true);
        }
        updateFullScreenButton();
    }


    public interface OnErrorListener {
        void onError(int what, int extra);
    }

    public interface OnInfoListener {
        void onInfo(int what, int extra);
    }

    public interface OnPreparedListener {
        void onPrepared();
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    public ZPlayer onError(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
        return this;
    }

    public ZPlayer onComplete(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
        return this;
    }

    public ZPlayer onInfo(OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
        return this;
    }

    public ZPlayer onPrepared(OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
        return this;
    }

    // 网络监听的回调
    public ZPlayer setOnNetChangeListener(OnNetChangeListener onNetChangeListener) {
        this.onNetChangeListener = onNetChangeListener;
        return this;
    }

    /**
     * 全屏按钮点击
     * 返回true 拦截 false 不拦截
     */
    public ZPlayer setOnFullScreenClickListener(OnFullScreenClick onFullScreenClick) {
        this.onFullScreenClick = onFullScreenClick;
        return this;
    }

    /**
     * set is live (can't seek forward)
     */
    public ZPlayer setLive(boolean isLive) {
        this.isLive = isLive;
        videoView.setLive(isLive);
        return this;
    }

    public ZPlayer toggleAspectRatio() {
        if (videoView != null) {
            videoView.toggleAspectRatio();
        }
        return this;
    }

    /**
     * 注册网络监听器
     */
    private void registerNetReceiver() {
        if (netChangeReceiver == null) {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            netChangeReceiver = new NetChangeReceiver();
            activity.registerReceiver(netChangeReceiver, filter);
        }
    }

    /**
     * 销毁网络监听器
     */
    private void unregisterNetReceiver() {
        if (netChangeReceiver != null) {
            activity.unregisterReceiver(netChangeReceiver);
            netChangeReceiver = null;
        }
    }

    public interface OnNetChangeListener {
        // wifi
        void onWifi();

        // 手机
        void onMobile();

        // 网络断开
        void onDisConnect();

        // 网路不可用
        void onNoAvailable();
    }

    /*********************************
     * 网络变化监听
     ************************************/
    public class NetChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (onNetChangeListener == null) {
                return;
            }
            if (NetUtils.getNetworkType(activity) == 3) {// 网络是WIFI
                onNetChangeListener.onWifi();
            } else if (NetUtils.getNetworkType(activity) == 2 || NetUtils.getNetworkType(activity) == 4) {// 网络不是手机网络或者是以太网
                statusChange(STATUS_PAUSE);
                videoView.pause();
                updatePausePlay();
                mQuery.id(R.id.app_video_loading).gone();
                onNetChangeListener.onMobile();
                showStatus(activity.getResources().getString(R.string.IjkPlayer_player_not_wifi), "继续");
            } else if (NetUtils.getNetworkType(activity) == 1) {// 网络链接断开
                onPause();
                onNetChangeListener.onDisConnect();
            } else {
                onNetChangeListener.onNoAvailable();
            }
        }
    }

    /**
     * 设置视频url，不会自动播放，点击按钮才会开始播放
     */
    public ZPlayer setVideoUrl(String videoUrl) {
        this.mVideoUrl = videoUrl;
        return this;
    }

    /**
     * 设置控制栏退出时间
     */
    public ZPlayer setControlShowTimeOut(int timeOut) {
        this.defaultTimeout = timeOut;
        return this;
    }

    public ZPlayer setOrientationEventEnable(boolean isSupportOrientationEvent) {
        this.isSupportOrientationEvent = isSupportOrientationEvent;
        return this;
    }

    public ZPlayer setShowLoading(boolean isShowLoading) {
        this.isShowLoading = isShowLoading;
        return this;
    }

    /**
     * 是否显示中心控制器
     *
     * @param isShow true ： 显示 false ： 不显示
     */
    public ZPlayer setShowCenterControl(boolean isShow) {
//        this.isShowCenterControl = isShow;
        return this;
    }

    /**
     * 是否显示头部控制器
     *
     * @param isShowTopControl true：显示 false ： 不显示
     */
    public ZPlayer setShowTopControl(boolean isShowTopControl) {
        this.isShowTopControl = isShowTopControl;
        return this;
    }

    /**
     * 是否一直隐藏控制栏
     */
    public ZPlayer setAlwaysHideControl() {
        this.isAlwaysHideControl = true;
        return this;
    }

    /**
     * 是否一直显示控制栏
     */
    public ZPlayer setAlwaysShowControl(boolean isAlwaysShowControl) {
        this.isAlwaysShowControl = isAlwaysShowControl;
        return this;
    }

    public ZPlayer setShowErrorControl(boolean isShowErrorControl) {
        this.isShowErrorControl = isShowErrorControl;
        return this;
    }

    /**
     * 设置播放视频是否有网络变化的监听
     *
     * @param isNetListener true ： 监听 false ： 不监听
     */
    public ZPlayer setNetChangeListener(boolean isNetListener) {
        this.isNetListener = isNetListener;
        return this;
    }

    /**
     * 设置小屏幕是否支持手势操作（默认false）
     */
    public ZPlayer setSupportGesture(boolean isSupportGesture) {
        this.isSupportGesture = isSupportGesture;
        return this;
    }

    /**
     * 设置是否支持双击切换纵横比
     *
     * @param isAspectRatioEnable true 支持，false 不支持
     */
    public ZPlayer setSupportAspectRatio(boolean isAspectRatioEnable) {
        this.isAspectRatioEnable = isAspectRatioEnable;
        return this;
    }

    public ZPlayer setShowBottomVolume(boolean isShow) {
        mQuery.id(R.id.bottom_voice_box).visibility(isShow ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 是否显示设置按钮 设置监听就显示 否则不显示  默认全屏显示设置
     */
    public ZPlayer setSettingListener(OnClickListener onClickListener) {
        this.onClickSetting = onClickListener;
        return this;
    }


    /**
     * 是否显示分享按钮  设置监听就显示，否则不显示 默认小屏显示分享
     */
    public ZPlayer setShareListener(OnClickListener onClickListener) {
        this.onClickShare = onClickListener;
        return this;
    }


    /**
     * 设置了竖屏的时候播放器的宽高
     *
     * @param width  0：默认是屏幕的宽度
     * @param height 0：默认是宽度的16:9
     */
    public ZPlayer setPlayerWH(int width, int height) {
        this.initWidth = width;
        this.initHeight = height;
        return this;
    }

    public ZPlayer setThumb(@DrawableRes int resId) {
        if (resId != 0) {
            mThumbEnabled = true;
            mThumbImageView.setImageResource(resId);
            mThumbImageView.setVisibility(View.VISIBLE);
        }
        return this;
    }


    /**
     * 设置视频缩略图
     *
     * @param thumbUrl 缩略图url
     */
    public ZPlayer setThumb(String thumbUrl, ThumbImageLoader loader) {
        if (!TextUtils.isEmpty(thumbUrl) && loader != null) {
            mThumbEnabled = true;
            mThumbImageView.setVisibility(View.VISIBLE);
            loader.display(getContext(), thumbUrl, mThumbImageView);
        }
        return this;
    }

    private void removeThumbView() {
        if (mThumbEnabled) {
            mThumbImageView.setVisibility(View.GONE);
        }
    }

    /**
     * 当前是否为全屏状态
     */
    public boolean isFullScreen() {
        return isFullScreen;
    }

    /**
     * 获取到当前播放的状态
     */
    public int getVideoStatus() {
        return videoView.getCurrentState();
    }

    /**
     * 获得某个控件
     */
    public View getView(int ViewId) {
        return activity.findViewById(ViewId);
    }

    public Query getQuery() {
        return mQuery;
    }

    /**
     * 全屏按钮点击
     * 返回true 拦截 false 不拦截
     */
    public interface OnFullScreenClick {
        boolean onClick();
    }

    /**
     * 切换全屏和小屏页面的时候监听
     */
    public interface OnFullScreenListener {
        void onFullScreen(boolean isFullScreen);
    }

    public interface ThumbImageLoader {
        void display(@NonNull Context context, @Nullable String url, @NonNull ImageView target);
    }
}
