package com.spotreview.spotreview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.info.object.SRUser;
import com.info.object.SessionManager;
import com.spotreview.services.Connect;
import com.spotreview.services.S3transferUtil;

import java.io.File;
import java.util.List;


public class CommentActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;
    private List<TransferObserver> observers;

    private SubmitSpotTask submitSpotTask= null;

    private EditText editBrandName;
    private EditText editComment;

    String strBrandName;
    String strComment;
    String imageName;
    int userID;

    private ProgressBar mProgressBar;

    private static final String TAG = "UploadImage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Initializes TransferUtility, always do this before using it.
        transferUtility = S3transferUtil.getTransferUtility(CommentActivity.this);

        initData();

        editBrandName = (EditText)findViewById(R.id.comment_editBrandName);
        editComment = (EditText)findViewById(R.id.comment_editComment);
        mProgressBar = (ProgressBar) findViewById(R.id.comment_progressbar);
        mProgressBar.setVisibility(View.GONE);

        if (SessionManager.postObject.postedTitle!=null){
            editBrandName.setText(SessionManager.postObject.postedTitle);
            editBrandName.setKeyListener(null);
        }

        ((ScrollView)findViewById(R.id.comment_scrollview)).setOnTouchListener(this);
        ((ImageView)findViewById(R.id.iv_comment_back)).setOnClickListener(this);
        ((Button)findViewById(R.id.comment_btnOk)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.comment_btnOk) {
            postSpots();
        }
        if(id == R.id.iv_comment_back){
            finish();
        }
    }

    public void postSpots() {
        strBrandName = editBrandName.getText().toString();
        strComment = editComment.getText().toString();
        if (TextUtils.isEmpty(strBrandName)) {
            alertShow("Error", "Please add company name.");
        }
        else if (TextUtils.isEmpty(strComment)){
            alertShow("Error", "Please add comment.");
        }
        else {
            SessionManager.postObject.postedTitle = strBrandName;
            SessionManager.postObject.postedContent = strComment;
            beginUploadImage(SessionManager.postObject.strPostedImagePath);
        }
    }

    private void initData() {
//        observers = transferUtility.getTransfersWithType(TransferType.UPLOAD);
//        for (TransferObserver observer : observers) {
//             if (!TransferState.COMPLETED.equals(observer.getState())
//                 && !TransferState.FAILED.equals(observer.getState())
//                 && !TransferState.CANCELED.equals(observer.getState())) {
//
//                 observer.setTransferListener(new UploadListener());
//             }
//        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodmanager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodmanager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    private void beginUploadImage(String filePath) {

//        AmazonS3Client s3Client = new AmazonS3Client( new BasicAWSCredentials( MY_ACCESS_KEY_ID, MY_SECRET_KEY ) );
//        s3Client.createBucket( MY_PICTURE_BUCKET );
//        PutObjectRequest por = new PutObjectRequest( Constants.getPictureBucket(), Constants.PICTURE_NAME, new java.io.File( filePath) );
//        s3Client.putObject(por);

        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }

        File file = new File(filePath);
        TransferObserver observer;
        imageName = System.currentTimeMillis()+".jpg";

        observer = transferUtility.upload(SessionManager.BUCKET_NAME, imageName, file);

        observer.setTransferListener(new TransferListener() {
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int progress = (int) ((double) bytesCurrent / bytesTotal);
                // update progress bar

                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(progress);

                if(bytesCurrent == bytesTotal)
                {
                    //    transferComplete = true;
                    Toast.makeText(CommentActivity.this,"Upload completed!",Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    submitToWebService();
                }
            }

            public void onStateChanged(int id, TransferState state) {
                Toast.makeText(CommentActivity.this,"State changed to : "+state.toString(),Toast.LENGTH_SHORT).show();

            }

            public void onError(int id, Exception ex) {
                Toast.makeText(CommentActivity.this,"Upload Unsuccessful due to `"+ex.toString(),Toast.LENGTH_LONG).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void submitToWebService(){
        SRUser oldUser = SRUser.loadFromDisk(this);
        userID = oldUser.userId;

        submitSpotTask = new SubmitSpotTask();
        submitSpotTask.execute();
    }

    public class SubmitSpotTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog mLoadingDialog = new ProgressDialog(CommentActivity.this);

        @Override
        protected void onPreExecute() {

            mLoadingDialog.setMessage("Submit To Server...");
            mLoadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean response = Connect.getInstance(getBaseContext()).spotPost(imageName, userID);
            if (response) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            submitSpotTask = null;
            mLoadingDialog.dismiss();

            if (success) {
                alertShow("", "Successfully posted to SpotReview");
            } else {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {

            submitSpotTask = null;
            mLoadingDialog.dismiss();
        }
    }


    //    private class UploadListener implements TransferListener {
//
//        // Simply updates the UI list when notified.
//        @Override
//        public void onError(int id, Exception e) {
//            Log.e(TAG, "Error during upload: " + id, e);
//            mLoadingDialog.dismiss();
//            alertShow("Error", "Can't upload image");
//        }
//
//        @Override
//        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//            Log.e(TAG, "OnProgressChanged: " + id);
//            mLoadingDialog.dismiss();
//        }
//
//        @Override
//        public void onStateChanged(int id, TransferState newState) {
//            if (newState == TransferState.COMPLETED) {
//                alertShow("Success", "Successed to upload image");
//            }
//            else if (newState == TransferState.FAILED) {
//                alertShow("Error", "Can't upload image");
//            }
//            mLoadingDialog.dismiss();
//        }
//    }

    public void alertShow(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CommentActivity.this, ThankYouActivity.class);

                        startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
