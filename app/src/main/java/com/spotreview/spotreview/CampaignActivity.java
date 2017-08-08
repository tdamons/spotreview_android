package com.spotreview.spotreview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CampaignActivity extends Activity implements View.OnClickListener {

    private ImageView ivBack, ivCampaign;
    private TextView tvOK;

    String strCampaignName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        String strCampaignUrl = getIntent().getStringExtra("campaignUrl");
        strCampaignName = getIntent().getStringExtra("campaignName");

        ivCampaign = (ImageView) findViewById(R.id.iv_campaign);
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoader.getInstance().displayImage(strCampaignUrl, ivCampaign, displayImageOptions);

        ivBack = (ImageView) findViewById(R.id.iv_campaign_back);
        ivBack.setOnClickListener(this);

        tvOK = (TextView) findViewById(R.id.tv_campaign_ok);
        tvOK.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack)
            finish();
        if (v == tvOK){
            Intent intent = new Intent(this, TakePictureActivity.class);
            intent.putExtra("campaignName", strCampaignName);
            startActivity(intent);
        }
    }
}
