package app.developer.jtsingla.money;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
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

import org.w3c.dom.Text;

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
        String title = getString(R.string.title_activity_ad_fmt,
                retrieveFirstName(getSharedPreferences(EnterActivity.LOGINFO, MODE_PRIVATE).getString(EnterActivity.NAME, "user")));
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
            setLayoutVisibile((RelativeLayout)findViewById(R.id.home_ad));
        } else if (id == R.id.nav_images_ad) {
            setLayoutVisibile((RelativeLayout)findViewById(R.id.images_ad));
        } else if (id == R.id.nav_videos_ad) {
            setLayoutVisibile((RelativeLayout)findViewById(R.id.videos_ad));
        } else if (id == R.id.nav_bank_details_ad) {
            setLayoutVisibile((RelativeLayout)findViewById(R.id.bank_details_ad));
        } else if (id == R.id.nav_log_out_ad) {
            setLayoutVisibile((RelativeLayout)findViewById(R.id.log_out_ad));
        } else if (id == R.id.nav_rate_ad) {
            setLayoutVisibile((RelativeLayout)findViewById(R.id.rate_ad));
        } else if (id == R.id.nav_feedback_ad) {
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
        SharedPreferences prefs = getSharedPreferences(EnterActivity.LOGINFO, MODE_PRIVATE);
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
}
