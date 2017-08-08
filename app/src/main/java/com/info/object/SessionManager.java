package com.info.object;

import android.net.Uri;

public class SessionManager {
    public static SRPostObject postObject = null;
    public static Boolean isHot = true;

    public static final String COGNITO_POOL_ID = "us-east-1:445ba5d2-1ea9-44f3-bab5-72d7b2b48edd";
//    public static final String BUCKET_NAME = "https://s3.amazonaws.com/spot-logo-usstandard/";
    public static final String BUCKET_NAME = "spot-logo-usstandard";

    public static void initSRPostObject() {

        postObject.uriPostedImage = Uri.parse("");
        postObject.strPostedImagePath = "";
        postObject.postedContent = "";
        postObject.postedTitle = "";
        postObject.postStatus = SRPostObject.SRPostStatus.SRNonStatus;
    }
}
