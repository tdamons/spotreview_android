package com.spotreview.spotreview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class ImagePreviewActivity extends AppCompatActivity {
    private static final String EXTRA_DETAILS = "extra.details";
    private String url;
    private ImageViewTouch image;

    public static void show(Activity activity, View view, String url) {
        if (activity==null||view==null||url==null)
            return;
        Intent intent = new Intent(activity,ImagePreviewActivity.class);
        intent.putExtra(EXTRA_DETAILS,url);
        Bundle bundle;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //animate shared transition for lolipop+
            bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, ViewCompat.getTransitionName(view)).toBundle();
            ActivityCompat.startActivity(activity,intent,bundle);
        }
        else{//or show slid up/down animation for older devices
            activity.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        parseIntent(getIntent());
        if (url==null){
            finish();
            return;
        }
        image = (ImageViewTouch) findViewById(R.id.image);
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        //ViewCompat.setTransitionName(image,spot.brandImagePath);
        supportPostponeEnterTransition();
        showImage();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementReturnTransition(null);
            getWindow().setSharedElementReenterTransition(null);
        }
    }

    private void showImage() {
        DisplayImageOptions brandOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(null)
                .showImageOnFail(null)
                .showImageOnLoading(null)
                .build();
        ImageLoader.getInstance().displayImage(url, image, brandOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                supportStartPostponedEnterTransition();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                supportStartPostponedEnterTransition();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                supportStartPostponedEnterTransition();
            }
        });
    }

    private void parseIntent(Intent intent) {
        if (intent==null)
            return;
        url = intent.getStringExtra(EXTRA_DETAILS);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
}
