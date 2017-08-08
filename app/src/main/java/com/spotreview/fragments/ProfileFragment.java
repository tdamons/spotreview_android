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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.info.object.SRUser;
import com.info.object.SpotObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spotreview.services.Connect;
import com.spotreview.spotreview.LoginActivity;
import com.spotreview.spotreview.MainActivity;
import com.spotreview.spotreview.R;
import com.spotreview.spotreview.SpotDetailsActivity;
import com.spotreview.spotreview.UpdateProfileActivity;

import java.util.ArrayList;

public class ProfileFragment extends Fragment implements OnClickListener{


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        ((ImageView) profileView.findViewById(R.id.iv_profile_menu)).setOnClickListener(this);
        ((Button)profileView.findViewById(R.id.myprofile_btnedit)).setOnClickListener(this);

        showProfileInformation(profileView);

        return profileView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.myprofile_btnedit) {
            Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
            startActivity(intent);
        }

        if (id == R.id.iv_profile_menu) {
            ((MainActivity)getActivity()).menuClick();
        }
    }

    public void showProfileInformation(View profileView) {
        Connect sharedInstance = Connect.getInstance(getActivity());
        SRUser currentUser = sharedInstance.getCurrentUser();

        ImageView avatarImageView = (ImageView) profileView.findViewById(R.id.myprofile_imgviewAvatar);
        String avatarImageUrl = currentUser.userAvatarPath;
        DisplayImageOptions brandOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoader.getInstance().displayImage(avatarImageUrl, avatarImageView, brandOptions);

        TextView userNameTxtView = (TextView) profileView.findViewById(R.id.myprofile_txtUserName);
        userNameTxtView.setText(currentUser.userName);
        TextView userDisplayNameTxtView = (TextView) profileView.findViewById(R.id.myprofile_txtDisplayName);
        userDisplayNameTxtView.setText(currentUser.userName);
        TextView userEmailTxtView = (TextView) profileView.findViewById(R.id.myprofile_txtUserEmail);
        userEmailTxtView.setText(currentUser.userEmail);
        TextView userTelNoTextView = (TextView) profileView.findViewById(R.id.myprofile_txtUserTelNumber);
        userTelNoTextView.setText(currentUser.userTelNumber);
        TextView userSecQuestionTxtView = (TextView) profileView.findViewById(R.id.myprofile_txtUserSecQuestion);
        userSecQuestionTxtView.setText(currentUser.userSecQuestion);
        TextView userSecAnswerTxtView = (TextView) profileView.findViewById(R.id.myprofile_txtUserSeqAnswer);
        userSecAnswerTxtView.setText(currentUser.userAnswer);
    }
}
