package app.developer.jtsingla.money;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EnterActivity extends AppCompatActivity {

    /* we need these fields to maintain whether the user is already logged in
       If we dont save this information locally we will have to ask user
       to log in every time he opens the application.
     */
    private final String LOGINFO = "userLogInfo";
    private final String ISLOGGEDIN = "isLoggedIn";
    private final String USERID = "userId";
    private final String NAME = "name";

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
        } else {
            // we need to set the visibility of 0th and 1st as visible,
            // As user is not signed in, we will show the option to either sign up or log in.
            setVisibility((Button)((LinearLayout) findViewById(R.id.buttons_enter)).getChildAt(2), false);
            setVisibility((Button)((LinearLayout) findViewById(R.id.buttons_enter)).getChildAt(0), true);
            setVisibility((Button)((LinearLayout) findViewById(R.id.buttons_enter)).getChildAt(1), true);
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
}
