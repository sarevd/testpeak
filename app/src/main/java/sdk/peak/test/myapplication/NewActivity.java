package sdk.peak.test.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.peak.PeakSdk;
import com.peak.PeakSdkListener;
import com.peak.PeakSdkUiHelper;
import com.peak.exception.PeakSdkBannerShowFailedException;
import com.peak.exception.PeakSdkException;
import com.peak.nativeads.PeakNativeAd;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by darko on 8/9/2016.
 */
public class NewActivity extends AppCompatActivity {
//
    private static final String PEAK_APP_ID = "5b1656281bbad6b8";
    private static final String PEAK_INTERSTITIAL_ZONE_ID = "27022";
    private static final String PEAK_BANNER_ZONE_ID = "27041";
    private static final String NATIVE_AD_ID = "27050";
    // new ids
//    private static final String PEAK_APP_ID = "343b9d1657f5f935";
//    private static final String PEAK_INTERSTITIAL_ZONE_ID = "59638";
//    private static final String PEAK_BANNER_ZONE_ID = "59665";
//    private static final String NATIVE_AD_ID = "59678";

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

    PeakSdkUiHelper uiHelper = new PeakSdkUiHelper(NewActivity.this);
    private LinearLayout bannerContainer;
    private String TAG = "peakkkkk";


    private final ScheduledExecutorService bannerAdAvailabilityExecutor =
            Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService nativeAdAvailabilityExecutor =
            Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService interstitialAdAvailabilityExecutor =
            Executors.newSingleThreadScheduledExecutor();


    private static final int AD_CHECK_DELAY_SECONDS = 1;


    private static final int NATIVE_AD_AVAILABILITY_CHECK_DELAY_SECONDS = 1;
    private static final int ACTIVITY_FINISH_IF_AD_NOT_SHOWN_DELAY = 30 * 1000;


    private final Runnable finishActivityRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isNativeShown()) {
//                Toast.makeText(this, R.string.error_native_ad_not_provided,
//                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_layout);
        findViews();
        PeakSdkListener peakSdkListener = new PeakSdkListener() {
            @Override
            public void onInitializationSuccess() {
                Log.d(TAG, "onInitializationSuccess: ");
                PeakSdk.showInterstitial(PEAK_INTERSTITIAL_ZONE_ID);
                PeakSdk.showBanner(PEAK_BANNER_ZONE_ID);
            }

            @Override
            public void onInitializationFailed(PeakSdkException e) {
                Log.d(TAG, "onInitializationFailed: ");
            }

            @Override
            public void onBannerShowSuccess(String s) {
                Log.d(TAG, "onBannerShowSuccess: ");
            }

            @Override
            public void onBannerShowFailed(String s, PeakSdkException e) {
                Log.d(TAG, "onBannerShowFailed: ");
//                bannerAdAvailabilityExecutor.scheduleWithFixedDelay(
//                        getShowBannerRunnable(), 0, AD_CHECK_DELAY_SECONDS, TimeUnit.SECONDS);
//                if (PeakSdk.checkAdAvailable(PEAK_BANNER_ZONE_ID)) {
//                    final View banner = PeakSdk.showBanner(PEAK_BANNER_ZONE_ID);
//                    uiThreadHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            showBanner(banner);
//                        }
//                    });
//                }
            }

            @Override
            public void onInterstitialShowSuccess(String s) {
                Log.d(TAG, "onInterstitialShowSuccess: ");
            }

            @Override
            public void onInterstitialShowFailed(String s, PeakSdkException e) {
                Log.d(TAG, "onInterstitialShowFailed: ");
                if (PeakSdk.checkAdAvailable(PEAK_INTERSTITIAL_ZONE_ID)) {
                    //  PeakSdk.showInterstitial(PEAK_INTERSTITIAL_ZONE_ID);
                }
            }

            @Override
            public void onInterstitialClosed(String s) {
                Log.d(TAG, "onInterstitialClosed: ");
                if (PeakSdk.checkAdAvailable(PEAK_INTERSTITIAL_ZONE_ID)) {
                    //  PeakSdk.showInterstitial(PEAK_INTERSTITIAL_ZONE_ID);
                }
            }

            @Override
            public void onCompletedRewardExperience(String s) {
                Log.d(TAG, "onCompletedRewardExperience: ");
                if (PeakSdk.checkAdAvailable(PEAK_INTERSTITIAL_ZONE_ID)) {
                    //  PeakSdk.showInterstitial(PEAK_INTERSTITIAL_ZONE_ID);
                }
            }

            @Override
            public void onNativeAdShowSuccess(String s) {
                Log.d(TAG, "onNativeAdShowSuccess: ");
            }

            @Override
            public void onNativeAdShowFailed(String s, PeakSdkException e) {
                Log.d(TAG, "onNativeAdShowFailed: ");
            }
        };

        PeakSdk.initialize(PEAK_APP_ID, uiHelper, peakSdkListener);


//        if (PeakSdk.checkAdAvailable(PEAK_BANNER_ZONE_ID)) {
//            final View banner = PeakSdk.showBanner(PEAK_BANNER_ZONE_ID);
//            uiThreadHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    showBanner(banner);
//                }
//            });
//        }
        bannerAdAvailabilityExecutor.scheduleWithFixedDelay(
                getShowBannerRunnable(), 0, AD_CHECK_DELAY_SECONDS, TimeUnit.SECONDS);
        nativeAdAvailabilityExecutor.scheduleWithFixedDelay(
                getShowNativeRunnable(), NATIVE_AD_AVAILABILITY_CHECK_DELAY_SECONDS,
                NATIVE_AD_AVAILABILITY_CHECK_DELAY_SECONDS, TimeUnit.SECONDS);
      //  uiThreadHandler.postDelayed(finishActivityRunnable, ACTIVITY_FINISH_IF_AD_NOT_SHOWN_DELAY);
        interstitialAdAvailabilityExecutor.scheduleWithFixedDelay(
                getShowInterstitialRunnable(), 0, AD_CHECK_DELAY_SECONDS, TimeUnit.SECONDS);
    }


    private boolean isNativeShown() {
        return nativeAd != null;
    }

    private Runnable getShowInterstitialRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || interstitialShown) {
                    interstitialAdAvailabilityExecutor.shutdownNow();
                }
                if (PeakSdk.checkAdAvailable(PEAK_INTERSTITIAL_ZONE_ID)) {
                    interstitialShown = true;
                    PeakSdk.showInterstitial(PEAK_INTERSTITIAL_ZONE_ID);
                }
            }
        };
    }

    private Runnable getShowBannerRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || bannerShown) {
                    bannerAdAvailabilityExecutor.shutdownNow();
                }
                if (PeakSdk.checkAdAvailable(PEAK_BANNER_ZONE_ID)) {
                    bannerShown = true;
                    final View banner = PeakSdk.showBanner(PEAK_BANNER_ZONE_ID);
                    uiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showBanner(banner);
                        }
                    });
                }
            }
        };
    }


    private Runnable getShowNativeRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    nativeAdAvailabilityExecutor.shutdownNow();
                }
                if (isNativeShown()) {
                    return;
                }
                uiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showNativeAd();
                    }
                });
            }
        };
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
        if (PeakSdk.checkAdAvailable(NATIVE_AD_ID)) {
            nativeAd = PeakSdk.showNativeAd(NATIVE_AD_ID);
            if (nativeAd != null) {
                progressBar.setVisibility(View.GONE);
                PeakSdk.trackNativeAdShown(NATIVE_AD_ID);
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
                PeakSdk.handleNativeAdClicked(NATIVE_AD_ID);
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


    @Override
    protected void onPause() {
        uiHelper.pause();
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
        //uiThreadHandler.removeCallbacks(finishActivityRunnable);
        interstitialAdAvailabilityExecutor.shutdownNow();
        super.onDestroy();
    }
}
