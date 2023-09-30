package com.ahmad.makkitvofficial.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmad.makkitvofficial.config.Global;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdkUtils;
import com.ahmad.makkitvofficial.R;

public class AdsActivity extends AppCompatActivity {
    MaxInterstitialAd maxInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        showAppLovinBanner();
        findViewById(R.id.ads).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maxInterstitialAd = new MaxInterstitialAd(Global.appLovinInterstitial, AdsActivity.this);
                maxInterstitialAd.setListener(new MaxAdListener() {
                    @Override
                    public void onAdLoaded(MaxAd ad) {
                        Toast.makeText(AdsActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();

                        maxInterstitialAd.showAd(Global.appLovinInterstitial);

                    }

                    @Override
                    public void onAdDisplayed(MaxAd ad) {
                        Toast.makeText(AdsActivity.this, "onAdDisplayed", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "The ad was shown.");
                    }

                    @Override
                    public void onAdHidden(MaxAd ad) {
                        Toast.makeText(AdsActivity.this, "onAdHidden", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onAdClicked(MaxAd ad) {
                        Toast.makeText(AdsActivity.this, "onAdClicked", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, MaxError error) {
                        Toast.makeText(AdsActivity.this, "onAdLoadFailed"+" --- "+error.getMessage()+" -- "+error.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                        Toast.makeText(AdsActivity.this, "onAdDisplayFailed"+" --- "+error.getMessage()+" -- "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                // Load the first ad
                maxInterstitialAd.loadAd();
            }
        });
    }
    public void showAppLovinBanner(){
        MaxAdView adView = new MaxAdView(Global.appLovinBanner, this );
        // MaxAdView adView = new MaxAdView( prefManager.getString("ADMIN_BANNER_ADMOB_ID"), this );
        adView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {
                Toast.makeText(AdsActivity.this, "onAdExpanded", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {
                Toast.makeText(AdsActivity.this, "onAdCollapsed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                adView.setVisibility(View.VISIBLE);
                Toast.makeText(AdsActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                Toast.makeText(AdsActivity.this, "onAdDisplayed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdHidden(MaxAd ad) {
                Toast.makeText(AdsActivity.this, "onAdHidden", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Toast.makeText(AdsActivity.this, "Banner_onAdLoadFailed"+" --- "+error.getMessage()+" -- "+error.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Toast.makeText(AdsActivity.this, "Banner_onAdLoadFailed"+" --- "+error.getMessage()+" -- "+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        adView.setVisibility(View.GONE);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;

        int heightDp = MaxAdFormat.BANNER.getAdaptiveSize( this ).getHeight();
        int heightPx = AppLovinSdkUtils.dpToPx( this, heightDp );

        adView.setLayoutParams( new FrameLayout.LayoutParams( width, heightPx ) );






        LinearLayout linear_layout_ads =  (LinearLayout) findViewById(R.id.linear_layout_ads);


        linear_layout_ads.addView(adView);



        // Load the ad
        adView.loadAd();
    }
}