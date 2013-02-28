package net.droza.android.premiumratesaver;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
 
public class CustomTabListener<T extends Fragment> implements TabListener {
    private Fragment mFragment;
    private final Activity mActivity;
    private final String mTag;
    private final Class<T> mClass;
 
    public CustomTabListener(Activity activity, String tag, Class<T> clz){
        mActivity = activity;
        mTag = tag;
        mClass = clz;
    }
 
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // Nothing special to do here for this application
    }
 
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction discard) {

    	FragmentManager fragMgr = ((FragmentActivity)mActivity).getSupportFragmentManager();
        FragmentTransaction ft = fragMgr.beginTransaction();

        // Check if the fragment is already initialized
        if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = Fragment.instantiate(mActivity, mClass.getName());

            ft.add(android.R.id.content, mFragment, mTag);
        } else {
            // If it exists, simply attach it in order to show it
            ft.attach(mFragment);
        }
        ft.commit();
    }
 
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        if(mFragment!=null)
            ft.detach(mFragment);
    }
}
