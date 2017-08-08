package com.spotreview.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.info.object.SRPostObject;
import com.info.object.SpotDetailObject;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.spotreview.spotreview.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SpotDetailsAdapter extends BaseAdapter{
    private final OnSpotItemClickListener clickListener;
    private Context mContext;
    private ArrayList<SpotDetailObject> mList;
    private static LayoutInflater inflater;

    public interface OnSpotItemClickListener{
        void onImageClick(ImageView image, SpotDetailObject details);
    }
    public SpotDetailsAdapter(Context context, ArrayList<SpotDetailObject> list,OnSpotItemClickListener listener) {

        mContext = context;
        mList = list;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        clickListener = listener;
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
        final SpotDetailObject spotDetailObject = mList.get(position);
        View rowView;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.item_spotdetail, null);
        }
        else {
            rowView = convertView;
        }

        ImageView userAvatarImageView = (ImageView) rowView.findViewById(R.id.item_spotdetail_avatar);
        String avatarImageUrl = spotDetailObject.userAvatarpath;

        final ImageView brandImageView = (ImageView) rowView.findViewById(R.id.item_spotdetail_imgbrand);
        String brandImageUrl = spotDetailObject.brandImagePath;

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        DisplayImageOptions brandOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(null)
                .showImageOnFail(null)
                .showImageOnLoading(null)
                .build();

//        DisplayImageOptions brandOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoader.getInstance().displayImage(avatarImageUrl, userAvatarImageView, brandOptions);
        ImageLoader.getInstance().displayImage(brandImageUrl, brandImageView, brandOptions);

        //Bitmap bitmap = ImageLoader.getInstance().loadImageSync(brandImageUrl,brandOptions);

        /*if (bitmap!=null) {
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;

            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            int height = (bitmapHeight * width) / bitmapWidth;

            brandImageView.getLayoutParams().height = height;
//            brandImageView.setImageBitmap(bitmap);
        }*/
        ViewCompat.setTransitionName(brandImageView,brandImageUrl);
        userAvatarImageView.setTag(position);
        brandImageView.setTag(position);
        if (clickListener!=null){
            brandImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onImageClick(brandImageView,spotDetailObject);
                }
            });
        }
        TextView userNameTextView = (TextView) rowView.findViewById(R.id.item_spotdetail_username);
        userNameTextView.setText(spotDetailObject.userName);
        TextView spotRateTextView = (TextView) rowView.findViewById(R.id.item_spotdetail_txtrate);

        if (spotDetailObject.postStatus == SRPostObject.SRPostStatus.SRGoodStatus) {
            spotRateTextView.setText("GOOD");
            spotRateTextView.setTextColor(Color.parseColor("#008000"));
        } else {
            spotRateTextView.setText("BAD");
            spotRateTextView.setTextColor(Color.parseColor("#BF0302"));
        }

        TextView postedDateTxtView = (TextView) rowView.findViewById(R.id.item_spotdetail_txtdate);

        String dateString = spotDetailObject.spotCreatedDate;
        SimpleDateFormat dateInputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateOutputFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = (Date) dateInputFormat.parse(dateString);
            dateString = dateOutputFormat.format(convertedDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        postedDateTxtView.setText(dateString);

        TextView commentTxtView = (TextView) rowView.findViewById(R.id.item_spotdetail_txtcomment);
        commentTxtView.setText(spotDetailObject.spotDescription);

        return rowView;
    }
}
