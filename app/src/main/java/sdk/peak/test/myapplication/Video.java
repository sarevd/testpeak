package sdk.peak.test.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.peak.PeakAsyncAdRequest;
import com.peak.PeakSdk;
import com.peak.PeakSdkListener;
import com.peak.PeakSdkUiHelper;
import com.peak.exception.PeakSdkException;

/**
 * Created by Darko Sarev on 9/13/2016.
 */

public class Video extends AppCompatActivity {

    private static final String PEAK_APP_ID = "2662c914e72da398";
    private static final String AD_ZONE_ID = "132210";
    private static final String TAG = "PEAKKK";
    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime;
    private final int REFRESH_RATE = 10;
    private String hours,minutes,seconds,milliseconds;
    private long secs,mins,hrs,msecs;
    private boolean stopped = false;

    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            mHandler.postDelayed(this,REFRESH_RATE);
        }
    };

    private void updateTimer (float time){
        secs = (long)(time/1000);
        mins = (long)((time/1000)/60);
        hrs = (long)(((time/1000)/60)/60);

		/* Convert the seconds to String
		 * and format to ensure it has
		 * a leading zero when required
		 */
        secs = secs % 60;
        seconds=String.valueOf(secs);
        if(secs == 0){
            seconds = "00";
        }
        if(secs <10 && secs > 0){
            seconds = "0"+seconds;
        }

		/* Convert the minutes to String and format the String */

        mins = mins % 60;
        minutes=String.valueOf(mins);
        if(mins == 0){
            minutes = "00";
        }
        if(mins <10 && mins > 0){
            minutes = "0"+minutes;
        }

    	/* Convert the hours to String and format the String */

        hours=String.valueOf(hrs);
        if(hrs == 0){
            hours = "00";
        }
        if(hrs <10 && hrs > 0){
            hours = "0"+hours;
        }

    	/* Although we are not using milliseconds on the timer in this example
    	 * I included the code in the event that you wanted to include it on your own
    	 */
        milliseconds = String.valueOf((long)time);
        if(milliseconds.length()==2){
            milliseconds = "0"+milliseconds;
        }
        if(milliseconds.length()<=1){
            milliseconds = "00";
        }
        milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-2);

    }

    PeakSdkUiHelper uiHelper = new PeakSdkUiHelper(Video.this);

    PeakAsyncAdRequest asyncAdRequest;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);
        startTime = System.currentTimeMillis();
        mHandler.removeCallbacks(startTimer);
        mHandler.postDelayed(startTimer, 0);
        PeakSdkListener peakSdkListener = new PeakSdkListener() {
            @Override
            public void onInitializationSuccess() {
                Log.d(TAG, "onInitializationSuccess: ");
                startAdRequest();
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
                Toast.makeText(Video.this, "Video showed in " + seconds +"," + milliseconds + "s", Toast.LENGTH_LONG).show();
                mHandler.removeCallbacks(startTimer);
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
        asyncAdRequest = PeakSdk.createAdRequest(AD_ZONE_ID);
        if (asyncAdRequest != null) {
            asyncAdRequest.start(asyncAdRequestListener);
        }
    }

    @Override
    protected void onPause() {
        uiHelper.pause();
        if (asyncAdRequest != null) {
            asyncAdRequest.cancel();
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
}
