package sdk.peak.test.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peak.PeakSdk;
import com.peak.PeakSdkListener;
import com.peak.PeakSdkUiHelper;
import com.peak.exception.PeakSdkException;
import com.peak.nativeads.PeakNativeAd;

import org.w3c.dom.Text;

/**
 * Created by nenad on 8/9/2016.
 */
public class NewActivity extends AppCompatActivity {

    private static final String PEAK_APP_ID = "343b9d1657f5f935";
    private static final String PEAK_INTERSTITIAL_ZONE_ID = "59638";
    private static final String PEAK_BANNER_ZONE_ID = "59665";
    private static final String NATIVE_AD_ID = "59678";

    ImageView adIcon, mainImage;
    TextView adText, adTitle;
    Button button;
    private final Handler uiThreadHandler = new Handler();

    PeakSdkUiHelper uiHelper = new PeakSdkUiHelper(NewActivity.this);
    private LinearLayout bannerContainer;
    private String TAG = "peakkkkk";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_layout);

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
                if (PeakSdk.checkAdAvailable(PEAK_BANNER_ZONE_ID)) {
                    View banner = PeakSdk.showBanner(PEAK_BANNER_ZONE_ID);
                    if (banner != null) {
                        bannerContainer.addView(banner);
                    }
                }
            }

            @Override
            public void onInterstitialShowSuccess(String s) {
                Log.d(TAG, "onInterstitialShowSuccess: ");
            }

            @Override
            public void onInterstitialShowFailed(String s, PeakSdkException e) {
                Log.d(TAG, "onInterstitialShowFailed: ");
                if (PeakSdk.checkAdAvailable(PEAK_INTERSTITIAL_ZONE_ID)) {
                    PeakSdk.showInterstitial(PEAK_INTERSTITIAL_ZONE_ID);
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
        };

        PeakSdk.initialize(PEAK_APP_ID, uiHelper, peakSdkListener);

        if (PeakSdk.checkAdAvailable(PEAK_INTERSTITIAL_ZONE_ID)) {
            PeakSdk.showInterstitial(PEAK_INTERSTITIAL_ZONE_ID);
        } else
            PeakSdk.showInterstitial(PEAK_INTERSTITIAL_ZONE_ID);

        if (PeakSdk.checkAdAvailable(PEAK_BANNER_ZONE_ID)) {
            final View banner = PeakSdk.showBanner(PEAK_BANNER_ZONE_ID);
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    showBanner(banner);
                }
            });
        }

        showNativeAd();
    }

    @SuppressWarnings("ConstantConditions")
    private void showBanner(View banner) {
        Log.d(TAG, "showBanner: true");
        ((ViewGroup) findViewById(R.id.bannerContainer)).addView(banner);
    }

    private void showNativeAd() {
        if(PeakSdk.checkAdAvailable(NATIVE_AD_ID)) {
            PeakNativeAd peakNativeAd = PeakSdk.showNativeAd(NATIVE_AD_ID);
            if (peakNativeAd != null) {
                //notify SDK that ad was shown
                PeakSdk.trackNativeAdShown(NATIVE_AD_ID);
                bindNativeAdToViews(peakNativeAd);
            }
        }
    }

    private void bindNativeAdToViews(PeakNativeAd nativeAd) {
        //fill views with received native ad data

        // load the nativeAd.getMainImage() into your ImageView for the main image
        // load the nativeAd.getIcon() into your ImageView for icon image
        // set the nativeAd.getTitle() into you TextView for title
        // set the nativeAd.getText() into you TextView for description text
        // set the nativeAd.getActionText() into your Button
        mainImage = (ImageView) findViewById(R.id.native_ad_main_image);
        mainImage.setImageResource(Integer.parseInt(nativeAd.getMainImage()));
        adIcon = (ImageView) findViewById(R.id.native_ad_icon_image);
        adIcon.setImageResource(Integer.parseInt(nativeAd.getIcon()));
        adText = (TextView) findViewById(R.id.native_ad_text);
        adText.setText(nativeAd.getText());
        adTitle = (TextView) findViewById(R.id.native_ad_title);
        adTitle.setText(nativeAd.getTitle());
        button = (Button) findViewById(R.id.button);
        // set onClickListener on your button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //notify SDK that ad was clicked by user
                PeakSdk.handleNativeAdClicked(NATIVE_AD_ID);
            }
        });
    }
}