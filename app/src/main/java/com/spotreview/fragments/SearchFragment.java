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
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.info.object.SpotObject;
import com.spotreview.adapters.SpotArrayAdapter;
import com.spotreview.services.Connect;
import com.spotreview.spotreview.MainActivity;
import com.spotreview.spotreview.R;
import com.spotreview.spotreview.SpotDetailsActivity;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements OnItemClickListener, OnQueryTextListener {

    private ArrayList<SpotObject> searchResults = null;
    private SpotArrayAdapter adapter = null;
    private SearchView searchView = null;
    private View rowView;
    private Boolean isSearchString;
    private String searchString;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), SpotDetailsActivity.class);
        intent.putExtra("company_name", searchResults.get(position).spotCompanyName);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText.equals("")) {
            isSearchString = false;
            ((ListView) rowView.findViewById(R.id.search_listView)).setVisibility(View.INVISIBLE);
        } else {
            isSearchString = true;
            ((ListView) rowView.findViewById(R.id.search_listView)).setVisibility(View.VISIBLE);
            searchString = newText;
        }

        new SearchTask().execute();
        return false;
    }

    public class SearchTask extends AsyncTask<Void, Void, ArrayList<SpotObject>> {

        private ProgressDialog mLoadingDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {

            mLoadingDialog.setMessage("Please wait...");
            mLoadingDialog.show();
        }

        @Override
        protected ArrayList<SpotObject> doInBackground(Void... params) {

            ArrayList<SpotObject> spotObjects;
            if (isSearchString) {
                spotObjects = Connect.getInstance(getActivity()).searchSpotsByString(searchString);
            } else {
                spotObjects = Connect.getInstance(getActivity()).searchSpots(1);
            }

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
                searchResults = null;
                rearrageSearchListView();
                Toast.makeText(getActivity(), "No results found", Toast.LENGTH_LONG).show();
            } else {
                searchResults = result;
                rearrageSearchListView();
            }
        }

        @Override
        protected void onCancelled() {
            mLoadingDialog.dismiss();
            Toast.makeText(getActivity(), "Connection Error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        isSearchString = false;

        // Inflate the layout for this fragment
        rowView = inflater.inflate(R.layout.fragment_search, container, false);
        ((ImageView)rowView.findViewById(R.id.iv_search_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).menuClick();
            }
        });
        searchView = (SearchView) rowView.findViewById(R.id.fragment_search_searchview);
        searchView.setOnQueryTextListener(this);
        ((ListView) rowView.findViewById(R.id.search_listView)).setVisibility(View.INVISIBLE);

        if (adapter == null) {
            new SearchTask().execute();

        } else {
            rearrageSearchListView();
        }

        return rowView;
    }

    public void rearrageSearchListView() {
        ((ListView) rowView.findViewById(R.id.search_listView)).setAdapter(new SpotArrayAdapter(this.getActivity(), searchResults, getActivity().getResources().getDrawable(R.drawable.logo)));
        ((ListView) rowView.findViewById(R.id.search_listView)).setOnItemClickListener(this);
    }
}
