package com.spotreview.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.info.object.SpotObject;
import com.spotreview.spotreview.R;

import java.util.ArrayList;

public class SpotArrayAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<SpotObject> mList;
    private Drawable mIcon;
    private static LayoutInflater inflater;

    public SpotArrayAdapter(Context context, ArrayList<SpotObject> list, Drawable icon) {

        mContext = context;
        mList = list;
        mIcon = icon;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        } else {
            return mList.size();
        }
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
            rowView = inflater.inflate(R.layout.spotarray_item, null);
        }
        else {
            rowView = convertView;
        }

        float goodRate = Float.parseFloat(spotObject.goodNumber) / (Float.parseFloat(spotObject.goodNumber) + Float.parseFloat(spotObject.badNumber)) * 100;
        float badRate = 100 - goodRate;

        TextView spotNameTextView = (TextView) rowView.findViewById(R.id.spotarray_spotName);
        TextView spotGoodRateTextView = (TextView) rowView.findViewById(R.id.spotarray_goodRate);
        TextView spotBadRateTextView = (TextView) rowView.findViewById(R.id.spotarray_badRate);
        ImageView iconImageView = (ImageView) rowView.findViewById(R.id.spotarray_icon);

        if (goodRate>badRate){
            spotGoodRateTextView.setTextSize(20);
            spotBadRateTextView.setTextSize(18);
        }
        else {
            spotGoodRateTextView.setTextSize(18);
            spotBadRateTextView.setTextSize(20);
        }


        spotNameTextView.setText(mList.get(position).spotCompanyName);
        spotGoodRateTextView.setText(String.format("%.0f", goodRate)+"%");
        spotBadRateTextView.setText(String.format("%.0f", badRate)+"%");
        iconImageView.setImageDrawable(mIcon);

        return rowView;
    }
}
