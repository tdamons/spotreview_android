package com.info.object;

import org.json.JSONObject;

import java.io.Serializable;

public class SpotObject extends BaseObject implements Serializable {

    private static final long serialVersionUID = 1L;

    public String spotCompanyName;
    public String spotImageUrl;
    public String spotDescription;
    public String spotCreatedDate;
    public String goodNumber;
    public String badNumber;

    public SpotObject(JSONObject jsonObject)  {

        this.spotCompanyName = getStringFromJSONObject(jsonObject, "post_title");
        this.spotImageUrl = getStringFromJSONObject(jsonObject, "post_imageurl");
        this.spotDescription = getStringFromJSONObject(jsonObject, "post_content");
        this.spotCreatedDate = getStringFromJSONObject(jsonObject, "created_date");
        this.goodNumber = getStringFromJSONObject(jsonObject, "good");
        this.badNumber = getStringFromJSONObject(jsonObject, "bad");
    }
}
