package sdk.peak.test.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.peak.PeakAsyncAdRequest;
import com.peak.PeakSdk;
import com.peak.PeakSdkListener;
import com.peak.PeakSdkUiHelper;
import com.peak.exception.PeakSdkException;
import com.peak.nativeads.PeakNativeAd;
import com.squareup.picasso.Picasso;

/**
 * Created by Darko Sarev on 9/13/2016.
 */

public class ProxyActivity extends AppCompatActivity {

    private static final String PEAK_APP_ID = "2662c914e72da398";
    private static final String INTERSTITIAL_ZONE_ID = "132147";
    private static final String BANNER_ZONE_ID = "132174";
    private static final String NATIVE_ZONE_ID = "132187";
    private boolean interstitialShown = false;
    private boolean bannerShown = false;

    private ImageView mainImageView;
    private ImageView logoImageView;
    private ImageView privacyIconImageView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private Button adActionButton;
    private ProgressBar progressBar;
    private final Handler uiThreadHandler = new Handler();
    PeakNativeAd nativeAd;

    private static final String TAG = "PEAKKK";

    PeakSdkUiHelper uiHelper = new PeakSdkUiHelper(ProxyActivity.this);

    PeakAsyncAdRequest asyncAdRequest,asyncAdRequest1,asyncAdRequest2;
    PeakAsyncAdRequest.PeakAsyncAdRequestListener asyncAdRequestListener = new PeakAsyncAdRequest.PeakAsyncAdRequestListener() {
        @Override
        public void onAdReady(String adZoneId) {
            //Show the ad with adZoneId
            //Request stops itself when the ad is ready.
            if (PeakSdk.checkAdAvailable(adZoneId)) {
                PeakSdk.showInterstitial(adZoneId);
            }
        }
    };
    PeakAsyncAdRequest.PeakAsyncAdRequestListener asyncAdRequestListener1 = new PeakAsyncAdRequest.PeakAsyncAdRequestListener() {
        @Override
        public void onAdReady(String adZoneId) {
            //Show the ad with adZoneId
            //Request stops itself when the ad is ready.
            if (PeakSdk.checkAdAvailable(adZoneId)) {
                PeakSdk.showBanner(adZoneId);
            }
        }
    };
    PeakAsyncAdRequest.PeakAsyncAdRequestListener asyncAdRequestListener2 = new PeakAsyncAdRequest.PeakAsyncAdRequestListener() {
        @Override
        public void onAdReady(String adZoneId) {
            //Show the ad with adZoneId
            //Request stops itself when the ad is ready.
            if (PeakSdk.checkAdAvailable(adZoneId)) {
                PeakSdk.showNativeAd(adZoneId);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxy);

        PeakSdkListener peakSdkListener = new PeakSdkListener() {
            @Override
            public void onInitializationSuccess() {
                Log.d(TAG, "onInitializationSuccess: ");
                startAdRequest();
                startAdRequest1();
                startAdRequest2();
            }

            @Override
            public void onInitializationFailed(PeakSdkException e) {
                Log.d(TAG, "onInitializationFailed: ");
            }

            @Override
            public void onBannerShowSuccess(String s) {

            }

            @Override
            public void onBannerShowFailed(String s, PeakSdkException e) {

            }

            @Override
            public void onInterstitialShowSuccess(String s) {
                Log.d(TAG, "onInterstitialShowSuccess: ");

            }

            @Override
            public void onInterstitialShowFailed(String s, PeakSdkException e) {
                Log.d(TAG, "onInterstitialShowFailed: ");
            }

            @Override
            public void onInterstitialClosed(String s) {
                Log.d(TAG, "onInterstitialClosed: ");
            }

            @Override
            public void onCompletedRewardExperience(String s) {
                Log.d(TAG, "onCompletedRewardExperience: ");
            }

            @Override
            public void onNativeAdShowSuccess(String s) {

            }

            @Override
            public void onNativeAdShowFailed(String s, PeakSdkException e) {

            }
        };
        PeakSdk.initialize(PEAK_APP_ID, uiHelper, peakSdkListener);

    }

    private void startAdRequest() {
        asyncAdRequest = PeakSdk.createAdRequest(INTERSTITIAL_ZONE_ID);
        if (asyncAdRequest != null) {
            asyncAdRequest.start(asyncAdRequestListener);
        }
    }
    private void startAdRequest1() {
        asyncAdRequest1 = PeakSdk.createAdRequest(BANNER_ZONE_ID);
        if (asyncAdRequest1 != null) {
            asyncAdRequest1.start(asyncAdRequestListener1);
        }
    }
    private void startAdRequest2() {
        asyncAdRequest2 = PeakSdk.createAdRequest(NATIVE_ZONE_ID);
        if (asyncAdRequest2 != null) {
            asyncAdRequest2.start(asyncAdRequestListener2);
        }
    }


    @Override
    protected void onPause() {
        uiHelper.pause();
        if (asyncAdRequest != null) {
            asyncAdRequest.cancel();
        }
        if (asyncAdRequest1 != null) {
            asyncAdRequest1.cancel();
        }
        if (asyncAdRequest2 != null) {
            asyncAdRequest2.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.resume();
    }

    @Override
    protected void onDestroy() {
        uiHelper.destroy();
        super.onDestroy();
    }

    private void findViews() {
        mainImageView = (ImageView) findViewById(R.id.mainImageView);
        logoImageView = (ImageView) findViewById(R.id.logoImageView);
        privacyIconImageView = (ImageView) findViewById(R.id.privacyInformationIconImageView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        adActionButton = (Button) findViewById(R.id.interactWithAdButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void showNativeAd() {
        if (PeakSdk.checkAdAvailable(NATIVE_ZONE_ID)) {
            nativeAd = PeakSdk.showNativeAd(NATIVE_ZONE_ID);
            if (nativeAd != null) {
                progressBar.setVisibility(View.GONE);
                PeakSdk.trackNativeAdShown(NATIVE_ZONE_ID);
                bindNativeAdToViews(nativeAd);
            }
        }
    }

    private void bindNativeAdToViews(PeakNativeAd nativeAd) {
        Picasso imageLoader = Picasso.with(this);
        fillMainImage(nativeAd, imageLoader);
        fillIcon(nativeAd, imageLoader);
        fillPrivacyInformationIcon(nativeAd, imageLoader);
        titleTextView.setText(nativeAd.getTitle());
        descriptionTextView.setText(nativeAd.getText());
        adActionButton.setVisibility(View.VISIBLE);
        adActionButton.setText(nativeAd.getActionText());
        adActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PeakSdk.handleNativeAdClicked(NATIVE_ZONE_ID);
            }
        });
    }

    private void fillMainImage(PeakNativeAd nativeAd, Picasso imageLoader) {
        String mainImage = nativeAd.getMainImage();
        if (!TextUtils.isEmpty(mainImage)) {
            imageLoader.load(mainImage).into(mainImageView);
        }
    }

    private void fillIcon(PeakNativeAd nativeAd, Picasso imageLoader) {
        String icon = nativeAd.getIcon();
        if (!TextUtils.isEmpty(icon)) {
            imageLoader.load(icon).into(logoImageView);
        }
    }

    private void fillPrivacyInformationIcon(PeakNativeAd nativeAd, Picasso imageLoader) {
        String privacyIcon = nativeAd.getPrivacyIcon();
        if (!TextUtils.isEmpty(privacyIcon)) {
            imageLoader.load(privacyIcon).into(privacyIconImageView);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void showBanner(View banner) {
        Log.d(TAG, "showBanner: true");
        ((ViewGroup) findViewById(R.id.bannerContainer)).addView(banner);
    }
}
