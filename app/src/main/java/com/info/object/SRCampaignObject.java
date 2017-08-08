package com.info.object;

import org.json.JSONException;
import org.json.JSONObject;

public class SRCampaignObject extends BaseObject {

//
//    - (id)initWithDictionary:(NSDictionary*)jsonObj;
//
//    @end
    public int campaignId;
    public String campaignCode;
    public String campaignBranding;
    public String campaignName;
    public String created_date;

    public SRCampaignObject(JSONObject jsonObject)  {
        this.campaignId = getIntFromJSONObject(jsonObject, "campaign_id");
        this.campaignCode = getStringFromJSONObject(jsonObject, "campaign_code");
        this.campaignBranding = getStringFromJSONObject(jsonObject, "campaign_branding");
        this.campaignName = getStringFromJSONObject(jsonObject, "campaign_name");
        this.created_date = getStringFromJSONObject(jsonObject, "created_date");
    }

    public SRCampaignObject(String strCampaign) {
        try {
            JSONObject jsonObject = new JSONObject(strCampaign);
            new SRCampaignObject(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
