package app.developer.jtsingla.money;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ButtonBarLayout;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import static app.developer.jtsingla.money.EnterActivity.LOGINFO;
import static app.developer.jtsingla.money.EnterActivity.NAME;
import static app.developer.jtsingla.money.EnterActivity.USERDB;
import static app.developer.jtsingla.money.EnterActivity.loggedInUser;
import static app.developer.jtsingla.money.FireBaseAccess.getLoggedInUserDb;
import static app.developer.jtsingla.money.FireBaseAccess.updateBankDetailsInDb;
import static app.developer.jtsingla.money.getUserInfo.log_out_from_method;
import static app.developer.jtsingla.money.getUserInfo.retrieveFirstName;

interface DialogButtonListener {
    void onClick(boolean clicked);
}

public class AdActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private UserDb userDb;

    private UserDb retrieveUserDbFromPreferences() {
        SharedPreferences prefs = getSharedPreferences(LOGINFO, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(USERDB, "");
        UserDb userDb = gson.fromJson(json, UserDb.class);
        return userDb;
    }

    private void saveUserDbToPreferences(UserDb userDb) {
        SharedPreferences.Editor editor = getSharedPreferences(LOGINFO, MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(userDb);
        editor.putString(USERDB, json);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        MobileAds.initialize(getApplicationContext(), getString(R.string.user_ad_id1));
        MobileAds.initialize(getApplicationContext(), getString(R.string.user_ad_id2));
        MobileAds.initialize(getApplicationContext(), getString(R.string.user_ad_id3));
        MobileAds.initialize(getApplicationContext(), getString(R.string.user_ad_id4));
        MobileAds.initialize(getApplicationContext(), getString(R.string.user_ad_id5));
        MobileAds.initialize(getApplicationContext(), getString(R.string.user_ad_id6));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /* get user from DB, only works if there is some change in DB */
        // saving the user in sharedprefs, to get when there is no change.
        if (loggedInUser == null) {
            loggedInUser = FirebaseAuth.getInstance().getCurrentUser();
        }
        /* first try getting the user from DB, for any updates or first time */
        userDb = getLoggedInUserDb(loggedInUser);
        if (userDb != null) {
            Log.i("adActivity on create", "got user from firebase listener");
            /* save the updated user to sharedprefs */
            saveUserDbToPreferences(userDb);
        } else {
            /* no update in db for this user, then get the old user from shared prefs */
            userDb = retrieveUserDbFromPreferences();
            Log.i("adActivity on create", "getting user from shared prefs");
        }
        if (userDb == null) {
            /* if it is still null, can't do much now :(*/
            Log.e("adActivity on create", "user was null in sharedprefs and DB. :(");
            Toast.makeText(AdActivity.this, "Some error Occured, Try Again", Toast.LENGTH_SHORT).show();
            this.onBackPressed();
            return;
        }

        if (userDb.getBankDetail() != null && userDb.getBankDetail().isValid()) {
            navigationView.setCheckedItem(R.id.nav_home_ad);
            setLayoutVisibile((RelativeLayout) findViewById(R.id.home_ad));
        } else {
            navigationView.setCheckedItem(R.id.nav_bank_details_ad);
            setLayoutVisibile((RelativeLayout) findViewById(R.id.bank_details_ad));
            setBankDetailsLayout();
        }

        FloatingActionButton image_button = (FloatingActionButton) findViewById(R.id.image_button_ad);
        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_action_ad();
            }
        });

        FloatingActionButton video_button = (FloatingActionButton) findViewById(R.id.video_button_ad);
        video_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                video_action_ad();
            }
        });
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
            image_action_ad();
        } else if (id == R.id.nav_videos_ad) {
            setVideosLayout();
            video_action_ad();
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

    public void image_action_ad() {
        setLayoutVisibile((RelativeLayout)findViewById(R.id.images_ad));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_images_ad);
        AdView mAdView1 = (AdView) findViewById(R.id.adView1);
        AdView mAdView2 = (AdView) findViewById(R.id.adView2);
        AdView mAdView3 = (AdView) findViewById(R.id.adView3);
        //AdRequest adRequest = new AdRequest.Builder().build();
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("D1FA4E8324D2D834243EB8F4A76B87A3")
                .build();
        mAdView1.loadAd(adRequest);
        mAdView2.loadAd(adRequest);
        mAdView3.loadAd(adRequest);
    }

    public void video_action_ad() {
        setLayoutVisibile((RelativeLayout)findViewById(R.id.videos_ad));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_videos_ad);
        AdView mAdView1 = (AdView) findViewById(R.id.adView4);
        AdView mAdView2 = (AdView) findViewById(R.id.adView5);
        AdView mAdView3 = (AdView) findViewById(R.id.adView6);
        //AdRequest adRequest = new AdRequest.Builder().build();
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("D1FA4E8324D2D834243EB8F4A76B87A3")
                .build();
        mAdView1.loadAd(adRequest);
        mAdView2.loadAd(adRequest);
        mAdView3.loadAd(adRequest);
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
        setActionBarTitle("Bank Details");
        //TODO: get from DB that whether bank details are valid or not
        boolean isValid = userDb.getBankDetail() != null ? userDb.getBankDetail().isValid() : false;
        Button button = getBankLayoutButton();

        // if details are not valid, then we should set the text of button as Save.
        if (!isValid) {
            button.setText("Save");
        } else {
            displayBankLayout();
        }
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

    private void ask_the_user_to_confirm(String title, String query, final DialogButtonListener listener) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(query)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClick(true);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClick(false);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private AutoCompleteTextView getBankLayoutTextView(@IdRes int id) {
        return (AutoCompleteTextView) findViewById(id);
    }

    private void disable_bank_details_fields(boolean disable) {
        AutoCompleteTextView textView = getBankLayoutTextView(R.id.bank_name_ad);
        textView.setEnabled(!disable);
        textView.setFocusable(!disable);
        textView.setFocusableInTouchMode(!disable);

        textView = getBankLayoutTextView(R.id.bank_account_holder_name_ad);
        textView.setEnabled(!disable);
        textView.setFocusable(!disable);
        textView.setFocusableInTouchMode(!disable);

        textView = getBankLayoutTextView(R.id.bank_account_no_ad);
        textView.setEnabled(!disable);
        textView.setFocusable(!disable);
        textView.setFocusableInTouchMode(!disable);

        textView = getBankLayoutTextView(R.id.bank_ifsc_ad);
        textView.setEnabled(!disable);
        textView.setFocusable(!disable);
        textView.setFocusableInTouchMode(!disable);

        textView = getBankLayoutTextView(R.id.bank_country_ad);
        textView.setEnabled(!disable);
        textView.setFocusable(!disable);
        textView.setFocusableInTouchMode(!disable);
    }

    private void updateBankDetails() {
        BankDetail bankDetail = new BankDetail();
        bankDetail.setValid(true);

        AutoCompleteTextView textView = getBankLayoutTextView(R.id.bank_name_ad);
        bankDetail.setBankName(textView.getText().toString());

        textView = getBankLayoutTextView(R.id.bank_account_holder_name_ad);
        bankDetail.setBankAccountHolderName(textView.getText().toString());

        textView = getBankLayoutTextView(R.id.bank_account_no_ad);
        bankDetail.setBankAccountNo(textView.getText().toString());

        textView = getBankLayoutTextView(R.id.bank_ifsc_ad);
        bankDetail.setIFSC(textView.getText().toString());

        textView = getBankLayoutTextView(R.id.bank_country_ad);
        bankDetail.setBankCountry(textView.getText().toString());

        userDb.setBankDetail(bankDetail);
        updateBankDetailsInDb(loggedInUser.getUid(), bankDetail);
    }

    private void displayBankLayout() {
        // this API will display the bank layout after disabling the fields and making the button
        // as Edit.
        AutoCompleteTextView textView = getBankLayoutTextView(R.id.bank_name_ad);
        textView.setText(userDb.getBankDetail().getBankName());

        textView = getBankLayoutTextView(R.id.bank_account_holder_name_ad);
        textView.setText(userDb.getBankDetail().getBankAccountHolderName());


        textView = getBankLayoutTextView(R.id.bank_account_no_ad);
        textView.setText(userDb.getBankDetail().getBankAccountNo());

        textView = getBankLayoutTextView(R.id.bank_ifsc_ad);
        textView.setText(userDb.getBankDetail().getIFSC());

        textView = getBankLayoutTextView(R.id.bank_country_ad);
        textView.setText(userDb.getBankDetail().getBankCountry());

        //disable the fields
        disable_bank_details_fields(true);
        // marking the button text as Edit
        setBankLayoutButtonText("Edit");
    }

    private void setBankLayoutButtonText(String text) {
        Button button = getBankLayoutButton();
        button.setText(text);
    }

    private Button getBankLayoutButton() {
        ButtonBarLayout layout = (ButtonBarLayout) findViewById(R.id.bank_details_buttons_ad);
        return (Button) layout.getChildAt(0);
    }

    private boolean checkBankDetailsValid() {
        // check if bank name is valid ?
        String Error = "This is required field.";
        boolean flag = false;
        AutoCompleteTextView textView = getBankLayoutTextView(R.id.bank_name_ad);
        if (textView.getText() == null || textView.getText().toString().isEmpty()) {
            textView.setError(Error);
            textView.requestFocus();
            flag = true;
        }

        textView = getBankLayoutTextView(R.id.bank_account_holder_name_ad);
        if (textView.getText() == null || textView.getText().toString().isEmpty()) {
            textView.setError(Error);
            textView.requestFocus();
            flag = true;
        }

        textView = getBankLayoutTextView(R.id.bank_account_no_ad);
        if (textView.getText() == null || textView.getText().toString().isEmpty()) {
            textView.setError(Error);
            textView.requestFocus();
            flag = true;
        }

        textView = getBankLayoutTextView(R.id.bank_ifsc_ad);
        if (textView.getText() == null || textView.getText().toString().isEmpty()) {
            textView.setError(Error);
            textView.requestFocus();
            flag = true;
        }

        textView = getBankLayoutTextView(R.id.bank_country_ad);
        if (textView.getText() == null || textView.getText().toString().isEmpty()) {
            textView.setError(Error);
            textView.requestFocus();
            flag = true;
        }

        if (flag) return false;
        return true;
    }

    /* on Click Buttons */
    public void save_bank_details(View v) {
        // check if button is set as Edit or Save.
        Button button = getBankLayoutButton();
        String buttonText = button.getText().toString();

        // Save Operation.
        if (buttonText != null && buttonText.equals("Save")) {
            // button text is set as Save, we should just check all fields and proceed with save.
            // check if details are valid, otherwise ask the user to put required fields
            boolean isValid = checkBankDetailsValid();
            if (isValid) {
                this.ask_the_user_to_confirm("Save Bank Details",
                        "Do you really want to save the bank details?",
                        new DialogButtonListener() {
                            @Override
                            public void onClick(boolean clicked) {
                                if (clicked) {
                                    // user Agreed
                                    updateBankDetails();
                                    displayBankLayout();
                                } else {
                                    // show old data.
                                    displayBankLayout();
                                }
                            }
                        });
            }
        }
        // Edit Operation.
        else if (buttonText != null && buttonText.equals("Edit")) {
            // button text is set as Edit, we should ask the user if he really wants to edit the details
            this.ask_the_user_to_confirm("Edit Bank Details",
                    "Do you really want to edit the bank details?",
                    new DialogButtonListener() {
                        @Override
                        public void onClick(boolean clicked) {
                            if (clicked) {
                                //user Agreed
                                disable_bank_details_fields(false);
                                // change the button text to save.
                                setBankLayoutButtonText("Save");
                            } else {
                                // do nothing
                                displayBankLayout();
                            }
                        }
                    });
        }
    }
}
