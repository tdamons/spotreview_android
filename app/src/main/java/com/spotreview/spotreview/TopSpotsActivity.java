package com.spotreview.spotreview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.info.object.SpotObject;
import com.spotreview.adapters.SpotArrayAdapter;
import com.spotreview.adapters.TopSpotsAdapter;
import com.spotreview.services.Connect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TopSpotsActivity extends Activity implements OnItemClickListener{

    private ArrayList<SpotObject> topSpots = null;
    private TopSpotsAdapter adapter = null;
    Boolean isTop;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(TopSpotsActivity.this, SpotDetailsActivity.class);
        intent.putExtra("company_name", topSpots.get(position).spotCompanyName);
        startActivity(intent);
    }

    public class GetTopSpotsTask extends AsyncTask<Void, Void, ArrayList<SpotObject>> {

        private ProgressDialog mLoadingDialog = new ProgressDialog(TopSpotsActivity.this);

        @Override
        protected void onPreExecute() {
            mLoadingDialog.setMessage("Please wait...");
            mLoadingDialog.show();
        }

        @Override
        protected ArrayList<SpotObject> doInBackground(Void... params) {

            ArrayList<SpotObject> spotObjects = new ArrayList<SpotObject>();

            if (isTop) {
                spotObjects = Connect.getInstance(TopSpotsActivity.this).getTopSpots(1);
            } else {
                spotObjects = Connect.getInstance(TopSpotsActivity.this).getBadSpots(1);
            }

            if (spotObjects.size() > 0) {
                return spotObjects;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<SpotObject> result) {

            mLoadingDialog.dismiss();

            if (result == null) {
//                Intent main = new Intent(getActivity(), MainActivity.class);
//                startActivity(main);
                Toast.makeText(TopSpotsActivity.this, "There are no Spots", Toast.LENGTH_LONG).show();
            } else {
                topSpots = result;
                rearrageListView();
            }
        }

        @Override
        protected void onCancelled() {
            mLoadingDialog.dismiss();
            Toast.makeText(TopSpotsActivity.this, "There are no Spots", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_spots);

        TextView txtViewTitle = (TextView)findViewById(R.id.topspot_title);
        isTop = (Boolean) getIntent().getExtras().get("istop");

        if (isTop) {
            txtViewTitle.setText("TOP 5 HOT SPOTS");
        } else {
            txtViewTitle.setText("TOP 5 COLD SPOTS");
        }

        if (adapter == null) {
            new GetTopSpotsTask().execute();
        } else {

            ((ListView) findViewById(R.id.topspots_listView)).setAdapter(new TopSpotsAdapter(TopSpotsActivity.this, topSpots, TopSpotsActivity.this.getResources().getDrawable(R.drawable.logo)));
        }
        ((ListView) findViewById(R.id.topspots_listView)).setOnItemClickListener(this);
        ((ImageView)findViewById(R.id.iv_top5_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopSpotsActivity.this.finish();
            }
        });
    }

    public void rearrageListView() {

        Collections.sort(topSpots, new Comparator<SpotObject>() {
            @Override
            public int compare(SpotObject lhs, SpotObject rhs) {
                return (lhs.spotCompanyName.toUpperCase()).compareTo((rhs.spotCompanyName.toUpperCase()));
            }
        });

        ((ListView) findViewById(R.id.topspots_listView)).setAdapter(new TopSpotsAdapter(TopSpotsActivity.this, topSpots, TopSpotsActivity.this.getResources().getDrawable(R.drawable.logo)));
    }

}
