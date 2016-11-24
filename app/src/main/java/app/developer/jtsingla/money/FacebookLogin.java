package app.developer.jtsingla.money;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static app.developer.jtsingla.money.EnterActivity.LOGINFO;
import static app.developer.jtsingla.money.EnterActivity.NAME;
import static app.developer.jtsingla.money.getUserInfo.startAdActivityForFacebook;
import static app.developer.jtsingla.money.getUserInfo.storeData;

/**
 * Created by jssingla on 11/13/16.
 */

public class FacebookLogin {
    public static CallbackManager FacebookLogin(final Context context) {
        FacebookSdk.sdkInitialize(context);
        final SharedPreferences prefs = context.getSharedPreferences(LOGINFO, MODE_PRIVATE);
        final CallbackManager callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.i("facebook", "success");
                setFacebookData(context, prefs, loginResult);

            }
            @Override
            public void onCancel() {
                //FixMe // TODO
                Log.i("facebook", "cancel");
                // do nothing?
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("facebook", "error");
                //TODO
            }
        });
        return callbackManager;
    }

    private static void setFacebookData(final Context context, final SharedPreferences prefs, final LoginResult loginResult)
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
}
