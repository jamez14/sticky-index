package br.com.stickyindexsample;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import br.com.customsearchable.SearchActivity;
import br.com.customsearchable.contract.CustomSearchableConstants;
import br.com.customsearchable.model.ResultItem;
import br.com.stickyindexsample.adapter.ContactsAdapter;

/**
 * Created by Edgar on 29/05/2015.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private ContactsAdapter viewPagerAdapter;
    private Boolean isPageViewWithOffset = Boolean.FALSE;

    // Activity Callbacks __________________________________________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        this.getSupportActionBar().setElevation(0);

        getWindow().setSharedElementExitTransition(new Slide());
        getWindow().setSharedElementEnterTransition(new Slide());

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPagerAdapter = new ContactsAdapter(this.getSupportFragmentManager(), this);

        viewPager.setAdapter(viewPagerAdapter);

        tabs.setShouldExpand(true);
        tabs.setTextColor(getResources().getColor(R.color.text_primary));
        tabs.setDividerColor(getResources().getColor(R.color.primary));
        tabs.setIndicatorColorResource(R.color.text_primary);
        tabs.setIndicatorHeight(7);

        // Bind the tabs to the ViewPager
        tabs.setViewPager(viewPager);

        //Start in Contacts Fragment
        viewPager.setCurrentItem(1);

        // Listener for slide animation on searchable selected
        setOnSearchableListener();

        // Configure custom-searchable UI
        // CustomSearchableInfo.setTransparencyColor(Color.parseColor("#0288D1"));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isPageViewWithOffset) {
            tabs.animate().translationY(0);
            viewPager.animate().translationY(0);
            isPageViewWithOffset = Boolean.FALSE;
        }

        //this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    // MENU ________________________________________________________________________________________
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);

            tabs.animate().translationY(-tabs.getHeight());
            viewPager.animate().translationY(-tabs.getHeight());
            isPageViewWithOffset = Boolean.TRUE;

            //this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            View contactFragmentRootView = ((ContactsFragment) viewPagerAdapter.getItem(1)).getRootView();
            contactFragmentRootView.invalidate();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // SEARCHABLE INTERFACE ________________________________________________________________________
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, "typed: " + query, Toast.LENGTH_SHORT).show();
        }else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Bundle bundle = this.getIntent().getExtras();

            assert (bundle != null);

            if (bundle != null) {
                ResultItem receivedItem = bundle.getParcelable(CustomSearchableConstants.CLICKED_RESULT_ITEM);
                ((ContactsFragment) viewPagerAdapter.getItem(1)).updateRecyclerViewFromSearchSelection(receivedItem.getHeader());
            }
        }
    }

    private void setOnSearchableListener () {
        SearchManager searchDialog = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        searchDialog.setOnCancelListener(new SearchManager.OnCancelListener() {
            @Override
            public void onCancel() {
                tabs.animate().translationY(0);
                viewPager.animate().translationY(0);
            }
        });

        searchDialog.setOnDismissListener(new SearchManager.OnDismissListener() {
            @Override
            public void onDismiss() {
                tabs.animate().translationY(0);
                viewPager.animate().translationY(0);
            }
        });
    }
}

