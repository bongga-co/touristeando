package co.bongga.touristeando.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import co.bongga.touristeando.R;
import co.bongga.touristeando.controllers.NotificationsPresenter;
import co.bongga.touristeando.fragments.HomeFragment;
import co.bongga.touristeando.fragments.NotificationFragment;
import co.bongga.touristeando.fragments.ProfileFragment;
import co.bongga.touristeando.utils.CircleTransform;
import co.bongga.touristeando.utils.Constants;
import co.bongga.touristeando.utils.PreferencesManager;
import co.bongga.touristeando.utils.UtilityManager;

public class Main extends AppCompatActivity {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Handler handler;
    private Toolbar toolbar;

    private static int navItemIndex = 0;
    private static final String TAG_HOME = "home";
    private static final String TAG_PROFILE = "profile";
    private static final String TAG_OFFER = "offer";
    private static String CURRENT_TAG = TAG_HOME;

    private String[] activityTitles;

    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private PreferencesManager preferencesManager;

    NotificationsPresenter notificationsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferencesManager = new PreferencesManager(this);

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_header_profile);

        handler = new Handler();

        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;

            Bundle extras = getIntent().getExtras();
            if(extras != null){
                boolean hasNotification = extras.getBoolean("hasNotification");
                if(hasNotification){
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_OFFER;
                }
            }

            loadHomeFragment();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        if(extras != null){
            boolean hasNotification = extras.getBoolean("hasNotification");
            if(hasNotification){
                navItemIndex = 2;
                CURRENT_TAG = TAG_OFFER;
            }

            loadHomeFragment();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            shareApp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_CHECK_SETTINGS){
            HomeFragment fragment = getHomeFragment();
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == Constants.REQUEST_COARSE_LOCATION_PERMISSION ||
                requestCode == Constants.REQUEST_FINE_LOCATION_PERMISSION ||
                requestCode == Constants.REQUEST_RECORD_PERMISSION){
            HomeFragment fragment = getHomeFragment();
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private HomeFragment getHomeFragment(){
        HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_HOME);
        return fragment;
    }

    private void loadNavHeader(){
        String urlNavHeaderBg = Constants.DEFAULT_HEADER_IMAGE;
        String urlProfileImg = Constants.DEFAULT_PROFILE_IMAGE;

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
            .crossFade()
            .placeholder(R.drawable.nav_menu_header_bg)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
            .crossFade()
            .thumbnail(0.5f)
            .bitmapTransform(new CircleTransform(this))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.nav_placeholder_profile)
            .into(imgProfile);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
            //Check to see which item was being clicked and perform appropriate action
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;

                    setCheckMenuItem(menuItem);
                    loadHomeFragment();
                    break;
                case R.id.nav_profile:
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_PROFILE;

                    setCheckMenuItem(menuItem);
                    loadHomeFragment();
                    break;
                case R.id.nav_offer:
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_OFFER;

                    setCheckMenuItem(menuItem);
                    loadHomeFragment();
                    break;
                case R.id.nav_setting:
                    startActivity(new Intent(Main.this, Settings.class));
                    setCloseDrawer();
                    break;
                case R.id.nav_feedback:
                    startActivity(new Intent(Main.this, Feedback.class));
                    setCloseDrawer();
                    break;
                case R.id.nav_terms:
                    startActivity(new Intent(Main.this, Terms.class));
                    setCloseDrawer();
                    break;
                default:
                    navItemIndex = 0;
            }

            return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                UtilityManager.hideKeyboard(Main.this);
            }
        };

        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void setCloseDrawer(){
        drawer.closeDrawers();
        invalidateOptionsMenu();
    }

    private void setCheckMenuItem(MenuItem menuItem){
        //Checking if the item is in checked state or not, if not make it in checked state
        if (menuItem.isChecked()) {
            menuItem.setChecked(false);
        }
        else {
            menuItem.setChecked(true);
        }
        menuItem.setChecked(true);
    }

    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();
        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            // show or hide the fab button
            //toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = setCurrentFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.content_main, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            handler.post(mPendingRunnable);
        }

        // show or hide the fab button
        //toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private Fragment setCurrentFragment(){
        switch (navItemIndex) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;
            case 2:
                NotificationFragment notificationFragment = new NotificationFragment();
                notificationsPresenter = new NotificationsPresenter(
                        notificationFragment, FirebaseMessaging.getInstance());
                return notificationFragment;
            default:
                return new HomeFragment();
        }
    }

    private void shareApp(){
        Intent send = new Intent(Intent.ACTION_SEND);
        send.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text));
        send.setType("text/plain");

        startActivity(Intent.createChooser(send, getResources().getString(R.string.share_chooser_title)));
    }
}