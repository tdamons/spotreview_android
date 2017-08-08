package com.spotreview.spotreview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.info.object.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;


public class ShareActivity extends Activity implements View.OnClickListener{

    private ImageView ivBack, ivFacebook, ivTwitter;
    private Button btnSubmit;

    private Boolean isFacebookClicked, isTwitterClicked;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        init();
    }

    private void init() {
        ivBack = (ImageView) findViewById(R.id.iv_share_back);
        ivFacebook = (ImageView) findViewById(R.id.iv_share_facebook);
        ivTwitter = (ImageView) findViewById(R.id.iv_share_twitter);
        btnSubmit = (Button) findViewById(R.id.btn_share_submit);

        ivBack.setOnClickListener(this);
        ivFacebook.setOnClickListener(this);
        ivTwitter.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        isFacebookClicked = isTwitterClicked = false;
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack){
            finish();
        }

        if (v == ivFacebook) {
            if (isFacebookClicked) {
                ivFacebook.setImageResource(R.drawable.ic_facebook_unclicked);
            }
            else
                ivFacebook.setImageResource(R.drawable.ic_facebook_clicked);

            isFacebookClicked=!isFacebookClicked;
        }

        if (v == ivTwitter) {
            if (isTwitterClicked)
                ivTwitter.setImageResource(R.drawable.ic_twitter_unclicked);
            else
                ivTwitter.setImageResource(R.drawable.ic_twitter_clicked);

            isTwitterClicked=!isTwitterClicked;
        }

        if (v == btnSubmit) {
            if (isFacebookClicked) {
                sharePhotoToFacebook();
            }
            if (isTwitterClicked)
                sharePhotoToTwitter();
        }
    }

    private void sharePhotoToFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken == null)
            facebookLogin();
        else
            postToFacebook(accessToken);
    }

    private void facebookLogin(){
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();

                Toast.makeText(getApplicationContext(), "Facebook Login Success!", Toast.LENGTH_SHORT).show();

                postToFacebook(accessToken);
            }

            @Override
            public void onCancel() {
                Toast.makeText(ShareActivity.this, "Facebook Login Cancel!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(ShareActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
    }

    private void postToFacebook(AccessToken accessToken) {

        Bundle params = new Bundle();
        params.putString("caption",SessionManager.postObject.postedTitle+"\n"+SessionManager.postObject.postedContent);
        params.putString("url", String.valueOf(SessionManager.postObject.uriPostedImage));

//        make the API call
        new GraphRequest(
                accessToken,
                "me/photos",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Toast.makeText(ShareActivity.this, response.SUCCESS_KEY, Toast.LENGTH_SHORT).show();
                    }
                }
        ).executeAsync();
    }

    private void sharePhotoToTwitter() {
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(getResources().getString(R.string.twitter_consumer_key), getResources().getString(R.string.twitter_consumer_secret));
        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());

        File myImageFile = new File(SessionManager.postObject.strPostedImagePath);
        Uri myImageUri = Uri.fromFile(myImageFile);

        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text(SessionManager.postObject.postedTitle + "\n" + SessionManager.postObject.postedContent)
                .image(myImageUri);
        builder.show();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCallbackManager.onActivityResult(requestCode, resultCode, data))
            return;
    }
}
