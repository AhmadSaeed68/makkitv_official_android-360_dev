package com.ahmad.makkitvofficial.ui.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.applovin.mediation.MaxAd;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder;
import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.ahmad.makkitvofficial.Provider.PrefManager;
import com.ahmad.makkitvofficial.R;
import com.ahmad.makkitvofficial.entity.Data;
import com.ahmad.makkitvofficial.ui.activities.ActorsActivity;
import com.ahmad.makkitvofficial.ui.activities.GenreActivity;
import com.ahmad.makkitvofficial.ui.activities.HomeActivity;
import com.ahmad.makkitvofficial.ui.activities.MyListActivity;
import com.ahmad.makkitvofficial.ui.activities.SearchActivity;
import com.ahmad.makkitvofficial.ui.activities.TopActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private  Activity activity;
    private List<Data> dataList = new ArrayList<>();
    private SlideAdapter slide_adapter;
    private ChannelAdapter channelAdapter;
    private LinearLayoutManager linearLayoutManagerChannelAdapter;
    private ActorAdapter actorAdapter;
    private LinearLayoutManager linearLayoutManagerActorAdapter;
    private LinearLayoutManager linearLayoutManagerGenreAdapter;
    private PosterAdapter posterAdapter;
    private int slide_count = 0;


    // private Timer mTimer;
    public HomeAdapter(List<Data> dataList,  Activity activity) {
        this.dataList = dataList;
        this.activity=activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0: {
                View v0 = inflater.inflate(R.layout.item_empty, parent, false);
                viewHolder = new EmptyHolder(v0);
                break;
            }
            case 1: {
                View v1 = inflater.inflate(R.layout.item_slides, parent, false);
                viewHolder = new SlideHolder(v1);
                break;
            }
            case 2: {
                View v2 = inflater.inflate(R.layout.item_channels, parent, false);
                viewHolder = new ChannelHolder(v2);
                break;
            }
            case 3: {
                View v3 = inflater.inflate(R.layout.item_actors, parent, false);
                viewHolder = new ActorHolder(v3);
                break;
            }
            case 4: {
                View v4 = inflater.inflate(R.layout.item_genres, parent, false);
                viewHolder = new GenreHolder(v4);
                break;
            }
            case 5: {
                View v5 = inflater.inflate(R.layout.item_admob_native_ads, parent, false);
                viewHolder = new AdmobNativeHolder(v5);
                break;
            }
            case 6: {
                View v6  = inflater.inflate(R.layout.item_max_native_ads, parent, false);
                viewHolder = new MaxNativeHolder(v6);
                break;
            }
            case 7: {
                View v7  = inflater.inflate(R.layout.item_search, parent, false);
                viewHolder = new SearchHolder(v7);
                break;
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder_parent, int position) {
        switch (getItemViewType(position)){
            case 1:

                final SlideHolder holder = (SlideHolder) holder_parent;
                slide_count = dataList.get(position).getSlides().size();
                slide_adapter = new SlideAdapter(activity, dataList.get(position).getSlides());
                holder.view_pager_slide.setAdapter(slide_adapter);
                holder.view_pager_slide.setOffscreenPageLimit(1);
                holder.view_pager_slide.setClipToPadding(false);
                holder.view_pager_slide.setPageMargin(0);
                holder.view_pager_indicator.setupWithViewPager(holder.view_pager_slide);
                holder.view_pager_slide.setCurrentItem(0);
                slide_adapter.notifyDataSetChanged();
                break;
            case 2:
                final ChannelHolder holder_channel = (ChannelHolder) holder_parent;
                this.linearLayoutManagerChannelAdapter=  new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                this.channelAdapter =new ChannelAdapter(dataList.get(position).getChannels(),activity);
                holder_channel.recycle_view_channels_item.setHasFixedSize(true);
                holder_channel.recycle_view_channels_item.setAdapter(channelAdapter);
                holder_channel.recycle_view_channels_item.setLayoutManager(linearLayoutManagerChannelAdapter);
                channelAdapter.notifyDataSetChanged();
                holder_channel.image_view_item_channel_more.setOnClickListener(v -> {
                    ((HomeActivity) activity).goToTV();
                });
                break;
            case 3:
                final ActorHolder holder_actor = (ActorHolder) holder_parent;
                this.linearLayoutManagerActorAdapter=  new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                this.actorAdapter =new ActorAdapter(dataList.get(position).getActors(),activity);
                holder_actor.recycle_view_actors_item_actors.setHasFixedSize(true);
                holder_actor.recycle_view_actors_item_actors.setAdapter(actorAdapter);
                holder_actor.recycle_view_actors_item_actors.setLayoutManager(linearLayoutManagerActorAdapter);
                actorAdapter.notifyDataSetChanged();
                holder_actor.image_view_item_actors_more.setOnClickListener(v -> {
                    Intent intent  =  new Intent(activity.getApplicationContext(), ActorsActivity.class);
                    (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                });
                break;
            case 4:
                final GenreHolder holder_genres = (GenreHolder) holder_parent;
                holder_genres.text_view_item_genre_title.setText(dataList.get(position).getGenre().getTitle());
                holder_genres.image_view_item_genre_more.setOnClickListener(v-> {
                    if (dataList.get(position).getGenre().getId() == -1){
                        Intent intent  =  new Intent(activity.getApplicationContext(), TopActivity.class);
                        intent.putExtra("order", "rating");
                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                    }else if (dataList.get(position).getGenre().getId() == 0){
                        Intent intent  =  new Intent(activity.getApplicationContext(), TopActivity.class);
                        intent.putExtra("order", "views");
                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                    }else if (dataList.get(position).getGenre().getId() == -2){
                        Intent intent  =  new Intent(activity.getApplicationContext(), MyListActivity.class);
                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                    }else{
                        Intent intent  =  new Intent(activity.getApplicationContext(), GenreActivity.class);
                        intent.putExtra("genre", dataList.get(position).getGenre());
                        (activity).startActivity(intent, ActivityOptionsCompat.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight()).toBundle());
                    }

                });
                this.linearLayoutManagerGenreAdapter=  new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                if (dataList.get(position).getGenre().getId() == -2)
                    this.posterAdapter =new PosterAdapter(dataList.get(position).getGenre().getPosters(),activity,true);
                else
                    this.posterAdapter =new PosterAdapter(dataList.get(position).getGenre().getPosters(),activity);

                holder_genres.recycle_view_posters_item_genre.setHasFixedSize(true);
                holder_genres.recycle_view_posters_item_genre.setAdapter(posterAdapter);
                holder_genres.recycle_view_posters_item_genre.setLayoutManager(linearLayoutManagerGenreAdapter);
                posterAdapter.notifyDataSetChanged();

                break;
            case 5:{
                final AdmobNativeHolder holder_admob = (AdmobNativeHolder) holder_parent;

                holder_admob.adLoader.loadAd(new AdRequest.Builder().build());

                break;
            }
            case 7:{
                final SearchHolder holder_admob = (SearchHolder) holder_parent;


                holder_admob.edit_text_home_activity_search.setOnEditorActionListener((v,actionId,event) -> {
                    if (holder_admob.edit_text_home_activity_search.getText().length()>0){
                        Intent intent =  new Intent(activity, SearchActivity.class);
                        intent.putExtra("query",holder_admob.edit_text_home_activity_search.getText().toString());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);

                        holder_admob.edit_text_home_activity_search.setText("");
                    }
                    return false;
                });
                holder_admob.edit_text_home_activity_search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (holder_admob.edit_text_home_activity_search.getText().length()>0) {
                            holder_admob.image_view_activity_home_close_search.setVisibility(View.VISIBLE);

                        }else{
                            holder_admob.image_view_activity_home_close_search.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                holder_admob.image_view_activity_home_close_search.setOnClickListener(v->{
                    holder_admob.edit_text_home_activity_search.setText("");
                });
                holder_admob.image_view_activity_home_search.setOnClickListener(v->{
                    if (holder_admob.edit_text_home_activity_search.getText().length()>0) {

                        Intent intent =  new Intent(activity, SearchActivity.class);
                        intent.putExtra("query",holder_admob.edit_text_home_activity_search.getText().toString());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);

                        holder_admob.edit_text_home_activity_search.setText("");
                    }
                });

                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    @Override
    public int getItemViewType(int position) {
        int type = 0;
        if(dataList.get(position).getSlides() != null){
            type = 1;
        }
        if(dataList.get(position).getChannels() != null){
            type = 2;
        }
        if(dataList.get(position).getActors() != null){
            type = 3;
        }
        if(dataList.get(position).getGenre() != null){
            type = 4;
        }
        if (dataList.get(position).getViewType() == 5){
            type = 5;

        }
        if (dataList.get(position).getViewType() == 6){
            type = 6;

        }
        if (dataList.get(position).getViewType() == 7){
            type = 7;

        }
        return type;
    }
    private class SlideHolder extends RecyclerView.ViewHolder {
        private final ViewPagerIndicator view_pager_indicator;
        private final ViewPager view_pager_slide;
        public SlideHolder(View itemView) {
            super(itemView);
            this.view_pager_indicator=(ViewPagerIndicator) itemView.findViewById(R.id.view_pager_indicator);
            this.view_pager_slide=(ViewPager) itemView.findViewById(R.id.view_pager_slide);
            pageSwitcher(5);

        }
        Timer timer;
        int page = 0;

        public void pageSwitcher(int seconds) {
            timer = new Timer(); // At this line a new Thread will be created
            timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000); // delay
            // in
            // milliseconds
        }

        // this is an inner class...
        class RemindTask extends TimerTask {

            @Override
            public void run() {

                // As the TimerTask run on a seprate thread from UI thread we have
                // to call runOnUiThread to do work on UI thread.
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (page == slide_count) { // In my case the number of pages are 5
                            page=0;
                            view_pager_slide.setCurrentItem(page);
                            page++;
                        } else {
                            view_pager_slide.setCurrentItem(page);
                            page++;
                        }
                    }
                });

            }
        }
    }

    private class ChannelHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_channels_item;
        private final ImageView image_view_item_channel_more;
        public ChannelHolder(View itemView) {
            super(itemView);
            this.recycle_view_channels_item=(RecyclerView) itemView.findViewById(R.id.recycle_view_channels_item);
            this.image_view_item_channel_more=  (ImageView) itemView.findViewById(R.id.image_view_item_channel_more);

        }
    }
    private class SearchHolder extends RecyclerView.ViewHolder {
        EditText edit_text_home_activity_search;
        ImageView image_view_activity_home_close_search;
        ImageView image_view_activity_home_search;
        public SearchHolder(View itemView) {
            super(itemView);

            this.edit_text_home_activity_search =  (EditText) itemView.findViewById(R.id.edit_text_home_activity_search);
            this.image_view_activity_home_close_search =  (ImageView) itemView.findViewById(R.id.image_view_activity_home_close_search);
            this.image_view_activity_home_search =  (ImageView) itemView.findViewById(R.id.image_view_activity_home_search);
        }
    }

    private class ActorHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_actors_item_actors;
        private final ImageView image_view_item_actors_more;

        public ActorHolder(View itemView) {
            super(itemView);
            this.recycle_view_actors_item_actors=  (RecyclerView) itemView.findViewById(R.id.recycle_view_actors_item_actors);
            this.image_view_item_actors_more=  (ImageView) itemView.findViewById(R.id.image_view_item_actors_more);
        }
    }
    private class GenreHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_posters_item_genre;
        private final TextView text_view_item_genre_title;
        private final ImageView image_view_item_genre_more;

        public GenreHolder(View itemView) {
            super(itemView);
            this.recycle_view_posters_item_genre=  (RecyclerView) itemView.findViewById(R.id.recycle_view_posters_item_genre);
            this.text_view_item_genre_title=  (TextView) itemView.findViewById(R.id.text_view_item_genre_title);
            this.image_view_item_genre_more=  (ImageView) itemView.findViewById(R.id.image_view_item_genre_more);
        }
    }
    public class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }
    public class MaxNativeHolder extends RecyclerView.ViewHolder {
        private MaxNativeAdLoader nativeAdLoader;
        private MaxAd loadedNativeAd;
        private FrameLayout         native_ad_layout;

        public MaxNativeHolder(@NonNull View itemView) {
            super(itemView);
            this.native_ad_layout = itemView.findViewById(R.id.native_ad_layout);
            PrefManager prefManager= new PrefManager(activity);
            nativeAdLoader = new MaxNativeAdLoader( prefManager.getString("ADMIN_NATIVE_ADMOB_ID"), activity );
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(MaxNativeAdView nativeAdView, MaxAd nativeAd) {
                    if ( loadedNativeAd != null )
                    {
                        nativeAdLoader.destroy( loadedNativeAd );
                    }

                    // Save ad for cleanup.
                    loadedNativeAd = nativeAd;

                    native_ad_layout.removeAllViews();
                    native_ad_layout.addView( nativeAdView );
                }
            });

            nativeAdLoader.loadAd(createNativeAdView());
        }
        private MaxNativeAdView createNativeAdView()
        {
            MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder( R.layout.native_max_ad_view )
                    .setTitleTextViewId( R.id.title_text_view )
                    .setBodyTextViewId( R.id.body_text_view )
                    .setAdvertiserTextViewId( R.id.advertiser_textView )
                    .setIconImageViewId( R.id.icon_image_view )
                    .setMediaContentViewGroupId( R.id.media_view_container )
                    .setOptionsContentViewGroupId( R.id.options_view )
                    .setCallToActionButtonId( R.id.cta_button )
                    .build();

            return new MaxNativeAdView(binder,activity);
        }
    }
    public class AdmobNativeHolder extends RecyclerView.ViewHolder {
        private final AdLoader adLoader;
        private com.google.android.gms.ads.nativead.NativeAd nativeAd;
        private FrameLayout frameLayout;

        public AdmobNativeHolder(@NonNull View itemView) {
            super(itemView);

            PrefManager prefManager= new PrefManager(activity);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.fl_adplaceholder);
            AdLoader.Builder builder = new AdLoader.Builder(activity, prefManager.getString("ADMIN_NATIVE_ADMOB_ID"));



            builder.forNativeAd(
                    nativeAd -> {
                        // If this callback occurs after the activity is destroyed, you must call
                        // destroy and return or you may get a memory leak.

                        if (nativeAd == null) {
                            nativeAd.destroy();
                            return;
                        }

                        AdmobNativeHolder.this.nativeAd = nativeAd;
                        FrameLayout frameLayout = activity.findViewById(R.id.fl_adplaceholder);
                        NativeAdView adView =
                                (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_unified, null);
                        populateNativeAdView(nativeAd, adView);
                        if(frameLayout != null){
                            frameLayout.removeAllViews();
                            frameLayout.addView(adView);
                        }

                    });

            VideoOptions videoOptions =
                    new VideoOptions.Builder().setStartMuted(true).build();

            NativeAdOptions adOptions =
                    new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

            builder.withNativeAdOptions(adOptions);

            adLoader =
                    builder
                            .withAdListener(
                                    new AdListener() {
                                        @Override
                                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                                            String error =
                                                    String.format(
                                                            "domain: %s, code: %d, message: %s",
                                                            loadAdError.getDomain(),
                                                            loadAdError.getCode(),
                                                            loadAdError.getMessage());
                                            Toast.makeText(
                                                    activity,
                                                    "Failed to load native ad with error " + error,
                                                    Toast.LENGTH_SHORT)
                                                    .show();

                                            Log.d("ADMOB_TES", error);

                                        }
                                    })
                            .build();

            adLoader.loadAd(new AdRequest.Builder().build());

        }
    }

    /**
     * Populates a {@link NativeAdView} object with data from a given {@link com.google.android.gms.ads.nativead.NativeAd}.
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView the view to be populated
     */
    private void populateNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView((com.google.android.gms.ads.nativead.MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getMediaContent().getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {


            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.

                    super.onVideoEnd();
                }
            });
        } else {

        }
    }

}
