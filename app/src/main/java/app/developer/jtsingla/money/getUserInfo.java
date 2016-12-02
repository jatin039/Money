package app.developer.jtsingla.money;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;
import static app.developer.jtsingla.money.EnterActivity.ISLOGGEDIN;
import static app.developer.jtsingla.money.EnterActivity.LOGGEDINVIA;
import static app.developer.jtsingla.money.EnterActivity.NAME;
import static app.developer.jtsingla.money.EnterActivity.USERID;
import static app.developer.jtsingla.money.EnterActivity.loggedInUser;
import static app.developer.jtsingla.money.FacebookLogin.globalFacebookLogin;
import static app.developer.jtsingla.money.FireBaseAccess.getLoggedInUserDb;
import static app.developer.jtsingla.money.FireBaseAccess.saveUserToDb;
import static app.developer.jtsingla.money.FireBaseAccess.updateEmailInDb;
import static app.developer.jtsingla.money.FireBaseAccess.updateLogInViaInDb;
import static app.developer.jtsingla.money.FireBaseAccess.updateUserIdInDb;
import static app.developer.jtsingla.money.FireBaseAccess.updateUserNameInDb;
import static app.developer.jtsingla.money.GoogleLogin.globalGoogleLogin;

/**
 * Created by jssingla on 11/19/16.
 */

public class getUserInfo {
    public enum logInMethod {
        Google("google"),
        Facebook("facebook"),
        Manual("manual");
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /* This gives us the information from google sign in,
     * returns user id -- email as a string */
    private static String getUserIdFromGoogleSignIn(GoogleSignInResult result, SharedPreferences prefs) {
        GoogleSignInAccount account = result.getSignInAccount();
        storeData(prefs, account.getEmail(), account.getDisplayName(), true, "google");
        return null;
    }

    public static void storeData(SharedPreferences prefs, String userId, String name, Boolean isLoggedIn,
                                 String logInMethod) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(NAME, name);
        editor.putString(USERID, userId);
        editor.putBoolean(ISLOGGEDIN, isLoggedIn);
        editor.putString(LOGGEDINVIA, logInMethod);
        editor.commit();
        Log.i("storeData", prefs.getString(NAME, "user"));
        // save this data in Firebase DB
        if (loggedInUser == null) {
            /* do not proceed with data save */
            return;
        }
        UserDb userDb = getLoggedInUserDb(loggedInUser);
        /* update name */
        updateUserNameInDb(loggedInUser.getUid(), name);
        /* update email */
        updateEmailInDb(loggedInUser.getUid(), userId);
        /* update user Id */
        updateUserIdInDb(loggedInUser.getUid(), userId);
        /* upadte log in via */
        updateLogInViaInDb(loggedInUser.getUid(), logInMethod);
    }

    public static String retrieveFirstName(String fullName) {
        // if name doesn't contain a space, return whole name.
        if (!fullName.contains(" ")) return fullName;
        return fullName.substring(0, fullName.indexOf(' '));
    }

    public static void log_out_from_method(SharedPreferences prefs) {
        String logInMethod = prefs.getString(LOGGEDINVIA, "not_logged_in");

        // store user earnings in DB. TODO
        loggedInUser = null;
        if (logInMethod.equals("facebook")) {
            //LoginManager.getInstance().logOut();
            Log.i("log_out", "logged_out_of_facebook");
            globalFacebookLogin.setLoginResult(null);
            FirebaseAuth.getInstance().signOut();
            //logOutFacebook();
        } else if (logInMethod.equals("google")) {
            Log.i("log_out", "logged_out_of_gogle");
            // can't log out without google client, TODO: analyze if really required
            globalGoogleLogin.setResult(null);
            FirebaseAuth.getInstance().signOut();
        } else if (logInMethod.equals("manual")) {
            Log.i("log_out", "logged_out_of_manual");
            FirebaseAuth.getInstance().signOut();
        } else {
            Log.i("log_out", "already_logged_out");
        }
        storeData(prefs, "username", "user ", false, "not_logged_in");
    }

    public static void startAdActivityForFacebook(final Context context) {
        startAdActivity(context, getUserInfo.logInMethod.Facebook, null);
    }
}
