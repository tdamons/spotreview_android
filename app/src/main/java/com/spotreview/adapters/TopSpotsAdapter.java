package com.spotreview.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.info.object.SessionManager;
import com.info.object.SpotObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spotreview.spotreview.R;
import com.spotreview.spotreview.TopSpotsActivity;

import java.util.ArrayList;

public class TopSpotsAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<SpotObject> mList;
    private Drawable mIcon;
    private static LayoutInflater inflater;

    public TopSpotsAdapter(Context context, ArrayList<SpotObject> list, Drawable icon) {

        mContext = context;
        mList = list;
        mIcon = icon;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SpotObject spotObject = mList.get(position);
        View rowView;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.item_topspot, null);
        }
        else {
            rowView = convertView;
        }

        float goodRate = Float.parseFloat(spotObject.goodNumber) / (Float.parseFloat(spotObject.goodNumber) + Float.parseFloat(spotObject.badNumber)) * 100;
        float badrate = 100 - goodRate;

//        ImageView spotBrandImageView = (ImageView) rowView.findViewById(R.id.item_topspot_imageview);
//        String brandImageUrl = mList.get(position).spotImageUrl;
//        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
//        ImageLoader.getInstance().displayImage(brandImageUrl, spotBrandImageView, options);

//        spotBrandImageView.setTag(position);

        TextView spotNameTextView = (TextView) rowView.findViewById(R.id.item_topspot_spotname);
        TextView spotGoodRateTextView = (TextView) rowView.findViewById(R.id.item_topspot_goodrate);
        TextView spotBadRateTextView = (TextView) rowView.findViewById(R.id.item_topspot_badrate);

        spotNameTextView.setText(mList.get(position).spotCompanyName);
        spotGoodRateTextView.setText(String.format("%.0f", goodRate) + "%");
        spotBadRateTextView.setText(String.format("%.0f", badrate) + "%");


        if (SessionManager.isHot) {
            spotGoodRateTextView.setTextSize(20.0f);
            spotBadRateTextView.setTextSize(18.0f);
        } else {
            spotGoodRateTextView.setTextSize(18.0f);
            spotBadRateTextView.setTextSize(20.0f);
        }

        ImageView iconImageView = (ImageView) rowView.findViewById(R.id.item_topspot_icon);
        iconImageView.setImageDrawable(mIcon);

        return rowView;
    }
}
