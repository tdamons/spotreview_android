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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.info.object.SRCampaignObject;
import com.info.object.SpotObject;
import com.spotreview.services.Connect;
import com.spotreview.spotreview.CampaignActivity;
import com.spotreview.spotreview.MainActivity;
import com.spotreview.spotreview.R;

import java.util.ArrayList;

public class CampaignModeFragment extends Fragment implements View.OnClickListener {

    private EditText etCampaignCode;
    private Button btnOK;
    private String campaignCode;
    private SRCampaignObject myCampaign= null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_campaign_mode, container, false);

        ((ImageView)view.findViewById(R.id.iv_campaignmode_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).menuClick();
            }
        });

        etCampaignCode = (EditText) view.findViewById(R.id.et_campaigncode);
        btnOK = (Button) view.findViewById(R.id.btn_campaignmode_ok);
        btnOK.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == btnOK){
            if (etCampaignCode.getText().toString().matches("")){
                Toast.makeText(getActivity(), "Please enter campaign code!", Toast.LENGTH_SHORT).show();
            }
            else {
                campaignCode = etCampaignCode.getText().toString();
                new GetCampaignTask().execute();
            }
        }
    }

    public class GetCampaignTask extends AsyncTask<Void, Void, SRCampaignObject> {

        private ProgressDialog mLoadingDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {

            mLoadingDialog.setMessage("Please wait...");
            mLoadingDialog.show();
        }

        @Override
        protected SRCampaignObject doInBackground(Void... params) {

            SRCampaignObject srCampaignObject = Connect.getInstance(getActivity()).getCampaign(campaignCode);
            if (srCampaignObject == null) {
                return null;
            } else {
                return srCampaignObject;
            }
        }

        @Override
        protected void onPostExecute(SRCampaignObject result) {

            mLoadingDialog.dismiss();

            if (result == null) {
                myCampaign = null;
                Toast.makeText(getActivity(), "Please enter a valid\ncampaign code.", Toast.LENGTH_LONG).show();
            } else {
                myCampaign = result;
                Intent intent = new Intent(getActivity(), CampaignActivity.class);
                intent.putExtra("campaignUrl", myCampaign.campaignBranding);
                intent.putExtra("campaignName", myCampaign.campaignName);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mLoadingDialog.dismiss();
            Toast.makeText(getActivity(), "Connection Error", Toast.LENGTH_LONG).show();
        }
    }
}
