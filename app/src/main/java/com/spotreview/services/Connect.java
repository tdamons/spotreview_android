package com.spotreview.services;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.info.object.SRCampaignObject;
import com.info.object.SRPostObject;
import com.info.object.SRUser;
import com.info.object.SessionManager;
import com.info.object.SpotDetailObject;
import com.info.object.SpotObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Connect {
    private static Connect mSharedInstance = null;
    DefaultHttpClient mHttpClient;

    SRUser mCurrentUser;
    Context mContext;

    public Connect(Context context) {

        mHttpClient = new DefaultHttpClient();

        mContext = context;
        SRUser oldUser = SRUser.loadFromDisk(context);
        if (oldUser != null) {
            mCurrentUser = oldUser;
        }
    }

    public static Connect getInstance(Context context) {

        if (mSharedInstance == null) {
            mSharedInstance = new Connect(context.getApplicationContext());
        }
        return mSharedInstance;
    }

    public SRUser loginUser(String email, String password) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("email", email));
        parameters.add(new BasicNameValuePair("password", password));
        JSONObject jsonObject = this.getJSONObject(HttpUrlManager.URL_SERVER + HttpUrlManager.URL_LOGIN, parameters);
        try {
            if (jsonObject.getBoolean("result")) {
                SRUser user = new SRUser(jsonObject.getJSONObject("object"));
                user.userType = SRUser.SRUserType.UserTypeEmail;
                user.userPassword = password;
                user.saveOnDisk(mContext);
                mCurrentUser = user;
                return user;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SRUser facebookLoginUser(List<NameValuePair> parameters) {
        JSONObject jsonObject = this.getJSONObject(HttpUrlManager.URL_SERVER + HttpUrlManager.URL_FACEBOOK_LOGIN, parameters);
        try {
            if (jsonObject.getBoolean("result")) {
                SRUser user = new SRUser(jsonObject.getJSONObject("object"));
                user.userType = SRUser.SRUserType.UserTypeFacebook;
                user.saveOnDisk(mContext);
                mCurrentUser = user;
                return user;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SRUser twitterLoginUser(List<NameValuePair> parameters) {
        JSONObject jsonObject = this.getJSONObject(HttpUrlManager.URL_SERVER + HttpUrlManager.URL_TWITTER_LOGIN, parameters);
        try {
            if (jsonObject.getBoolean("result")) {
                SRUser user = new SRUser(jsonObject.getJSONObject("object"));
                user.userType = SRUser.SRUserType.UserTypeTwitter;
                user.saveOnDisk(mContext);
                mCurrentUser = user;
                return user;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SRUser userRegister(List<NameValuePair> parameters) {
        JSONObject jsonObject = this.getJSONObject(HttpUrlManager.URL_SERVER + HttpUrlManager.URL_REGISTER, parameters);
        try {
            if (jsonObject.getBoolean("result")) {
                SRUser user = new SRUser(jsonObject.getJSONObject("object"));
                user.saveOnDisk(mContext);
                mCurrentUser = user;
                return user;
            }
            else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean spotPost(String imageName, int userID){
        String photoTitle = SessionManager.postObject.postedTitle;
        String photoContent = SessionManager.postObject.postedContent;
        String photoName = "https://s3.amazonaws.com/"+SessionManager.BUCKET_NAME+"/"+imageName;
        SRPostObject.SRPostStatus status = SessionManager.postObject.postStatus;

        SessionManager.postObject.uriPostedImage = Uri.parse(photoName);

        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("userId", String.valueOf(userID)));
        parameters.add(new BasicNameValuePair("photoTitle", photoTitle));
        parameters.add(new BasicNameValuePair("photoContent", photoContent));
        parameters.add(new BasicNameValuePair("photoUrl", photoName));
        parameters.add(new BasicNameValuePair("status", String.valueOf(SRPostObject.SRPostStatus.valueOf(status.toString()).ordinal())));
        JSONObject response = this.getJSONObject(HttpUrlManager.URL_SERVER + HttpUrlManager.URL_POST, parameters);
        try {
            if (response.getBoolean("result") == true) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public SRCampaignObject getCampaign(String campaignCode) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("campaign_code", campaignCode));
        SRCampaignObject srCampaignObject = null;
        JSONObject jsonObject = this.getJSONObject(HttpUrlManager.URL_SERVER + HttpUrlManager.URL_CHECK_CAMPAIGNCODE, parameters);
        try {
            if (jsonObject.getBoolean("result")) {
                srCampaignObject = new SRCampaignObject(jsonObject.getJSONObject("object"));
                return srCampaignObject;
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public ArrayList<SpotObject> getTopSpots(int page) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
        ArrayList<SpotObject> spotObjects = new ArrayList<SpotObject>();
        JSONObject jsonObject = this.getJSONObject(HttpUrlManager.URL_SERVER + HttpUrlManager.URL_GET_TOPSPOTS, parameters);
        try {
            if (jsonObject.getBoolean("result")) {
                JSONArray jsonArray;

                jsonArray = jsonObject.getJSONArray("object");
                for (int i = 0; i < jsonArray.length(); i++) {
                    SpotObject spotObject = new SpotObject(jsonArray.getJSONObject(i));
                    spotObjects.add(spotObject);
                }

                return spotObjects;
            } else {
                return null;
            }
        } catch (JSONException e) {
                e.printStackTrace();
            return null;
        }
    }

    public ArrayList<SpotObject> getBadSpots(int page) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
        ArrayList<SpotObject> spotObjects = new ArrayList<SpotObject>();
        JSONObject jsonObject = this.getJSONObject(HttpUrlManager.URL_SERVER + HttpUrlManager.URL_GET_BADSPOTS, parameters);
        try {
            if (jsonObject.getBoolean("result")) {
                JSONArray jsonArray;

                jsonArray = jsonObject.getJSONArray("object");
                for (int i = 0; i < jsonArray.length(); i++) {
                    SpotObject spotObject = new SpotObject(jsonArray.getJSONObject(i));
                    spotObjects.add(spotObject);
                }

                return spotObjects;
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<SpotDetailObject> getCompanyDetails(String companyName) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("company_name", companyName));
        ArrayList<SpotDetailObject> spotDetailObjects = new ArrayList<SpotDetailObject>();
        JSONObject jsonObject = this.getJSONObject(HttpUrlManager.URL_SERVER + HttpUrlManager.URL_GET_COMPANYDETAILS, parameters);
        try {
            if (jsonObject.getBoolean("result")) {
                JSONArray jsonArray;

                jsonArray = jsonObject.getJSONArray("object");
                for (int i = 0; i < jsonArray.length(); i++) {
                    SpotDetailObject detailObject = new SpotDetailObject(jsonArray.getJSONObject(i));
                    spotDetailObjects.add(detailObject);
                }

                return spotDetailObjects;
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<SpotObject> getMyReviews(int page) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("user_id", String.valueOf(mCurrentUser.userId)));
        parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
        ArrayList<SpotObject> spotObjects = new ArrayList<SpotObject>();
        JSONObject jsonObject = this.getJSONObject(HttpUrlManager.URL_SERVER + HttpUrlManager.URL_GET_SPOTSBYUSERID, parameters);
        try {
            if (jsonObject.getBoolean("result") == true) {
                JSONArray jsonArray;

                jsonArray = jsonObject.getJSONArray("object");
                for (int i = 0; i < jsonArray.length(); i++) {
                    SpotObject spotObject = new SpotObject(jsonArray.getJSONObject(i));
                    spotObjects.add(spotObject);
                }

                return spotObjects;
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<SpotObject> searchSpots(int page) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("page", String.valueOf(page)));
        ArrayList<SpotObject> spotObjects = new ArrayList<SpotObject>();
        JSONObject jsonObject = this.getJSONObject(HttpUrlManager.URL_SERVER + HttpUrlManager.URL_GET_BRANDS, parameters);
        try {
            if (jsonObject.getBoolean("result") == true) {
                JSONArray jsonArray;

                jsonArray = jsonObject.getJSONArray("object");
                for (int i = 0; i < jsonArray.length(); i++) {
                    SpotObject spotObject = new SpotObject(jsonArray.getJSONObject(i));
                    spotObjects.add(spotObject);
                }

                return spotObjects;
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<SpotObject> searchSpotsByString(String searchString) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("search_string", searchString));
        ArrayList<SpotObject> spotObjects = new ArrayList<SpotObject>();
        JSONObject jsonObject = this.getJSONObject(HttpUrlManager.URL_SERVER + HttpUrlManager.URL_GET_SEARCHBRANDS, parameters);
        try {
            if (jsonObject.getBoolean("result") == true) {
                JSONArray jsonArray;

                jsonArray = jsonObject.getJSONArray("object");
                for (int i = 0; i < jsonArray.length(); i++) {
                    SpotObject spotObject = new SpotObject(jsonArray.getJSONObject(i));
                    spotObjects.add(spotObject);
                }

                return spotObjects;
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SRUser updateUserProfile(String userName, String email, String telNo, String secQuestion, String secAnswer, String password) {

        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("user_id", String.valueOf(mCurrentUser.userId)));

        if (!TextUtils.isEmpty(userName)) {
            parameters.add(new BasicNameValuePair("user_name", userName));
        }
        if (!TextUtils.isEmpty(email)) {
            parameters.add(new BasicNameValuePair("user_email", email));
        }
        if (!TextUtils.isEmpty(telNo)) {
            parameters.add(new BasicNameValuePair("user_mobilenumber", telNo));
        }
        if (!TextUtils.isEmpty(secQuestion)) {
            parameters.add(new BasicNameValuePair("user_secquestion", secQuestion));
        }
        if (!TextUtils.isEmpty(secAnswer)) {
            parameters.add(new BasicNameValuePair("user_answer", secAnswer));
        }
        if (!TextUtils.isEmpty(password)) {
            parameters.add(new BasicNameValuePair("user_password", password));
        }

        JSONObject jsonObject = this.getJSONObject(HttpUrlManager.URL_SERVER + HttpUrlManager.URL_UPDATE_USER, parameters);
        try {
            if (jsonObject.getBoolean("result") == true) {
                SRUser user = new SRUser(jsonObject.getJSONObject("object"));
                user.saveOnDisk(mContext);
                mCurrentUser = user;
                return user;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getJSONObject(String url, List<NameValuePair> parameters) {
        HttpPost httpPost = new HttpPost(url);
        HttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(parameters));
            response = mHttpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
            return jsonObject;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SRUser getCurrentUser() {
        return mCurrentUser;
    }
}
