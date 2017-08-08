package com.spotreview.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.spotreview.spotreview.MainActivity;
import com.spotreview.spotreview.R;
import com.spotreview.spotreview.SpotDetailsActivity;
import com.spotreview.spotreview.TakePictureActivity;

import java.util.IllegalFormatCodePointException;

public class HomeFragment extends Fragment implements OnClickListener{

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View homeView = inflater.inflate(R.layout.fragment_home, container, false);
        ImageView ivMenu = (ImageView)homeView.findViewById(R.id.iv_home_menu);
        ivMenu.setOnClickListener(this);
        Button btnStart = (Button)homeView.findViewById(R.id.home_btnStart);
        btnStart.setOnClickListener(this);

        return homeView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.home_btnStart) {
            Intent intent = new Intent(getActivity(), TakePictureActivity.class);
            startActivity(intent);
        }
        if (id == R.id.iv_home_menu) {
            ((MainActivity)getActivity()).menuClick();
        }
    }
}
