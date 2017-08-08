package com.info.object;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.json.JSONObject;
import android.content.Context;

import java.io.Serializable;

public class SRUser extends BaseObject implements Serializable {

    public enum SRUserType {
        UserTypeEmail,
        UserTypeFacebook,
        UserTypeTwitter
    };

    private static final long serialVersionUID = 1L;

    public int userId;
    public String userName;
    public String userAvatarPath;
    public String userEmail ;
    public String userTelNumber;
    public String userSecQuestion;
    public String userAnswer;
    public String facebookId;
    public String twitterId;
    public SRUserType userType;
    public String userPassword = "";

    public SRUser(JSONObject jsonObject) {

        this.userId = getIntFromJSONObject(jsonObject, "user_id");
        this.userName = getStringFromJSONObject(jsonObject, "user_name");
        this.userAvatarPath = getStringFromJSONObject(jsonObject, "user_avatarpath");
        this.userEmail = getStringFromJSONObject(jsonObject, "user_email");
        this.userTelNumber = getStringFromJSONObject(jsonObject, "user_mobilenumber");
        this.userSecQuestion = getStringFromJSONObject(jsonObject, "user_secquestion");
        this.userAnswer = getStringFromJSONObject(jsonObject, "user_answer");
        this.facebookId = getStringFromJSONObject(jsonObject, "facebook_id");
        this.twitterId = getStringFromJSONObject(jsonObject, "twitter_id");
        this.userType = (SRUserType.values()[getIntFromJSONObject(jsonObject, "user_type")]);
    }

    public void saveOnDisk(Context context) {

        FileOutputStream fos;
        try {
            fos = context.openFileOutput("currentUser", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SRUser loadFromDisk(Context context) {

        FileInputStream fis;
        try {
            fis = context.openFileInput("currentUser");
            ObjectInputStream is = new ObjectInputStream(fis);
            SRUser user = (SRUser)is.readObject();
            is.close();
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteFromDisk(Context context) {

        context.deleteFile("currentUser");
    }
}
