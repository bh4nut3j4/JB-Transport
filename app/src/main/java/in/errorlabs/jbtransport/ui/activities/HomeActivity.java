package in.errorlabs.jbtransport.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

import net.steamcrafted.loadtoast.LoadToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.fragments.HomeDataFragment;
import in.errorlabs.jbtransport.ui.fragments.HomeMapFragment;
import in.errorlabs.jbtransport.ui.fragments.NoticeFragment;
import in.errorlabs.jbtransport.utils.Connection;
import in.errorlabs.jbtransport.utils.SharedPrefs;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "HomeActivity";
    SharedPrefs sharedPrefs;
    LoadToast loadToast;
    Connection connection;
    public static final int DETAILS_LOADER_ID = 11;
    public static final int COORDINATES_LOADER_ID = 12;
    NavigationView navigationView;
    Menu menu;
    @BindView(R.id.relative_lay)RelativeLayout relativeLayout;
    @BindView(R.id.notice_card_view)CardView noticecard;
    @BindView(R.id.noticebard_text)TextView notice_text;
    @BindView(R.id.notice_last_updated_text)TextView notice_timestamp;
    HomeDataFragment homeDataFragment;
    HomeMapFragment homeMapFragment;
    NoticeFragment noticeFragment;
    public static final String HOMEDATA_FRAG = "homedata";
    public static final String MAP_FRAG = "mapdata";
    public static final String NOTICE_FRAG = "noticedata";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setupWindowAnimations();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        sharedPrefs = new SharedPrefs(this);
        loadToast = new LoadToast(this);
        connection = new Connection(this);
        if (savedInstanceState==null){
            startMainActivity();
        }else {
            noticeFragment = (NoticeFragment) getSupportFragmentManager().getFragment(savedInstanceState,NOTICE_FRAG);
            homeDataFragment = (HomeDataFragment) getSupportFragmentManager().getFragment(savedInstanceState,HOMEDATA_FRAG);
            homeMapFragment = (HomeMapFragment) getSupportFragmentManager().getFragment(savedInstanceState,MAP_FRAG);
        }
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPopUp();
            }
        });
    }

    private void setupWindowAnimations() {
        Transition fade = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            fade = TransitionInflater.from(this).inflateTransition(R.transition.fadein);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(fade);
        }
    }

    public void searchPopUp(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.searchtitle);
        alert.setMessage(R.string.searchmessage);
        alert.setIcon(R.drawable.searchalert);
        final EditText input = new EditText(this);
        input.setHint(R.string.searchhint);
        alert.setView(input);
        alert.setPositiveButton(getString(R.string.search), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String result = input.getText().toString();
                if (result.length()>0){
                    Intent intent = new Intent(getApplicationContext(),AllRoutes.class);
                    intent.putExtra(getString(R.string.AreaName),result);
                    startActivity(intent);
                }else {
                    Snackbar.make(relativeLayout,getString(R.string.invalidinput),Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        alert.setNegativeButton(getString(R.string.cancel),null);
        alert.show();
    }


    public void startMainActivity() {
        if (sharedPrefs.getRouteSelected()) {
             if (FirebaseInstanceId.getInstance().getToken() != null) {
                 FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.jb_public_push));
                FirebaseMessaging.getInstance().subscribeToTopic(sharedPrefs.getSelectedRouteFcmID());
            }
            getSelectedRouteDetails(sharedPrefs.getSelectedRouteNumber());
        } else {
            showError();
        }
    }

    public void getSelectedRouteDetails(String routeNumber) {
        homeDataFragment = new HomeDataFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        homeDataFragment.setRouteNumber(routeNumber);
        fragmentManager.beginTransaction().replace(R.id.main_data_fragment,homeDataFragment).commit();

        homeMapFragment = new HomeMapFragment();
        homeMapFragment.setRouteNumber(routeNumber);
        fragmentManager.beginTransaction().replace(R.id.main_map_fragment,homeMapFragment).commit();

        noticeFragment = new NoticeFragment();
        fragmentManager.beginTransaction().replace(R.id.notice_frag,noticeFragment).commit();
    }

    public void showError() {
        loadToast.error();
        Snackbar.make(relativeLayout, getString(R.string.tryagainlater), Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connection.isInternet()) {
                    startMainActivity();
                }
            }
        }).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.reset) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.warning);
            alert.setMessage(R.string.resetwarning);
            alert.setIcon(R.drawable.warning);
            alert.setPositiveButton(getString(R.string.proceed), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(sharedPrefs.getSelectedRouteFcmID());
                    sharedPrefs.setSelectedRouteNumber(null);
                    sharedPrefs.setSelectedRouteFcmID(null);
                    sharedPrefs.setRouteSelectedAsFalse();
                    startActivity(new Intent(getApplicationContext(),Splash.class));
                    finish();
                }
            });
            alert.setNegativeButton(getString(R.string.cancel),null);
            alert.show();
            return true;
        }else if(id == R.id.refresh) {
            startMainActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_all_routes) {
            startActivity(new Intent(getApplicationContext(),AllRoutes.class));
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        } else if (id == R.id.nav_complaints) {
            startActivity(new Intent(getApplicationContext(),Scanner.class));
        } else if (id == R.id.nav_notifications) {
            startActivity(new Intent(getApplicationContext(),Notifications.class));
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        } else if (id == R.id.nav_sos) {
            startActivity(new Intent(getApplicationContext(), Sos.class));
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        } else if (id == R.id.nav_collegemap) {
            startActivity(new Intent(getApplicationContext(), MapViewActivity.class));
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        } else if (id == R.id.nav_aboutus) {

            AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
            alert.setTitle(String.format("%1$s", getString(R.string.aboutus)));
            alert.setMessage(getResources().getText(R.string.contributers));
            alert.setIcon(R.drawable.aboutus);
            alert.setPositiveButton(R.string.OK,null);
            AlertDialog welcomeAlert = alert.create();
            welcomeAlert.show();
            ((TextView) welcomeAlert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

        } else if (id == R.id.nav_reportabug) {
            Intent i = new Intent(Intent.ACTION_SENDTO);
            String uriText = getString(R.string.mailto) + Uri.encode(getString(R.string.bhanuteja_r07email)) + getString(R.string.subjectemailqstn) +
                    Uri.encode(getString(R.string.reportingabug)) + getString(R.string.bodymail) + Uri.encode(getString(R.string.mailintro));
            Uri uri = Uri.parse(uriText);
            i.setData(uri);
            startActivity(Intent.createChooser(i, getString(R.string.sendemail)));
        } else if (id == R.id.nav_opensourcelicenses) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle(String.format("%1$s", getString(R.string.opensource) + ":"));
            builder.setMessage(getResources().getText(R.string.licenses_text));
            builder.setPositiveButton("OK", null);
            builder.setIcon(R.drawable.license);
            AlertDialog welcomeAlert = builder.create();
            welcomeAlert.show();
            ((TextView) welcomeAlert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState,NOTICE_FRAG,noticeFragment);
        getSupportFragmentManager().putFragment(outState,HOMEDATA_FRAG,homeDataFragment);
        getSupportFragmentManager().putFragment(outState,MAP_FRAG,homeMapFragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState!=null){
            noticeFragment = (NoticeFragment) getSupportFragmentManager().getFragment(savedInstanceState,NOTICE_FRAG);
            homeDataFragment = (HomeDataFragment) getSupportFragmentManager().getFragment(savedInstanceState,HOMEDATA_FRAG);
            homeMapFragment = (HomeMapFragment) getSupportFragmentManager().getFragment(savedInstanceState,MAP_FRAG);
        }
    }

}
