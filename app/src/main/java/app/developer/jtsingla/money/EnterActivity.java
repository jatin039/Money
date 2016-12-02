package app.developer.jtsingla.money;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import static app.developer.jtsingla.money.getUserInfo.log_out_from_method;

public class EnterActivity extends AppCompatActivity {

    /* we need these fields to maintain whether the user is already logged in
       If we dont save this information locally we will have to ask user
       to log in every time he opens the application.
     */
    public static FirebaseUser loggedInUser = null;
    public static final String LOGINFO = "userLogInfo";
    public static final String ISLOGGEDIN = "isLoggedIn";
    public static final String USERID = "userId";
    public static final String NAME = "name";
    public static final String LOGGEDINVIA = "logInVia";
    public static final String VIDEOS = "videos";
    public static final String IMAGES = "images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        SharedPreferences prefs = getSharedPreferences(LOGINFO, MODE_PRIVATE);
        Boolean isLoggedIn = prefs.getBoolean(ISLOGGEDIN, false);

        if (isLoggedIn.equals(false)) {
            //either app is opened very first time or the user has manually signed out.
            // we need to put the entry if it equals false because it might not exist is prefs.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(ISLOGGEDIN, false);
            editor.commit();
            // set the layout according to the logged in value

        }

        changeDisplay(isLoggedIn);

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences(LOGINFO, MODE_PRIVATE);
        Boolean isLoggedIn = prefs.getBoolean(ISLOGGEDIN, false);
        changeDisplay(isLoggedIn);
    }

    private void changeDisplay(boolean isLoggedIn) {
        changeButtonsVisibility(isLoggedIn);
        changeText(isLoggedIn);
    }

    private void changeText(boolean isLoggedIn) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.login_info_enter);
        if (isLoggedIn) {
            // if logged in, we should show the logged in name, and proper instruction
            TextView tv = (TextView) linearLayout.getChildAt(0);
            String greet = getString(R.string.logged_in_enter,
                    getSharedPreferences(LOGINFO, MODE_PRIVATE).getString(NAME, "user"));
            tv.setText(greet);

            tv = (TextView) linearLayout.getChildAt(1);
            tv.setText(R.string.logged_in_sub_enter);
        } else {
            // if not logged in, we should ask the user to login
            TextView tv = (TextView) linearLayout.getChildAt(0);
            tv.setText(R.string.not_logged_in_enter);

            tv = (TextView) linearLayout.getChildAt(1);
            tv.setText(R.string.not_logged_in_sub_enter);
        }
    }

    private void changeButtonsVisibility(boolean isLoggedIn) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.buttons_enter);
        if (isLoggedIn) {
            // we need to set the visibility of 0th and 1st child as gone,
            // As user is already signed in, we will just show the option to sign in.
            setVisibility((Button)((LinearLayout) findViewById(R.id.buttons_enter)).getChildAt(2), true);
            setVisibility((Button)((LinearLayout) findViewById(R.id.buttons_enter)).getChildAt(0), false);
            setVisibility((Button)((LinearLayout) findViewById(R.id.buttons_enter)).getChildAt(1), false);
            setVisibility((Button)((LinearLayout) findViewById(R.id.buttons_enter)).getChildAt(3), true);
        } else {
            // we need to set the visibility of 0th and 1st as visible,
            // As user is not signed in, we will show the option to either sign up or log in.
            setVisibility((Button)((LinearLayout) findViewById(R.id.buttons_enter)).getChildAt(2), false);
            setVisibility((Button)((LinearLayout) findViewById(R.id.buttons_enter)).getChildAt(0), true);
            setVisibility((Button)((LinearLayout) findViewById(R.id.buttons_enter)).getChildAt(1), true);
            setVisibility((Button)((LinearLayout) findViewById(R.id.buttons_enter)).getChildAt(3), false);
        }
    }

    private void setVisibility(Button button, boolean set) {
        if (set) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    public void sign_up(View v) {
        // if not logged in, open signUpActivity
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void log_in(View v) {
        // if not logged in, open logInActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void start_earning(View v) {
        // if logged in, get user data from db, and open AdActivity
        // get user data

        //start AdActivity
        Intent intent = new Intent(this, AdActivity.class);
        startActivity(intent);
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
        changeDisplay(prefs.getBoolean(ISLOGGEDIN, false));
        // Todo : put the pending balance to DB. (images + videos)
        // show log out!
        Toast.makeText(this, "You have been logged out!",
                Toast.LENGTH_SHORT).show();
    }
}
