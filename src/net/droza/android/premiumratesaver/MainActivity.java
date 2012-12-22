package net.droza.android.premiumratesaver;

import net.droza.android.premiumratesaver.fragment.HistoryFragment;
import net.droza.android.premiumratesaver.fragment.LookupFragment;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
 
public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
        actionBar.setDisplayShowTitleEnabled(false);
 
        Tab tab = actionBar.newTab()
            .setText(R.string.title_section_lookup)
            .setTabListener(new CustomTabListener<LookupFragment>(this, "Search", LookupFragment.class));
 
        actionBar.addTab(tab);
 
        tab = actionBar.newTab()
            .setText(R.string.title_section_history)
            .setTabListener(new CustomTabListener<HistoryFragment>(this, "History", HistoryFragment.class));
 
        actionBar.addTab(tab);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	menu.clear();
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
        case R.id.menu_settings:
          startActivity(new Intent(this, StartPreferenceActivity.class));

          return(true);
      }

      return(super.onOptionsItemSelected(item));
    }
}