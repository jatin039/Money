package app.developer.jtsingla.money;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import static android.content.Context.MODE_PRIVATE;
import static app.developer.jtsingla.money.EnterActivity.ISLOGGEDIN;
import static app.developer.jtsingla.money.EnterActivity.LOGINFO;
import static app.developer.jtsingla.money.EnterActivity.NAME;
import static app.developer.jtsingla.money.EnterActivity.USERID;

/**
 * Created by jssingla on 11/19/16.
 */

public class getUserInfo {
    public enum logInMethod {
        Google("google"),
        Facebook("facebook");
        private String method;

        logInMethod(String value) {
            this.method = value;
        }

        public String getMethod() {
            return method;
        }
    }

    public static void startAdActivity(Context context, logInMethod method, Object object) {
        SharedPreferences prefs = context.getSharedPreferences(EnterActivity.LOGINFO, MODE_PRIVATE);
        if (method.getMethod().equals(logInMethod.Google.getMethod())) {
            // get info from google sign in
            Log.i("start activity", "google");
            getUserIdFromGoogleSignIn((GoogleSignInResult)object, prefs);
        } else if (method.getMethod().equals(logInMethod.Facebook.getMethod())) {
            // get user info from facebook sign in
            Log.i("start activity", "facebook");
        }
        Intent intent = new Intent(context, AdActivity.class);
        context.startActivity(intent);
    }

    /* This gives us the information from google sign in,
     * returns user id -- email as a string */
    private static String getUserIdFromGoogleSignIn(GoogleSignInResult result, SharedPreferences prefs) {
        GoogleSignInAccount account = result.getSignInAccount();
        storeData(prefs, account.getEmail(), account.getDisplayName(), true);
        return null;
    }

    public static void storeData(SharedPreferences prefs, String userId, String name, Boolean isLoggedIn) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(NAME, name);
        editor.putString(USERID, userId);
        editor.putBoolean(ISLOGGEDIN, isLoggedIn);
        Log.i("storeData", prefs.getString(NAME, "user"));
        editor.commit();
    }

    public static String retrieveFirstName(String fullName) {
        return fullName.substring(0, fullName.indexOf(' '));
    }
}
