package com.info.object;

import android.net.Uri;

public class SRPostObject extends Object {

    public enum SRPostStatus {
        SRNonStatus,
        SRGoodStatus,
        SRBadStatus
    };

    public Uri uriPostedImage;
    public String strPostedImagePath;
    public String postedTitle;
    public String postedContent;
    public SRPostStatus postStatus;
}
