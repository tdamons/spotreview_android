package com.spotreview.spotreview;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.info.object.SRUser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spotreview.adapters.MenuArrayAdapter;
import com.spotreview.fragments.AboutUsFragment;
import com.spotreview.fragments.BuyServicesFragment;
import com.spotreview.fragments.CampaignModeFragment;
import com.spotreview.fragments.ContactUsFragment;
import com.spotreview.fragments.HomeFragment;
import com.spotreview.fragments.MyReviewFragment;
import com.spotreview.fragments.ProfileFragment;
import com.spotreview.fragments.SearchFragment;
import com.spotreview.fragments.TopSpotsFragment;
import com.spotreview.services.Connect;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends FragmentActivity {

    private static String TAG = MainActivity.class.getSimpleName();

    private ListView mDrawerList;
    private RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<String> mMenuItems = new ArrayList<>(Arrays.asList("Home", "My Profile", "My Reviews", "Top Spots", "Campaign Mode", "About Us", "Contact Us", "Our Services", "Logout"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Menu Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.menuList);
        MenuArrayAdapter menuAdapter= new MenuArrayAdapter(this, mMenuItems);
        mDrawerList.setAdapter(menuAdapter);

//        profile Image
        Connect sharedInstance = Connect.getInstance(this);
        SRUser currentUser = sharedInstance.getCurrentUser();

        ImageView ivAvatar = (ImageView) findViewById(R.id.avatar);
        String avatarImageUrl = currentUser.userAvatarPath;
        DisplayImageOptions brandOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoader.getInstance().displayImage(avatarImageUrl, ivAvatar, brandOptions);

        TextView tvUserName = (TextView)findViewById(R.id.userName);
        tvUserName.setText(currentUser.userName);

//        Set fragment
        Fragment fragment = new HomeFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit();

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /*
* Called when a particular item from the menu drawer is selected.
* */
    private void selectItemFromDrawer(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new ProfileFragment();
                break;
            case 2:
                fragment = new MyReviewFragment();
                break;
            case 3:
                fragment = new TopSpotsFragment();
                break;
//            case 4:
//                fragment = new SearchFragment();
//                break;
            case 4:
                fragment = new CampaignModeFragment();
                break;
            case 5:
                fragment = new AboutUsFragment();
                break;
            case 6:
                fragment = new ContactUsFragment();
                break;
            case 7:
                fragment = new BuyServicesFragment();
                break;
            case 8:
                SRUser oldUser = new SRUser(null);
                oldUser.saveOnDisk(this);
                MainActivity.this.finish();
                break;
        }

        if (fragment!=null)
            fragmentManager.beginTransaction().replace(R.id.mainContent, fragment).commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuItems.get(position));
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    public void menuClick() {
        mDrawerLayout.openDrawer(mDrawerPane);
    }

}
