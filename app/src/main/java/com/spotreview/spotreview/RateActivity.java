package com.spotreview.spotreview;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.info.object.SRPostObject;
import com.info.object.SessionManager;

public class RateActivity extends Activity implements OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        ((ImageView)findViewById(R.id.iv_rateexperience_back)).setOnClickListener(this);
        ((Button)findViewById(R.id.rate_btnGood)).setOnClickListener(this);
        ((Button)findViewById(R.id.rate_btnBad)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rate_btnGood) {
            SessionManager.postObject.postStatus = SRPostObject.SRPostStatus.SRGoodStatus;
        } else {
            SessionManager.postObject.postStatus = SRPostObject.SRPostStatus.SRBadStatus;
        }

        if(id == R.id.iv_rateexperience_back){
            finish();
            return;
        }

        Intent intent = new Intent(RateActivity.this, CommentActivity.class);
        startActivity(intent);
    }
}
