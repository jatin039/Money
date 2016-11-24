package app.developer.jtsingla.money;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.SystemClock.sleep;
import static app.developer.jtsingla.money.EnterActivity.ISLOGGEDIN;
import static app.developer.jtsingla.money.EnterActivity.LOGINFO;
import static app.developer.jtsingla.money.EnterActivity.NAME;
import static app.developer.jtsingla.money.getUserInfo.log_out_from_method;
import static app.developer.jtsingla.money.getUserInfo.retrieveFirstName;

public class AdActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home_ad);
        setLayoutVisibile((RelativeLayout)findViewById(R.id.home_ad));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(this, EnterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        String name = retrieveFirstName(getSharedPreferences(LOGINFO, MODE_PRIVATE)
                .getString(EnterActivity.NAME, ""));
        if (name.equals("user")) name = "";
        String title = getString(R.string.title_activity_ad_fmt, name);
        Log.i("On create options name", name);
        setTitle(title);
        setLayout();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home_ad) {
            setHomeLayout();
            setLayoutVisibile((RelativeLayout)findViewById(R.id.home_ad));
        } else if (id == R.id.nav_images_ad) {
            setImagesLayout();
            setLayoutVisibile((RelativeLayout)findViewById(R.id.images_ad));
        } else if (id == R.id.nav_videos_ad) {
            setVideosLayout();
            setLayoutVisibile((RelativeLayout)findViewById(R.id.videos_ad));
        } else if (id == R.id.nav_bank_details_ad) {
            setBankDetailsLayout();
            setLayoutVisibile((RelativeLayout)findViewById(R.id.bank_details_ad));
        } else if (id == R.id.nav_log_out_ad) {
            setLogOutLayout();
            setLayoutVisibile((RelativeLayout)findViewById(R.id.log_out_ad));
        } else if (id == R.id.nav_rate_ad) {
            setRateLayout();
            setLayoutVisibile((RelativeLayout)findViewById(R.id.rate_ad));
        } else if (id == R.id.nav_feedback_ad) {
            setFeedbackLayout();
            setLayoutVisibile((RelativeLayout)findViewById(R.id.feedback_ad));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void image_action_ad(View v) {
        setLayoutVisibile((RelativeLayout)findViewById(R.id.images_ad));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_images_ad);
    }

    public void video_action_ad(View v) {
        setLayoutVisibile((RelativeLayout)findViewById(R.id.videos_ad));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_videos_ad);
    }

    private void setLayout() {
        SharedPreferences prefs = getSharedPreferences(LOGINFO, MODE_PRIVATE);
        String firstName = retrieveFirstName(prefs.getString(EnterActivity.NAME, "user "));
        String userId = prefs.getString(EnterActivity.USERID, "username");

        // set userDetails
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.user_details_ad);
        TextView tv = (TextView) linearLayout.getChildAt(0);

        // in shared preference NAME & USERNAME must be valid as the user is logged in,
        // there should never be the case that these fields are invalid when user is in AdActivity.
        tv.setText("Hello " + firstName + "!");

        tv = (TextView) linearLayout.getChildAt(1);
        tv.setText(userId);

        // set earningsDetails
        linearLayout = (LinearLayout) findViewById(R.id.earning_details_ad);
        tv = (TextView) linearLayout.getChildAt(1);
        tv.setText("Rs. 0"); // FixMe
        // tv.setText(getTotalEarningsOfUser(userId));

    }

    private void setAllLayoutAsNotVisible() {
        RelativeLayout rr = (RelativeLayout) findViewById(R.id.home_ad);
        rr.setVisibility(View.GONE);
        rr = (RelativeLayout) findViewById(R.id.images_ad);
        rr.setVisibility(View.GONE);
        rr = (RelativeLayout) findViewById(R.id.videos_ad);
        rr.setVisibility(View.GONE);
        rr = (RelativeLayout) findViewById(R.id.bank_details_ad);
        rr.setVisibility(View.GONE);
        rr = (RelativeLayout) findViewById(R.id.log_out_ad);
        rr.setVisibility(View.GONE);
        rr = (RelativeLayout) findViewById(R.id.rate_ad);
        rr.setVisibility(View.GONE);
        rr = (RelativeLayout) findViewById(R.id.feedback_ad);
        rr.setVisibility(View.GONE);
    }

    private void setLayoutVisibile(RelativeLayout relativeLayout) {
        setAllLayoutAsNotVisible();
        relativeLayout.setVisibility(View.VISIBLE);
    }

    public void log_out_confirm(View v) {
        // ask the user for log out confirmation
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        log_out();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_delete)
                .show();
    }

    private void log_out() {
        SharedPreferences prefs = getSharedPreferences(LOGINFO, MODE_PRIVATE);
        log_out_from_method(prefs);
        // Todo : put the pending balance to DB. (images + videos)
        // show log out!
        Toast.makeText(this, "You have been logged out!",
                Toast.LENGTH_LONG).show();
        this.onBackPressed();
    }

    private void setHomeLayout() {
        setActionBarTitle(getString(R.string.title_activity_ad_fmt,
                retrieveFirstName(getSharedPreferences(LOGINFO, MODE_PRIVATE).getString(EnterActivity.NAME, "user"))));
    }

    private void setImagesLayout() {
        setActionBarTitle(getString(R.string.title_activity_ad_fmt,
                retrieveFirstName(getSharedPreferences(LOGINFO, MODE_PRIVATE).getString(EnterActivity.NAME, "user"))));
    }

    private void setVideosLayout() {
        setActionBarTitle(getString(R.string.title_activity_ad_fmt,
                retrieveFirstName(getSharedPreferences(LOGINFO, MODE_PRIVATE).getString(EnterActivity.NAME, "user"))));
    }

    private void setBankDetailsLayout() {
        setActionBarTitle(getString(R.string.title_activity_ad_fmt,
                retrieveFirstName(getSharedPreferences(LOGINFO, MODE_PRIVATE).getString(EnterActivity.NAME, "user"))));
    }

    private void setLogOutLayout() {
        setActionBarTitle(getString(R.string.title_log_out_ad));
        LinearLayout ll = (LinearLayout) findViewById(R.id.log_out_ll_ad);
        TextView tv = (TextView) ll.getChildAt(0);
        SharedPreferences prefs = getSharedPreferences(LOGINFO, MODE_PRIVATE);
        String logOutString = getString(R.string.logged_in_enter,
                retrieveFirstName(prefs.getString(NAME, "user")));
        tv.setText(logOutString);
    }

    private void setRateLayout() {
        setActionBarTitle(getString(R.string.title_activity_ad_fmt,
            retrieveFirstName(getSharedPreferences(LOGINFO, MODE_PRIVATE).getString(EnterActivity.NAME, "user"))));
    }

    private void setFeedbackLayout() {
        setActionBarTitle(getString(R.string.title_activity_ad_fmt,
                retrieveFirstName(getSharedPreferences(LOGINFO, MODE_PRIVATE).getString(EnterActivity.NAME, "user"))));
    }

    private void setActionBarTitle(String title) {
        setTitle(title);
    }
}
