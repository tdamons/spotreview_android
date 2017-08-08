package com.spotreview.spotreview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.info.object.SpotDetailObject;
import com.spotreview.adapters.SpotDetailsAdapter;
import com.spotreview.services.Connect;

import java.util.ArrayList;

public class SpotDetailsActivity extends Activity implements SpotDetailsAdapter.OnSpotItemClickListener {

    private ArrayList<SpotDetailObject> spotDetails = null;
    private SpotDetailsAdapter adapter = null;
    String spotCompanyName;

    private ListView list;

    @Override
    public void onImageClick(ImageView image, SpotDetailObject details) {
        ImagePreviewActivity.show(this,image,details.brandImagePath);
    }

    public class GetSpotDetailsTask extends AsyncTask<Void, Void, ArrayList<SpotDetailObject>> {

        private ProgressDialog mLoadingDialog = new ProgressDialog(SpotDetailsActivity.this);

        @Override
        protected void onPreExecute() {
            mLoadingDialog.setMessage("Please wait...");
            mLoadingDialog.show();
        }

        @Override
        protected ArrayList<SpotDetailObject> doInBackground(Void... params) {

            ArrayList<SpotDetailObject> spotDetailObjects = Connect.getInstance(SpotDetailsActivity.this).getCompanyDetails(spotCompanyName);
            if (spotDetailObjects.size() > 0) {
                return spotDetailObjects;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<SpotDetailObject> result) {

            mLoadingDialog.dismiss();

            if (result == null) {
                Toast.makeText(SpotDetailsActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            } else {
                spotDetails = result;
                rearrageListView();
            }
        }

        @Override
        protected void onCancelled() {
            mLoadingDialog.dismiss();
            Toast.makeText(SpotDetailsActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_details);

        spotCompanyName = (String)getIntent().getExtras().get("company_name");
        ((TextView)findViewById(R.id.spotdetail_txttitle)).setText(spotCompanyName);
        ((ImageView)findViewById(R.id.iv_spotdetails_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotDetailsActivity.this.finish();
            }
        });
        list = ((ListView) findViewById(R.id.spotdetail_listview));

        if (adapter == null) {
            new GetSpotDetailsTask().execute();
        } else {
            list.setAdapter(new SpotDetailsAdapter(SpotDetailsActivity.this, spotDetails,this));
        }
    }

    public void rearrageListView() {
        list.setAdapter(new SpotDetailsAdapter(SpotDetailsActivity.this, spotDetails,this));
    }
}
