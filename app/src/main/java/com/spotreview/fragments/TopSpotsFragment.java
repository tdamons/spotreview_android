package com.spotreview.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.info.object.SessionManager;
import com.info.object.SpotObject;
import com.spotreview.adapters.SpotArrayAdapter;
import com.spotreview.adapters.TopSpotsAdapter;
import com.spotreview.services.Connect;
import com.spotreview.spotreview.MainActivity;
import com.spotreview.spotreview.R;
import com.spotreview.spotreview.TopSpotsActivity;

import java.util.ArrayList;

public class TopSpotsFragment extends Fragment implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_spots, container, false);

        ((ImageView)view.findViewById(R.id.iv_topspots_menu)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.topspot_btnHotSpot)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.topspot_btnColdSpot)).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.topspot_btnHotSpot) {
            Intent intent = new Intent(getActivity(), TopSpotsActivity.class);
            intent.putExtra("istop", true);
            SessionManager.isHot = true;
            startActivity(intent);
        }
        else if (id == R.id.topspot_btnColdSpot) {
            Intent intent = new Intent(getActivity(), TopSpotsActivity.class);
            intent.putExtra("istop", false);
            SessionManager.isHot = false;
            startActivity(intent);
        }

        if (id == R.id.iv_topspots_menu) {
            ((MainActivity)getActivity()).menuClick();
        }
    }
}
