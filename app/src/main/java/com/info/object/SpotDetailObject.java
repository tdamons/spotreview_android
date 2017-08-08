package com.info.object;

import org.json.JSONObject;
import java.io.Serializable;
import com.info.object.SRPostObject.SRPostStatus;

public class SpotDetailObject extends BaseObject implements Serializable {

    private static final long serialVersionUID = 1L;

    public String userAvatarpath;
    public String userName;
    public SRPostStatus postStatus;
    public String brandImagePath;
    public String spotDescription;
    public String spotCreatedDate;

    public SpotDetailObject(JSONObject jsonObject)  {

        this.userName = getStringFromJSONObject(jsonObject, "user_name");
        this.userAvatarpath = getStringFromJSONObject(jsonObject, "user_avatarpath");
        this.spotDescription = getStringFromJSONObject(jsonObject, "post_content");
        this.spotCreatedDate = getStringFromJSONObject(jsonObject, "created_date");
        this.brandImagePath = getStringFromJSONObject(jsonObject, "post_imageurl");
        this.postStatus = (SRPostStatus.values()[getIntFromJSONObject(jsonObject, "post_status")]);
    }
}
