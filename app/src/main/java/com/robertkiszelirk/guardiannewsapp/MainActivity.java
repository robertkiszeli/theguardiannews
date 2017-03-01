package com.robertkiszelirk.guardiannewsapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.net.Uri;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.robertkiszelirk.guardiannewsapp.QueryUtils.getPagesCount;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<BaseArticleData>>{

    // Adapter for the articles
    private ArticleAdapter articleAdapter;
    // ID of the loader class
    private static final int ARTICLE_LOADER_ID = 1;
    // List view for the article list
    private ListViewCompat articleListView = null;
    // Empty list view
    private TextView emptyView;
    // Layout for the spinner progressbar
    private LinearLayoutCompat progressBarLayout;
    // String fro the search field
    private String searchString ="";
    // String to select section
    private String searchSection =null;
    // String for beginning date of articles
    public static String FROM_DATE = "";
    // String for ending date of articles
    public static String TO_DATE = "";
    // Tracking for the current article page
    private int currentPage = 1;
    // Number of the article pages
    private int pagesCount = 0;
    // Drawing the current and all article pages number
    private AppCompatTextView pageText;
    // Change article page Up/Down
    private AppCompatImageButton pageUp = null;
    private AppCompatImageButton pageDown = null;
    // Calendar to get the date
    private static final Calendar CALENDAR = Calendar.getInstance();
    // Navigation Drawer Layout
    private DrawerLayout drawerLayout = null;
    // Navigation Drawer Toggle Button
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle application when orientation changes
        // Get current date in custom format
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(savedInstanceState == null) {
            FROM_DATE = sdf.format(CALENDAR.getTime());
            TO_DATE = sdf.format(CALENDAR.getTime());
        }else{
            FROM_DATE = savedInstanceState.getString(getString(R.string.from_date));
            TO_DATE = savedInstanceState.getString(getString(R.string.to_date));
            searchSection = savedInstanceState.getString(getString(R.string.search_section));
            searchString = savedInstanceState.getString(getString(R.string.search_string));
            currentPage = savedInstanceState.getInt(getString(R.string.current_page));
        }
        // Set title at start
        if(searchSection == null) {
            setTitle(getString(R.string.fresh_news) + searchString);
        }else{
            if(searchString.equals("")){
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.news) + searchSection);
                }
            }else {
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.news) + searchSection + "/" + searchString);
                }
            }
        }
        // Find reference for empty view
        emptyView = (TextView) findViewById(R.id.empty);
        // Find reference to the article List View
        articleListView = (ListViewCompat) findViewById(R.id.article_list_view);
        // Find a reference to the article Adapter
        articleAdapter = new ArticleAdapter(MainActivity.this,new ArrayList<BaseArticleData>());
        // Set adapter on the list view to populate it
        articleListView.setAdapter(articleAdapter);
        // Handle listview item click, open article website
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get selected object
                final BaseArticleData articleSelected = articleAdapter.getItem(position);
                // Check for valid item and url
                if(articleSelected != null && articleSelected.getArticleURL() != null){
                    // Build the URL
                    Uri articleUrl = Uri.parse(articleSelected.getArticleURL());
                    // Create intent
                    Intent urlIntent = new Intent(Intent.ACTION_VIEW,articleUrl);
                    // Verify it resolves
                    PackageManager packageManager = getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(urlIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;
                    // If intent ok then start it
                    if (isIntentSafe) {
                        startActivity(urlIntent);
                    }

                }
            }
        });
        // Find reference for progressbar
        progressBarLayout = (LinearLayoutCompat) findViewById(R.id.progress_bar_layout);
        // Find reference for from date button
        AppCompatButton fromDateButton = (AppCompatButton) findViewById(R.id.from_date_button);
        // Set text for from date button
        fromDateButton.setText(FROM_DATE);
        // Handle click on from date button
        fromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create dialog fragment for date picker
                DialogFragment newFragment = new DatePickerFragment();
                // Send date type
                Bundle args = new Bundle();
                args.putInt(getString(R.string.date_type),1);
                newFragment.setArguments(args);
                //Show date picker dialog
                newFragment.show(getFragmentManager(), getString(R.string.date_picker));

            }
        });
        // Find reference for to date button
        AppCompatButton toDateButton = (AppCompatButton) findViewById(R.id.to_date_button);
        // Set text for to date button
        toDateButton.setText(TO_DATE);
        // Handle click on to date button
        toDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create dialog fragment for date picker
                DialogFragment newFragment = new DatePickerFragment();
                // Send date type
                Bundle args = new Bundle();
                args.putInt(getString(R.string.date_type),2);
                newFragment.setArguments(args);
                //Show date picker dialog
                newFragment.show(getFragmentManager(), getString(R.string.date_picker));

            }
        });
        // Find reference for refresh button
        AppCompatImageButton refresh = (AppCompatImageButton) findViewById(R.id.refresh_button);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show progressbar, hide list view while loading
                progressBarLayout.setVisibility(View.VISIBLE);
                articleListView.setVisibility(View.GONE);
                //Set start position
                currentPage = 1;
                pageDown.setVisibility(View.INVISIBLE);
                pageUp.setVisibility(View.VISIBLE);
                //Refresh list view
                handleQuery(searchString, FROM_DATE, TO_DATE,currentPage);
            }
        });
        // Load articles from api to list view
        if(checkNetworkConnection()) {
            handleQuery(searchString, FROM_DATE, TO_DATE, currentPage);
            getLoaderManager().initLoader(ARTICLE_LOADER_ID, null, MainActivity.this);
        }else {
            articleListView.setEmptyView(emptyView);
            if (articleAdapter!= null)
            {articleAdapter.clear();}
            progressBarLayout.setVisibility(View.GONE);
            emptyView.setText(R.string.no_connection);
        }
        // Find reference for page down button
        pageDown = (AppCompatImageButton) findViewById(R.id.page_down);
        pageDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show progressbar, hide list view while loading
                progressBarLayout.setVisibility(View.VISIBLE);
                articleListView.setVisibility(View.GONE);
                // Handle min page
                if (currentPage > 1){
                    currentPage--;
                }
                // Handle page down button visibility
                if(currentPage < 2){pageDown.setVisibility(View.INVISIBLE);}
                // Handle page up button visibility
                if(currentPage < pagesCount){pageUp.setVisibility(View.VISIBLE);}
                //Refresh list view
                if(checkNetworkConnection()) {
                    handleQuery(searchString, FROM_DATE, TO_DATE, currentPage);
                }else {
                    articleListView.setEmptyView(emptyView);
                    if (articleAdapter!= null)
                    {articleAdapter.clear();}
                    progressBarLayout.setVisibility(View.GONE);
                    emptyView.setText(R.string.no_connection);
                }
            }
        });
        // Find reference for page up button
        pageUp = (AppCompatImageButton) findViewById(R.id.page_up);
        pageUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show progressbar, hide list view while loading
                progressBarLayout.setVisibility(View.VISIBLE);
                articleListView.setVisibility(View.GONE);
                // Handle max page
                if(currentPage < pagesCount){
                    currentPage++;
                }
                // Handle page down button visibility
                if(currentPage > 1){pageDown.setVisibility(View.VISIBLE);}
                // Handle page up button visibility
                if(currentPage == pagesCount){pageUp.setVisibility(View.INVISIBLE);}
                //Refresh list view
                if(checkNetworkConnection()) {
                    handleQuery(searchString, FROM_DATE, TO_DATE, currentPage);
                }else {
                    if (articleAdapter!= null)
                    {articleAdapter.clear();}
                    progressBarLayout.setVisibility(View.GONE);
                    emptyView.setText(R.string.no_connection);
                }
            }
        });
        // Find reference for show pages text view
        pageText = (AppCompatTextView) findViewById(R.id.pages_text);
        // Set text for pages text view
        pageText.setText(getString(R.string.page) +currentPage + getString(R.string.of) + pagesCount);
        // Find reference for navigation drawer layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Find reference for navigation drawer list view
        final ListView drawerList = (ListView) findViewById(R.id.drawer_list);
        // Find reference for navigation drawer list view header
        View header = getLayoutInflater().inflate(R.layout.nav_header,null);
        // Add header to list view
        drawerList.addHeaderView(header);
        // Set array adapter for navigation drawer list view
        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.drawer_list_item_layout,getResources().getStringArray(R.array.navigation_drawer_list));
        // Populate navigation drawer list view
        drawerList.setAdapter(adapter);
        // Handle navigation drawer list view click
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                switch(position){
                    case 1: searchSection = null;
                        // Close drawer layout
                        drawerLayout.closeDrawer(drawerList);
                        // Show progressbar, hide list view while loading
                        progressBarLayout.setVisibility(View.VISIBLE);
                        articleListView.setVisibility(View.GONE);
                        // Set action bar text
                        if(getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(getString(R.string.fresh_news));
                        }
                        // Set starting positions
                        currentPage = 1;
                        pageDown.setVisibility(View.INVISIBLE);
                        pageUp.setVisibility(View.VISIBLE);
                        // Refresh list view
                        if(checkNetworkConnection()) {
                            handleQuery(searchString, FROM_DATE, TO_DATE, currentPage);
                        }else {
                            articleListView.setEmptyView(emptyView);
                            if (articleAdapter!= null)
                            {articleAdapter.clear();}
                            progressBarLayout.setVisibility(View.GONE);
                            emptyView.setText(getString(R.string.no_connection));
                        }
                        break;
                    // At defined position call handle method
                    case 2:handleNavigationDrawerClick(getString(R.string.world),drawerLayout,drawerList);
                        break;
                    case 3: handleNavigationDrawerClick(getString(R.string.sport),drawerLayout,drawerList);
                        break;
                    case 4: handleNavigationDrawerClick(getString(R.string.football),drawerLayout,drawerList);
                        break;
                    case 5: handleNavigationDrawerClick(getString(R.string.culture),drawerLayout,drawerList);
                        break;
                    case 6: handleNavigationDrawerClick(getString(R.string.business),drawerLayout,drawerList);
                        break;
                    case 7: handleNavigationDrawerClick(getString(R.string.fashion),drawerLayout,drawerList);
                        break;
                    case 8: handleNavigationDrawerClick(getString(R.string.technology),drawerLayout,drawerList);
                        break;
                    case 9: handleNavigationDrawerClick(getString(R.string.travel),drawerLayout,drawerList);
                        break;
                    case 10: handleNavigationDrawerClick(getString(R.string.money),drawerLayout,drawerList);
                        break;
                    case 11: handleNavigationDrawerClick(getString(R.string.science),drawerLayout,drawerList);
                        break;
                }
            }
        });
        // Setup navigation drawer action bar toggle
        setupDrawer();
        // Display button for navigation drawer
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void setupDrawer() {
        // Set toggle button
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            // Called when navigation bar is open
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                searchString = "";
                // Set title text
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.navigation);
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            // Called when navigation bar is closed
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Set title text
                if (getSupportActionBar() != null) {
                    if (searchSection != null) {
                        if(searchString.equals("")){
                            getSupportActionBar().setTitle(getString(R.string.news) + searchSection);
                        }else {
                            getSupportActionBar().setTitle(getString(R.string.news) + searchSection + "/" + searchString);
                        }
                    }else{
                        getSupportActionBar().setTitle(getString(R.string.fresh_news) + searchString);
                    }
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        // Handle toggle button view
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
    }
    // Sync toogle button to navigation drawer
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
    // Handle configuration change (orientation)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    // Create toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate toolbar menu
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        // Find reference to toolbar menu .xml
        final MenuItem item = menu.findItem(R.id.toolbar_search_item);
        // Get search view from toolbar
        final SearchView searchView = (SearchView) item.getActionView();
        // Handle search input
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {

                    // Show loading screen
                    progressBarLayout.setVisibility(View.VISIBLE);
                    articleListView.setVisibility(View.GONE);
                    // Set start position
                    currentPage = 1;
                    pageDown.setVisibility(View.INVISIBLE);
                    pageUp.setVisibility(View.VISIBLE);
                    // Close search field
                    searchView.onActionViewCollapsed();
                    searchString = query;
                    // Set title text
                    if (searchSection == null) {
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(getString(R.string.news_for) + searchString);
                        }
                    } else {
                        if (searchString.equals("")) {
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setTitle(getString(R.string.news) + searchSection);
                            }
                        } else {
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setTitle(getString(R.string.news) + searchSection + "/" + searchString);
                            }
                        }
                    }
                    // Refresh list view
                    if(checkNetworkConnection()) {
                        handleQuery(query, FROM_DATE, TO_DATE, currentPage);
                    }else {
                        articleListView.setEmptyView(emptyView);
                        if (articleAdapter!= null)
                        {articleAdapter.clear();}
                        progressBarLayout.setVisibility(View.GONE);
                        emptyView.setText(getString(R.string.no_connection));
                    }
                return false;
            }
            // Handle search text change
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    // Handle search icon click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
    // Handle query,refresh list view
    private void handleQuery(String searchStringQ, String fromDateQ, String toDateQ, int searchPageQ) {
        // Select URL
        String url;
        if(searchSection == null) {
            url = getString(R.string.base_url) + searchStringQ + getString(R.string.url_tags) + getString(R.string.url_from_date) + fromDateQ + getString(R.string.url_to_date) + toDateQ + getString(R.string.url_page) + searchPageQ + getString(R.string.url_end);
        }else{
            url = getString(R.string.base_url) + searchStringQ + getString(R.string.url_section) + searchSection + getString(R.string.url_from_date) + fromDateQ + getString(R.string.url_to_date) + toDateQ + getString(R.string.url_page) + searchPageQ + getString(R.string.url_end);
        }
        // Restart the loader
        Bundle args = new Bundle();
        args.putString("uri",url);
        getLoaderManager().restartLoader(ARTICLE_LOADER_ID, args, MainActivity.this);
        // Set List view to top
        articleListView.smoothScrollToPosition(0);
    }
    // Create article loader
    @Override
    public Loader<List<BaseArticleData>> onCreateLoader(int id, Bundle args) {
        return new ArticleLoader(MainActivity.this,args);
    }
    // On load finished
    @Override
    public void onLoadFinished(Loader<List<BaseArticleData>> loader, List<BaseArticleData> data) {
        // Clear adapter
        if (articleAdapter != null)
            articleAdapter.clear();
        // If we have a valid list of articles, add them to  the adapter, which will trigger the update of the view
        if (data != null && data.size() > 0) {
            articleAdapter.addAll(data);
        }else{
            emptyView.setText(R.string.no_news_found);
        }
        // Get number of pages
        pagesCount = getPagesCount();
        // Set pages text
        pageText.setText(getString(R.string.page) +currentPage + getString(R.string.of) + pagesCount);
        // Handle one page up down button visibility
        if(pagesCount == 1){
            pageUp.setVisibility(View.INVISIBLE);
            pageDown.setVisibility(View.INVISIBLE);
        }
        if((pagesCount > 1)&&(currentPage != 1)){
            pageDown.setVisibility(View.VISIBLE);
            pageUp.setVisibility(View.VISIBLE);
        }
        if((pagesCount > 1)&&(currentPage == pagesCount)){
            pageDown.setVisibility(View.VISIBLE);
            pageUp.setVisibility(View.INVISIBLE);
        }
        //Show progressbar, hide list view while loading
        progressBarLayout.setVisibility(View.GONE);
        articleListView.setVisibility(View.VISIBLE);

    }
    // When loader reset
    @Override
    public void onLoaderReset(Loader<List<BaseArticleData>> loader) {
        //Reset adapter
        if(articleAdapter != null) {
            articleAdapter.clear();
        }
    }

    protected void handleNavigationDrawerClick(String sString, DrawerLayout dLayout, ListView dList){
        searchSection = sString;
        // Set starting positions
        currentPage = 1;
        pageDown.setVisibility(View.INVISIBLE);
        pageUp.setVisibility(View.VISIBLE);
        // Close navigation drawer
        dLayout.closeDrawer(dList);
        // Show progressbar, hide list view while loading
        progressBarLayout.setVisibility(View.VISIBLE);
        articleListView.setVisibility(View.GONE);
        // Set action bar text
        if (getSupportActionBar() != null) {
            if(searchString.equals("")){
                getSupportActionBar().setTitle(getString(R.string.news) + searchSection);
            }else {
                getSupportActionBar().setTitle(getString(R.string.news) + searchSection + "/" + searchString);
            }
        }
        // Refresh list view
        if(checkNetworkConnection()) {
            handleQuery(searchString, FROM_DATE, TO_DATE, currentPage);
        }else {
            articleListView.setEmptyView(emptyView);
            if (articleAdapter!= null)
            {articleAdapter.clear();}
            progressBarLayout.setVisibility(View.GONE);
            emptyView.setText(getString(R.string.no_connection));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(getString(R.string.from_date),FROM_DATE);
        outState.putString(getString(R.string.to_date),TO_DATE);
        outState.putString(getString(R.string.search_string),searchString);
        outState.putString(getString(R.string.search_section),searchSection);
        outState.putInt(getString(R.string.current_page), currentPage);
    }

    protected boolean checkNetworkConnection(){
        // Check network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
