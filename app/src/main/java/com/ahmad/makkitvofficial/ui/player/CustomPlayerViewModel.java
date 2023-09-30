package com.ahmad.makkitvofficial.ui.player;

import android.app.Activity;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableBoolean;

import android.graphics.Color;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadRequestData;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.TextTrackStyle;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.ahmad.makkitvofficial.BR;
import com.ahmad.makkitvofficial.Provider.PrefManager;
import com.ahmad.makkitvofficial.R;
import com.ahmad.makkitvofficial.entity.Subtitle;
import com.ahmad.makkitvofficial.ui.activities.PlayerActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tamim on 28/09/2019.

 */

public class CustomPlayerViewModel extends BaseObservable implements Player.Listener {
    private ArrayList<Subtitle> subtitlesForCast= new ArrayList<>();

    private Activity mActivity;
    private String mUrl;
    private boolean loadingComplete = false;
    private static final boolean SHOULD_AUTO_PLAY = true;
    private ObservableBoolean controlsVisible = new ObservableBoolean(true);
    private boolean mPausable = true;
    private boolean isInProgress = false;
    private boolean isLoadingNow = false;

    private CastSession mCastSession;
    private SessionManager mSessionManager;

    private PlayerView mSimpleExoPlayerView;
    public ExoPlayer mExoPlayer;
    private ImageView ic_media_stop;
    private RelativeLayout payer_pause_play;
    private Boolean isLive = false;
    private String videoType;
    private String videoImage;
    private String videoTitle;
    private String videoSubTile;

    public CustomPlayerViewModel(Activity activity) {
        mActivity = activity;
    }

    public void onStart(PlayerView simpleExoPlayerView, Bundle bundle) {
        mSimpleExoPlayerView = simpleExoPlayerView;
        mUrl = bundle.getString("videoUrl");
        isLive = bundle.getBoolean("isLive");
        videoType = bundle.getString("videoType");
        videoTitle = bundle.getString("videoTitle");
        videoSubTile = bundle.getString("videoSubTile");
        videoImage = bundle.getString("videoImage");
        initPlayer();
        mSimpleExoPlayerView.setPlayer(mExoPlayer);

        preparePlayer(null,0);
        updateCastSessionAndSessionManager();

    }

    public void setPayerPausePlay(RelativeLayout payer_pause_play) {
            this.payer_pause_play =  payer_pause_play;
    }
    public void setMediaFull() {
        mSimpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        mExoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
    }
    public void setMediaNormal() {
        mSimpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
    }
    private void initPlayer() {
        // 1. Create a default TrackSelector
       /* Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
*/

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(mActivity.getApplicationContext()).build();
        ExoTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();

        DefaultTrackSelector trackSelector = new DefaultTrackSelector(mActivity.getApplicationContext());

        DefaultTrackSelector.Parameters newParameters = trackSelector
                .buildUponParameters()
                .setPreferredTextLanguage("en").build();
        trackSelector.setParameters(newParameters);

        // 2. Create a default LoadControl
        LoadControl loadControl = loadControl = new DefaultLoadControl.Builder().build();

        // 3. Create the player


        mExoPlayer = new ExoPlayer.Builder(mActivity.getApplicationContext()).setTrackSelector(trackSelector).setLoadControl(loadControl).build();
    }

    public void preparePlayer(Subtitle subtitle,long seekTo) {





    // Produces Extractor instances for parsing the media data.
//    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//    Handler mainHandler = new Handler();
//    MediaSource mediaSource = new HlsMediaSource(Uri.parse(
//    "https://live3-mediaset-it.akamaized.net/content/hls_clr_xo/live/channel(ch09)/Stream(02)/index.m3u8"),
//        dataSourceFactory, null, null);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mActivity,
                Util.getUserAgent(mActivity, "ExoPlayer2"), bandwidthMeter);
        // Produces Extractor instances for parsing the media data.
        // This is the MediaSource representing the media to be played.
        Uri videoUri = Uri.parse(mUrl);
        MediaSource mediaSource1;
        int sourceSize = 0;

        if (videoType.equals("mp4")){
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            //  mediaSource1 = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
           /* mediaSource1 =  new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .setExtractorsFactory(extractorsFactory)
                    .createMediaSource(videoUri);*/
            mediaSource1 =  new ProgressiveMediaSource.Factory(dataSourceFactory,extractorsFactory)
                    .createMediaSource(MediaItem.fromUri(videoUri));
            sourceSize++;
        }else if (videoType.equals("dash")){
           // mediaSource1 = new DashMediaSource(videoUri,dataSourceFactory, new DefaultDashChunkSource.Factory(dataSourceFactory),null,null);
            mediaSource1=  new DashMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
            sourceSize++;
        }else if (videoType.equals("m3u8")){

            mediaSource1 = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(videoUri));
            // mediaSource1 = new HlsMediaSource(videoUri, dataSourceFactory,  new DefaultDashChunkSource.Factory(dataSourceFactory), null,null);
            sourceSize++;
        }else{
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            //  mediaSource1 = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
            mediaSource1 =  new ProgressiveMediaSource.Factory(dataSourceFactory,extractorsFactory)
                    .createMediaSource(MediaItem.fromUri(videoUri));
            sourceSize++;
        }
        SingleSampleMediaSource subtitleSource = null;
      /*  if (subtitle!=null){

            if (subtitle.getType().equals("srt")) {




                Format textFormat = new Format.Builder().setId(null).setSampleMimeType(MimeTypes.APPLICATION_SUBRIP).setSelectionFlags(Format.NO_VALUE).setLanguage(subtitle.getLanguage()).setSelectionFlags(Format.NO_VALUE).setDrmInitData(null).build();
               // subtitleSource = new SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(subtitle.getUrl()), textFormat, C.TIME_UNSET);
                subtitleSource = new SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(subtitle.getUrl()), textFormat, C.TIME_UNSET);
            }else if (subtitle.getType().equals("vtt")){
                subtitleSource = new SingleSampleMediaSource(Uri.parse(subtitle.getUrl()), dataSourceFactory, new Format.Builder().setId(null).setSampleMimeType(MimeTypes.TEXT_VTT).setSelectionFlags(Format.NO_VALUE).setDrmInitData(null).setLanguage(subtitle.getLanguage()).build(), C.TIME_UNSET);
            }else if (subtitle.getType().equals("ass")){
                subtitleSource = new SingleSampleMediaSource(Uri.parse(subtitle.getUrl()), dataSourceFactory, new Format.Builder().setId(null).setSampleMimeType(MimeTypes.TEXT_SSA).setSelectionFlags(Format.NO_VALUE).setDrmInitData(null).setLanguage(subtitle.getLanguage()).build(), C.TIME_UNSET);
            }
            sourceSize++;
        }*/
        MediaSource mediaSource;

        if (subtitle!=null){
            MediaItem.SubtitleConfiguration subtitleConfig = new MediaItem.SubtitleConfiguration.Builder(Uri.parse(subtitle.getUrl())).setMimeType(MimeTypes.TEXT_VTT).setLanguage("en").setLabel(subtitle.getLanguage()).build();

            MediaSource textMediaSource = null;
            if (subtitle.getType().equals("srt")) {
                 textMediaSource = new SingleSampleMediaSource.Factory(dataSourceFactory)
                        .setTreatLoadErrorsAsEndOfStream(true)
                        .createMediaSource(subtitleConfig,C.TIME_UNSET);
            }else if (subtitle.getType().equals("vtt")){
                 textMediaSource = new SingleSampleMediaSource.Factory(dataSourceFactory)
                        .setTreatLoadErrorsAsEndOfStream(true)
                        .createMediaSource(subtitleConfig,C.TIME_UNSET);

            }else if (subtitle.getType().equals("ass")){
                textMediaSource  = new SingleSampleMediaSource.Factory(dataSourceFactory)
                        .setTreatLoadErrorsAsEndOfStream(true)
                        .createMediaSource(subtitleConfig,C.TIME_UNSET);

            }
            if (textMediaSource != null)
                mediaSource = new MergingMediaSource(mediaSource1, textMediaSource);
            else
                mediaSource = new MergingMediaSource(mediaSource1);

        }else{
            mediaSource = new MergingMediaSource(mediaSource1);
        }






        // Prepare the player with the source.
        mExoPlayer.setMediaSource(mediaSource);
        mExoPlayer.prepare();
        mExoPlayer.seekTo(seekTo);
        mExoPlayer.addListener(this);
        mExoPlayer.setPlayWhenReady(SHOULD_AUTO_PLAY);
       // mExoPlayer.setPlayWhenReady(true);

    }

    public void onPlayerClicked(final View view) {
        controlsVisible.set(true);
    }

    @Bindable
    public int getPlaybackImageRes() {
        if (!isPlaying()) {
            return R.drawable.ic_media_play;
        }
        if (!isPausable()) {
            return R.drawable.ic_media_stop;
        } else {
            return R.drawable.ic_media_pause;
        }
    }


    public void onPlayPauseClicked(final View view) {
        if (mExoPlayer == null) {
            return;
        }
        if (isPlaying()) {
            pause();
        } else {
            play();
        }
    }
    public void play() {
        if (mExoPlayer == null) {
            return;
        }
        mExoPlayer.setPlayWhenReady(true);
    }

    public void pause() {
        if (mExoPlayer == null) {
            return;
        }
        mExoPlayer.setPlayWhenReady(false);
    }

    private void updateCastSessionAndSessionManager() {
        if (mActivity instanceof PlayerActivity) {
            mCastSession = ((PlayerActivity) mActivity).getCastSession();
            mSessionManager = ((PlayerActivity) mActivity).getSessionManager();
        }
    }

    private void loadRemoteMedia(int position, boolean autoPlay) {
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            Log.d("MYAPP","remoteMediaClient == null");
            return;
        }

        remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
            @Override
            public void onStatusUpdated() {
                Log.d("MYAPP","onStatusUpdated");
                if (remoteMediaClient.getMediaStatus() != null) {
                    if (remoteMediaClient.getMediaStatus().getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING || remoteMediaClient.getMediaStatus().getPlayerState() == MediaStatus.PLAYER_STATE_BUFFERING) {
                        mSimpleExoPlayerView.setUseController(false);
                    } else {
                        mSimpleExoPlayerView.setUseController(true);
                    }
                    if (remoteMediaClient.getMediaStatus().getIdleReason() == MediaStatus.IDLE_REASON_FINISHED) {
                        mExoPlayer.seekToDefaultPosition();
                        mExoPlayer.setPlayWhenReady(false);
                    }
                }
            }
        });
        remoteMediaClient.load(new MediaLoadRequestData.Builder()
                .setMediaInfo(getMediaInfo())
                .setAutoplay(autoPlay)
                .setCurrentTime(position).build());
       /* remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
            @Override
            public void onStatusUpdated() {
                Log.d("MYAPP","onStatusUpdated");
                if (remoteMediaClient.getMediaStatus() != null) {
                    if (remoteMediaClient.getMediaStatus().getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING ||
                            remoteMediaClient.getMediaStatus().getPlayerState() == MediaStatus.PLAYER_STATE_BUFFERING) {
                        mSimpleExoPlayerView.setUseController(false);
                    } else {
                        mSimpleExoPlayerView.setUseController(true);
                    }
                    if (remoteMediaClient.getMediaStatus().getIdleReason() == MediaStatus.IDLE_REASON_FINISHED) {
                        mExoPlayer.seekToDefaultPosition();
                        mExoPlayer.setPlayWhenReady(false);
                    }
                }
            }

            @Override
            public void onMetadataUpdated() {
                Log.d("MYAPP","onMetadataUpdated");
            }

            @Override
            public void onQueueStatusUpdated() {
                Log.d("MYAPP","onQueueStatusUpdated");
            }

            @Override
            public void onPreloadStatusUpdated() {
                Log.d("MYAPP","onPreloadStatusUpdated");
            }

            @Override
            public void onSendingRemoteMediaRequest() {
                Log.d("MYAPP","onSendingRemoteMediaRequest");
            }

            @Override
            public void onAdBreakStatusUpdated() {

            }
        });
        remoteMediaClient.load(getMediaInfo(), autoPlay, position);*/
    }

    public void loadMedia(int position, boolean autoPlay) {
        updateCastSessionAndSessionManager();

        if (mCastSession == null) {
            mCastSession = mSessionManager.getCurrentCastSession();
        }
        if (mCastSession != null) {
            mSimpleExoPlayerView.setUseController(false);
            mExoPlayer.setPlayWhenReady(false);
            loadRemoteMedia(position, autoPlay);
        } else {
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private MediaInfo getMediaInfo() {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, videoTitle);
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, videoSubTile);
        movieMetadata.addImage(new WebImage(Uri.parse(videoImage)));
        movieMetadata.addImage(new WebImage(Uri.parse(videoImage)));
        List<MediaTrack> tracks =  new ArrayList<>();

        for (int i = 0; i < subtitlesForCast.size(); i++) {
            tracks.add(buildTrack(i+1,"text","captions",subtitlesForCast.get(i).getUrl(),subtitlesForCast.get(i).getLanguage(),"en-US"));
        }

        TextTrackStyle textTrackStyle =  new TextTrackStyle();
        int textColor = Color.WHITE;
        int backrgoundColor =Color.TRANSPARENT;
        PrefManager pref =new  PrefManager(mActivity.getApplicationContext());

        if (pref.getInt("subtitle_text_color") != 0){
            textColor = pref.getInt("subtitle_text_color");
        }
        if (pref.getInt("subtitle_background_color") != 0){
            backrgoundColor = pref.getInt("subtitle_background_color");
        }

        textTrackStyle.setBackgroundColor(backrgoundColor);
        textTrackStyle.setForegroundColor(textColor);

        MediaInfo mediaInfo = new MediaInfo.Builder(mUrl)
                .setStreamType(getStramType())
               // .setContentType("application/x-mpegurl")
                .setStreamDuration(getStramType())
                .setMetadata(movieMetadata)
                .setMediaTracks(tracks)
                .setTextTrackStyle(textTrackStyle)
                .build();
        Log.v("URLVIDEO",mUrl);
        return mediaInfo;
    }

    public int getStramType(){
        if (isLive)
            return MediaInfo.STREAM_TYPE_LIVE;
        else
            return MediaInfo.STREAM_TYPE_BUFFERED;

    }
    private static MediaTrack buildTrack(long id, String type, String subType, String contentId,
                                         String name, String language) {

        int trackType = MediaTrack.TYPE_UNKNOWN;
        if ("text".equals(type)) {
            trackType = MediaTrack.TYPE_TEXT;
        } else if ("video".equals(type)) {
            trackType = MediaTrack.TYPE_VIDEO;
        } else if ("audio".equals(type)) {
            trackType = MediaTrack.TYPE_AUDIO;
        }

        int trackSubType = MediaTrack.SUBTYPE_NONE;
        if (subType != null) {
            if ("captions".equals(type)) {
                trackSubType = MediaTrack.SUBTYPE_CAPTIONS;
            } else if ("subtitle".equals(type)) {
                trackSubType = MediaTrack.SUBTYPE_SUBTITLES;
            }
        }

        return new MediaTrack.Builder(id, trackType)
                .setContentType(MediaFormat.MIMETYPE_TEXT_VTT)
                .setName(name)
                .setSubtype(trackSubType)
                .setContentId(contentId)
                .setLanguage(language).build();
    }
    private boolean isPlaying() {
        return mExoPlayer != null && mExoPlayer.getPlayWhenReady();
    }

    private boolean isPausable() {
        return mPausable;
    }

    public void setPausable(boolean pausable) {
        mPausable = pausable;
//        notifyPropertyChanged(BR.pausable);
    }

    public ExoPlayer getExoPlayer() {
        return mExoPlayer;
    }

    public PlayerView getSimpleExoPlayerView() {
        return mSimpleExoPlayerView;
    }

    public void setIsInProgress(boolean isInProgress) {
        this.isInProgress = isInProgress;
    }

    @Bindable
    public boolean getLoadingComplete() {
        return loadingComplete;
    }
    @Bindable
    public boolean isLoaidingNow() {
        Log.i("TEST", "ExoPlayer Changed ");
      /*  if (isLoadingNow)
            payer_pause_play.setVisibility(View.GONE);
        else
            payer_pause_play.setVisibility(View.VISIBLE);
        */
        return isLoadingNow;
    }
    private void setLoadingComplete(boolean complete) {
        loadingComplete = complete;
        notifyPropertyChanged(BR.loadingComplete);
    }

    @Bindable
    public boolean getControlsVisible() {
        return controlsVisible.get();
    }






    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        setLoadingComplete(!isLoading);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY && playWhenReady && !isInProgress) {
            loadMedia(0, true);
        } else if (playbackState == ExoPlayer.STATE_ENDED) {
            mExoPlayer.seekToDefaultPosition();
            mExoPlayer.setPlayWhenReady(false);
            isInProgress = false;
        }
        if (playbackState ==  Player.STATE_BUFFERING ){
            isLoadingNow =  true;
            notifyPropertyChanged(BR.loaidingNow);
        }
        if (playbackState ==  Player.STATE_READY){
            isLoadingNow =  false;
            notifyPropertyChanged(BR.loaidingNow);
        }

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }




    @Override
    public void onPositionDiscontinuity(int reason) {

    }




    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }


    public void setSubtitilesList(ArrayList<Subtitle> _subtitlesForCast) {
        subtitlesForCast =  _subtitlesForCast;
    }
}
