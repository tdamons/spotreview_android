package com.spotreview.spotreview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.info.object.SRPostObject;
import com.info.object.SRUser;
import com.info.object.SessionManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.spotreview.services.Connect;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AuthToken;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.fabric.sdk.android.Fabric;


public class LoginActivity extends Activity implements OnClickListener {

    private static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

    private UserLoginTask userLoginTask = null;
    private UserFacebookLoginTask userFacebookLoginTask = null;
    private UserTwitterLoginTask userTwitterLoginTask = null;


    String emailString;
    String passwordString;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private CallbackManager mCallbackManager;
    private List<NameValuePair> facebookUserData = new ArrayList<NameValuePair>();
    private List<NameValuePair> twitterUserData = new ArrayList<NameValuePair>();

//    private Twitter twitter;
//    private RequestToken requestToken;
    private TwitterAuthClient mTwitterAuthClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_page);

        emailString = getIntent().getStringExtra(EXTRA_EMAIL);
        editTextEmail = (EditText) findViewById(R.id.login_emailTextField);
        editTextEmail.setText(emailString);
        editTextPassword = (EditText) findViewById(R.id.login_pwdTextField);


        ((Button) findViewById(R.id.login_loginBtn)).setOnClickListener(this);
        ((Button) findViewById(R.id.login_forgotBtn)).setOnClickListener(this);
        ((Button) findViewById(R.id.login_registerBtn)).setOnClickListener(this);
        ((Button) findViewById(R.id.login_facebookLoginBtn)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_login_twitter)).setOnClickListener(this);


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        SRUser oldUser = SRUser.loadFromDisk(this);
        if (oldUser != null) {
            if (oldUser.userType == SRUser.SRUserType.UserTypeEmail) {
                editTextEmail.setText(oldUser.userEmail);
                editTextPassword.setText(oldUser.userPassword);

                loginToEmail();
            }

            if (oldUser.userType == SRUser.SRUserType.UserTypeFacebook) {
                loginToFacebook();
            }

            if (oldUser.userType == SRUser.SRUserType.UserTypeTwitter) {
                loginToTwitter();
            }
        }

        SessionManager.postObject = new SRPostObject();
        SessionManager.initSRPostObject();
    }

    private void loginToFacebook () {
        SharedPreferences sharedPref = getSharedPreferences("facebook", MODE_PRIVATE);
        String userName = sharedPref.getString("userName", null);
        String id = sharedPref.getString("id", null);
        String avatarpath = sharedPref.getString("avatarpath", null);

        if (userName == null || id == null || avatarpath == null)
            facebookLogin();
        else
            facebookLoginRequest(userName, id, avatarpath);
    }

    private void facebookLogin(){
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();
                GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        String userName = user.optString("name");
                        String id = user.optString("id");
                        String avatarpath = "https://graph.facebook.com/"+id+"/picture?type=normal";

                        SharedPreferences sharedPreferences = getSharedPreferences("facebook", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userName", userName);
                        editor.putString("id", id);
                        editor.putString("avatarpath", avatarpath);
                        editor.commit();

                        facebookLoginRequest(userName, id, avatarpath);
                    }
                }).executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Facebook Login Cancel!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
    }

    private void facebookLoginRequest(String userName, String id, String avatarpath){
        facebookUserData.add(new BasicNameValuePair("username", userName));
        facebookUserData.add(new BasicNameValuePair("avatarpath", avatarpath));
        facebookUserData.add(new BasicNameValuePair("facebookid", id));
        facebookUserData.add(new BasicNameValuePair("userType", String.valueOf(SRUser.SRUserType.UserTypeFacebook)));

        userFacebookLoginTask = new UserFacebookLoginTask();
        userFacebookLoginTask.execute();
    }

    private void loginToTwitter() {
        SharedPreferences sharedPref = getSharedPreferences("twitter", MODE_PRIVATE);
        String userName = sharedPref.getString("userName", null);
        String id = sharedPref.getString("id", null);
        String token = sharedPref.getString("token", null);
        String secret = sharedPref.getString("secret", null);

        if (userName == null || id == null || token == null || secret == null)
            twitterLogin();
        else
            twitterLoginRequest(userName, id);
    }

    private void twitterLogin(){
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(getResources().getString(R.string.twitter_consumer_key), getResources().getString(R.string.twitter_consumer_secret));
        Fabric.with(this, new Twitter(authConfig));

        mTwitterAuthClient= new TwitterAuthClient();

        mTwitterAuthClient.authorize(this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                String userName = twitterSessionResult.data.getUserName();
                String id = String.valueOf(twitterSessionResult.data.getUserId());
                String token = twitterSessionResult.data.getAuthToken().token;
                String secret = twitterSessionResult.data.getAuthToken().secret;

                SharedPreferences sharedPreferences = getSharedPreferences("twitter", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userName", userName);
                editor.putString("id", id);
                editor.putString("token", token);
                editor.putString("secret", secret);
                editor.commit();

                twitterLoginRequest(userName, id);
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(LoginActivity.this, "Twitter Login Failure!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void twitterLoginRequest(String userName, String id) {
        twitterUserData.add(new BasicNameValuePair("username", userName));
        twitterUserData.add(new BasicNameValuePair("twitterid", id));
        twitterUserData.add(new BasicNameValuePair("userType", String.valueOf(SRUser.SRUserType.UserTypeTwitter)));

        userTwitterLoginTask = new UserTwitterLoginTask();
        userTwitterLoginTask.execute();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.login_forgotBtn:
                break;
            case R.id.login_registerBtn:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;

//            Login
            case R.id.login_loginBtn:
                loginToEmail();
                break;
            case R.id.login_facebookLoginBtn:
                loginToFacebook();
                break;
            case R.id.btn_login_twitter:
                loginToTwitter();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCallbackManager!=null && mCallbackManager.onActivityResult(requestCode, resultCode, data))
            return;
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    public void loginToEmail () {

        if (userLoginTask == null) {
            editTextEmail.setError(null);
            editTextPassword.setError(null);
            emailString = editTextEmail.getText().toString();
            passwordString = editTextPassword.getText().toString();

            boolean cancel = false;
            View focusView = null;

            if (TextUtils.isEmpty(emailString)) {
                editTextPassword.setError(getString(R.string.error_field_required));
                focusView = editTextEmail;
                cancel = true;

            } else if (TextUtils.isEmpty(passwordString)) {
                editTextPassword.setError(getString(R.string.error_field_required));
                focusView = editTextPassword;
                cancel = true;
            }

            if (cancel) {
                focusView.requestFocus();
            } else {
                userLoginTask = new UserLoginTask();
                userLoginTask.execute();
            }
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog mLoadingDialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {

            mLoadingDialog.setMessage("Signing in...");
            mLoadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            SRUser user = Connect.getInstance(getBaseContext()).loginUser(emailString, passwordString);
            if (user != null && user.userEmail != null) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            userLoginTask = null;
            mLoadingDialog.dismiss();

            if (success) {
                Intent main = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(main);
            } else {
                editTextPassword.setError(getString(R.string.error_incorrect_password));
                editTextPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {

            userLoginTask = null;
            mLoadingDialog.dismiss();
        }
    }

    public class UserFacebookLoginTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog mLoadingDialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {

            mLoadingDialog.setMessage("Signing in...");
            mLoadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            SRUser user = Connect.getInstance(getBaseContext()).facebookLoginUser(facebookUserData);
            if (user != null) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            userFacebookLoginTask = null;
            mLoadingDialog.dismiss();

            if (success) {
                Toast.makeText(getApplicationContext(), "Facebook Login Success!", Toast.LENGTH_SHORT).show();

                Intent main = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(main);
            } else {
                Toast.makeText(LoginActivity.this, "Facebook Login Faild!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {

            userFacebookLoginTask = null;
            mLoadingDialog.dismiss();
        }
    }

    public class UserTwitterLoginTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog mLoadingDialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {

            mLoadingDialog.setMessage("Signing in...");
            mLoadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            SRUser user = Connect.getInstance(getBaseContext()).twitterLoginUser(twitterUserData);
            if (user != null) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            userTwitterLoginTask = null;
            mLoadingDialog.dismiss();

            if (success) {
                Toast.makeText(LoginActivity.this, "Twitter Login Success!", Toast.LENGTH_SHORT).show();

                Intent main = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(main);
            } else {
                Toast.makeText(LoginActivity.this, "Twitter Login Faild!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {

            userTwitterLoginTask = null;
            mLoadingDialog.dismiss();
        }
    }
}
