package sdk.peak.test.myapplication;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.peak.PeakSdk;
import com.peak.PeakSdkListener;
import com.peak.PeakSdkListenerAdapter;
import com.peak.PeakSdkUiHelper;
import com.peak.exception.PeakSdkException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    PeakSdkUiHelper uiHelper = new PeakSdkUiHelper(MainActivity.this);
    private String TAG = "peak";
    private static final int AD_CHECK_DELAY_SECONDS = 1;
    private static final String PEAK_APP_ID = "343b9d1657f5f935";
    private static final String PEAK_INTERSTITIAL_ZONE_ID = "59638";
    private static final String PEAK_BANNER_ZONE_ID = "59665";

    private final Handler uiThreadHandler = new Handler();
    private final ScheduledExecutorService interstitialAdAvailabilityExecutor =
            Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService bannerAdAvailabilityExecutor =
            Executors.newSingleThreadScheduledExecutor();

    private boolean interstitialShown = false;
    private boolean bannerShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        PeakSdk.initialize(PEAK_APP_ID , uiHelper, new PeakSdkListenerAdapter() {
            @Override
            public void onInitializationSuccess() {
                Log.d(TAG, "onInitializationSuccess");
            }
        });

        interstitialAdAvailabilityExecutor.scheduleWithFixedDelay(
                getShowInterstitialRunnable(), 0, AD_CHECK_DELAY_SECONDS, TimeUnit.SECONDS);
        bannerAdAvailabilityExecutor.scheduleWithFixedDelay(
                getShowBannerRunnable(), 0, AD_CHECK_DELAY_SECONDS, TimeUnit.SECONDS);

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
        interstitialAdAvailabilityExecutor.shutdownNow();
        super.onDestroy();
    }
}
