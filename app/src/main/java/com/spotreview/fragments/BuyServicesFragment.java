package com.spotreview.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.SpannableStringBuilder;
import android.widget.Toast;

import com.spotreview.spotreview.MainActivity;
import com.spotreview.spotreview.R;

public class BuyServicesFragment extends Fragment {

    private TextView txtViewBuyService;
    private String strBuyDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View buyserviceView = inflater.inflate(R.layout.fragment_buy_services, container, false);

        ((ImageView)buyserviceView.findViewById(R.id.iv_buyservices_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).menuClick();
            }
        });

        txtViewBuyService = (TextView)buyserviceView.findViewById(R.id.buyservices_txtView);

        strBuyDetail = "We offer the following\nservices to our clients:\n\nReports\nIncentive Voucher System\nConsulting\n\nIf you'd like to find out more\n" +
                "about this, please Contact Us\nand we will be more than\nhappy to discuss the various\noptions and pricing with you";

        SpannableString content = new SpannableString(strBuyDetail);
        int posStart, posEnd;
        posStart = strBuyDetail.indexOf("Contact Us");
        posEnd = posStart + "Contact Us".length();
        content.setSpan(new UnderlineSpan(), posStart, posEnd, 0);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.mainContent, new ContactUsFragment());
                ft.commit();
            }
        };

        content.setSpan(clickableSpan, posStart, posEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtViewBuyService.setText(content);
        txtViewBuyService.setMovementMethod(LinkMovementMethod.getInstance());

        return buyserviceView;
    }
}
