package app.developer.jtsingla.money;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

/**
 * Created by jssingla on 11/13/16.
 */

public class FacebookLogin {
    public static CallbackManager FacebookLogin(final Context context) {
        FacebookSdk.sdkInitialize(context);
        CallbackManager callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            }
            @Override
            public void onCancel() {
                //FixMe // TODO
                // do nothing?
            }

            @Override
            public void onError(FacebookException error) {
                //TODO
            }
        });
        return callbackManager;
    }
}
