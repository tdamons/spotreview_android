package com.spotreview.spotreview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.info.object.SRUser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spotreview.services.Connect;

public class UpdateProfileActivity extends Activity implements OnClickListener, View.OnTouchListener {

    private EditText userNameEditTxt;
    private EditText userEmailEditTxt;
    private EditText userTelEditText;
    private EditText userSecQueEditTxt;
    private EditText userSecAnswerEditTxt;
    private EditText userresetPwdEditTxt;
    private EditText userConfirmPwdEditTxt;

    String strUserName;
    String strUserEmail;
    String strUserTelNo;
    String strUserQeustion;
    String strUserAnswer;
    String strUserPassword;
    String strUserConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        showProfileInformation();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.updateprofile_btnSave) {
            updateProfile();
        }
        if (id == R.id.iv_updateprofile_back){
            finish();
        }
    }

    public void showProfileInformation() {
        Connect sharedInstance = Connect.getInstance(UpdateProfileActivity.this);
        SRUser currentUser = sharedInstance.getCurrentUser();

        ImageView avatarImageView = (ImageView) findViewById(R.id.updateprofile_imgviewUserAvatar);
        String avatarImageUrl = currentUser.userAvatarPath;
        DisplayImageOptions brandOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoader.getInstance().displayImage(avatarImageUrl, avatarImageView, brandOptions);

        userNameEditTxt = (EditText) findViewById(R.id.updateprofile_editUserName);
        userNameEditTxt.setText(currentUser.userName);
        userEmailEditTxt = (EditText) findViewById(R.id.updateprofile_editEmail);
        userEmailEditTxt.setText(currentUser.userEmail);
        userTelEditText = (EditText) findViewById(R.id.updateprofile_editTelNumber);
        userTelEditText.setText(currentUser.userTelNumber);
        userSecQueEditTxt = (EditText) findViewById(R.id.updateprofile_editSecQuestion);
        userSecQueEditTxt.setText(currentUser.userSecQuestion);
        userSecAnswerEditTxt = (EditText) findViewById(R.id.updateprofile_editSecAnswer);
        userSecAnswerEditTxt.setText(currentUser.userAnswer);
        userresetPwdEditTxt = (EditText) findViewById(R.id.updateprofile_editNewPassword);
        userConfirmPwdEditTxt = (EditText) findViewById(R.id.updateprofile_editConfirmPassword);

        ((Button)findViewById(R.id.updateprofile_btnSave)).setOnClickListener(this);
        ((ImageView)findViewById(R.id.iv_updateprofile_back)).setOnClickListener(this);
        ((ScrollView)findViewById(R.id.updateprofile_scrollview)).setOnTouchListener(this);
    }

    public void updateProfile() {
        strUserName = userNameEditTxt.getText().toString();
        strUserEmail = userEmailEditTxt.getText().toString();
        strUserTelNo = userTelEditText.getText().toString();
        strUserQeustion = userSecQueEditTxt.getText().toString();
        strUserAnswer = userSecAnswerEditTxt.getText().toString();
        strUserPassword = userresetPwdEditTxt.getText().toString();
        strUserConfirmPassword = userConfirmPwdEditTxt.getText().toString();

        if (!strUserPassword.equals(strUserConfirmPassword)) {
            alertShow("Error", "Password does not match");
        } else {
            new UpdateProfileTask().execute();
        }
    }

    public void alertShow(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                hideKeyboard(v);
                break;
        }
        return true;
    }

    public class UpdateProfileTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog mLoadingDialog = new ProgressDialog(UpdateProfileActivity.this);

        @Override
        protected void onPreExecute() {

            mLoadingDialog.setMessage("Updating profile...");
            mLoadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            SRUser user = Connect.getInstance(getBaseContext()).updateUserProfile(strUserName, strUserEmail, strUserTelNo, strUserQeustion, strUserAnswer, strUserPassword);
            if (user != null && user.userEmail != null) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mLoadingDialog.dismiss();

            if (success) {
                showProfileInformation();
            } else {
                alertShow("Error", "Update Failed");
            }
        }

        @Override
        protected void onCancelled() {
            mLoadingDialog.dismiss();
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodmanager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodmanager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
