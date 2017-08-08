package com.spotreview.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotreview.spotreview.EmailActivity;
import com.spotreview.spotreview.MainActivity;
import com.spotreview.spotreview.R;

public class ContactUsFragment extends Fragment {

    private TextView txtViewEmailAddress;
    private TextView txtViewWebsiteAddress;

    String strEmail = "info@spotreview.mobi";
    String strWebAddress = "<a href='http://www.spotreview.mobi'>www.spotreview.mobi</a>";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        ((ImageView)view.findViewById(R.id.iv_contactus_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).menuClick();
            }
        });

//        E-mail Address
        txtViewEmailAddress = (TextView)view.findViewById(R.id.contactus_txtEmail);

        SpannableString content = new SpannableString(strEmail);
        int posStart, posEnd;
        posStart = 0;
        posEnd = strEmail.length();
        content.setSpan(new UnderlineSpan(), posStart, posEnd, 0);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(getActivity(), EmailActivity.class);
                intent.putExtra("emailAddress", strEmail);
                startActivity(intent);
            }
        };

        content.setSpan(clickableSpan, posStart, posEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtViewEmailAddress.setText(content);
        txtViewEmailAddress.setMovementMethod(LinkMovementMethod.getInstance());

//        Website Address
        txtViewWebsiteAddress = (TextView)view.findViewById(R.id.contactus_txtwebsiteAddress);
        txtViewWebsiteAddress.setClickable(true);
        txtViewWebsiteAddress.setMovementMethod(LinkMovementMethod.getInstance());

        txtViewWebsiteAddress.setText(Html.fromHtml(strWebAddress));

        return view;
    }
}
