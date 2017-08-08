package com.spotreview.spotreview;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ThankYouActivity extends Activity implements View.OnClickListener{

    private Button btnShare, btnClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        btnShare = (Button) findViewById(R.id.thankyou_share);
        btnClose = (Button) findViewById(R.id.thankyou_close);
        btnShare.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btnClose){
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startMain);
        }
        if (v == btnShare){
            Intent intent = new Intent(this, ShareActivity.class);
            startActivity(intent);
        }
    }
}
