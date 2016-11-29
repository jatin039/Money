package app.developer.jtsingla.money;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import static app.developer.jtsingla.money.EnterActivity.NAME;
import static app.developer.jtsingla.money.getUserInfo.startAdActivityForFacebook;
import static app.developer.jtsingla.money.getUserInfo.storeData;

/**
 * Created by jssingla on 11/13/16.
 */

public class FacebookLogin {
    public static FacebookLogin globalFacebookLogin = new FacebookLogin();  // object for storing facebook log in results;
    private SharedPreferences prefs;
    private LoginResult loginResult;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuthFacebook;

    FacebookLogin() {
        this.loginResult = null;
        this.callbackManager = null;
        this.mAuthFacebook = null;
        this.prefs = null;
    }

    public void setPrefs(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    public void setCallbackManager(CallbackManager callbackManager) {
        this.callbackManager = callbackManager;
    }

    public CallbackManager getCallbackManager() {
        return this.callbackManager;
    }

    public void setLoginResult(LoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public LoginResult getLoginResult() {
        return this.loginResult;
    }

    public void setmAuthFacebook(FirebaseAuth mAuthFacebook) {
        this.mAuthFacebook = mAuthFacebook;
    }

    public FirebaseAuth getmAuthFacebook() {
        return this.mAuthFacebook;
    }

    public static void setFacebookData(final Context context, final SharedPreferences prefs, final LoginResult loginResult)
    {
        Log.i("setfacebook", "success");
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            Log.i("Response",response.toString());

                            if (response.getJSONObject() == null) {
                                Log.i("Json", "null");
                                return;
                            }
                            String email = response.getJSONObject().getString("email");
                            String firstName = response.getJSONObject().getString("first_name");
                            String lastName = response.getJSONObject().getString("last_name");

                            Profile profile = Profile.getCurrentProfile();
                            String id = profile.getId();

                            Log.i("Login" + "Email", email);
                            Log.i("Login"+ "FirstName", firstName);
                            Log.i("Login" + "LastName", lastName);

                            storeData(prefs, (email != null) ? email : id, firstName+ " " + lastName, true /* logged in */,
                                    "facebook" /* log in method */);

                            Log.i("Login", prefs.getString(NAME, "user"));

                            startAdActivityForFacebook(context);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email, first_name, last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static void logOutFacebook() {
        LoginManager.getInstance().logOut();
    }
}