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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.info.object.SRUser;
import com.info.object.SpotObject;
import com.spotreview.adapters.SpotArrayAdapter;
import com.spotreview.services.Connect;
import com.spotreview.spotreview.MainActivity;
import com.spotreview.spotreview.R;
import com.spotreview.spotreview.SpotDetailsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyReviewFragment extends Fragment implements OnItemClickListener {

    private ArrayList<SpotObject> myReviews = null;
    private SpotArrayAdapter adapter = null;
    private View rowView;

    public class GetMyReviewsTask extends AsyncTask<Void, Void, ArrayList<SpotObject>> {

        private ProgressDialog mLoadingDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {

            mLoadingDialog.setMessage("Please wait...");
            mLoadingDialog.show();
        }

        @Override
        protected ArrayList<SpotObject> doInBackground(Void... params) {

            ArrayList<SpotObject> spotObjects = Connect.getInstance(getActivity()).getMyReviews(1);
            if (spotObjects == null) {
                return null;
            } else {
                return spotObjects;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<SpotObject> result) {

            mLoadingDialog.dismiss();

            if (result == null) {
//                Intent main = new Intent(getActivity(), MainActivity.class);
//                startActivity(main);
                myReviews = null;
                Toast.makeText(getActivity(), "There are no Reviews", Toast.LENGTH_LONG).show();
            } else {
               myReviews = result;
            }

            rearrageSearchListView();
        }

        @Override
        protected void onCancelled() {
            mLoadingDialog.dismiss();
            Toast.makeText(getActivity(), "There are no Reviews", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rowView = inflater.inflate(R.layout.fragment_my_review, container, false);

        ((ImageView)rowView.findViewById(R.id.iv_review_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).menuClick();
            }
        });

        if (adapter == null) {
            new GetMyReviewsTask().execute();
        } else {
            rearrageSearchListView();
        }

        return rowView;
    }

    public void rearrageSearchListView() {

        Collections.sort(myReviews, new Comparator<SpotObject>() {
            @Override
            public int compare(SpotObject lhs, SpotObject rhs) {
                return (lhs.spotCompanyName.toUpperCase()).compareTo((rhs.spotCompanyName.toUpperCase()));
            }
        });

        ((ListView) rowView.findViewById(R.id.myreview_listview)).setAdapter(new SpotArrayAdapter(this.getActivity(), myReviews, getActivity().getResources().getDrawable(R.drawable.logo)));
        ((ListView) rowView.findViewById(R.id.myreview_listview)).setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), SpotDetailsActivity.class);
        intent.putExtra("company_name", myReviews.get(position).spotCompanyName);
        startActivity(intent);
    }
}
