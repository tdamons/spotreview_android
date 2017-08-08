package com.spotreview.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.spotreview.spotreview.R;

import java.util.ArrayList;

public class MenuArrayAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList<String> mList;

    public MenuArrayAdapter(Context context, ArrayList<String> list) {
        super(context, R.layout.drawer_item, list);

        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public String getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        }
        else {
            view = convertView;
        }

        TextView menuItemView = (TextView) view.findViewById(R.id.menu_title);
        menuItemView.setText(mList.get(position));

        return view;
    }
}
