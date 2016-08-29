package sdk.peak.test.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.peak.PeakSdk;
import com.peak.PeakSdkListener;
import com.peak.PeakSdkUiHelper;
import com.peak.exception.PeakSdkException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Darko Sarev on 8/29/2016.
 */

public class DoodleTest extends AppCompatActivity {

    private static final String PEAK_APP_ID = "b275a085a5c266ef";
    private static final String PEAK_INTERSTITIAL_ZONE_ID = "26773";
    private static final String PEAK_REWARDED_ZONE_ID = "26763";

    private boolean interstitialShown = false;


    PeakSdkUiHelper uiHelper = new PeakSdkUiHelper(DoodleTest.this);

    private String TAG = "peakkkkk";

    private final ScheduledExecutorService rewardedAvailabilityExecutor =
            Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService interstitialAdAvailabilityExecutor =
            Executors.newSingleThreadScheduledExecutor();

    private static final int AD_CHECK_DELAY_SECONDS = 1;

    Button staticB, rewardedB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        staticB = (Button) findViewById(R.id.button2);
        rewardedB = (Button) findViewById(R.id.button3);
        PeakSdkListener peakSdkListener = new PeakSdkListener() {
            @Override
            public void onInitializationSuccess() {
                Log.d(TAG, "onInitializationSuccess: ");
                PeakSdk.showInterstitial(PEAK_INTERSTITIAL_ZONE_ID);
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
            }

            @Override
            public void onInterstitialShowSuccess(String s) {
                Log.d(TAG, "onInterstitialShowSuccess: ");
            }

            @Override
            public void onInterstitialShowFailed(String s, PeakSdkException e) {
                Log.d(TAG, "onInterstitialShowFailed: ");
                if (PeakSdk.checkAdAvailable(PEAK_INTERSTITIAL_ZONE_ID)) {
                }
            }

            @Override
            public void onInterstitialClosed(String s) {
                Log.d(TAG, "onInterstitialClosed: ");
                if (PeakSdk.checkAdAvailable(PEAK_INTERSTITIAL_ZONE_ID)) {
                }
            }

            @Override
            public void onCompletedRewardExperience(String s) {
                Log.d(TAG, "onCompletedRewardExperience: ");
                if (PeakSdk.checkAdAvailable(PEAK_INTERSTITIAL_ZONE_ID)) {
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

        interstitialAdAvailabilityExecutor.scheduleWithFixedDelay(
                getShowInterstitialRunnable(), 0, AD_CHECK_DELAY_SECONDS, TimeUnit.SECONDS);
        rewardedAvailabilityExecutor.scheduleWithFixedDelay(
                getRewardedRunnable(), 0, AD_CHECK_DELAY_SECONDS, TimeUnit.SECONDS);
        staticB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PeakSdk.checkAdAvailable(PEAK_INTERSTITIAL_ZONE_ID)) {
                    interstitialShown = true;
                    PeakSdk.showInterstitial(PEAK_INTERSTITIAL_ZONE_ID);
                }
            }
        });

        rewardedB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PeakSdk.checkAdAvailable(PEAK_REWARDED_ZONE_ID)) {
                    interstitialShown = true;
                    PeakSdk.showInterstitial(PEAK_REWARDED_ZONE_ID);
                }
            }
        });
    }


    private Runnable getShowInterstitialRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || interstitialShown) {
                    interstitialAdAvailabilityExecutor.shutdownNow();
                }

            }
        };
    }

    private Runnable getRewardedRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || interstitialShown) {
                    rewardedAvailabilityExecutor.shutdownNow();
                }

            }
        };
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
        rewardedAvailabilityExecutor.shutdownNow();
        super.onDestroy();
    }
}
