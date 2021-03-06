package app.developer.jtsingla.money;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by jssingla on 11/13/16.
 */

public class GoogleLogin {
    public static GoogleLogin globalGoogleLogin = new GoogleLogin();
    private GoogleApiClient client;
    private GoogleSignInResult result;

    private FirebaseAuth mAuthGoogle;

    GoogleLogin() {
        this.client = null;
        this.result = null;
        this.mAuthGoogle = null;
    }

    public void setClient(GoogleApiClient client) {
        this.client = client;
    }

    public void setResult(GoogleSignInResult result) {
        this.result = result;
    }

    public GoogleApiClient getClient() {
        return this.client;
    }

    public GoogleSignInResult getResult() {
        return this.result;
    }

    public void setmAuthGoogle(FirebaseAuth mAuthGoogle) {
        this.mAuthGoogle = mAuthGoogle;
    }

    public FirebaseAuth getmAuthGoogle() {
        return this.mAuthGoogle;
    }

    public GoogleApiClient createClient(final Activity context, final int RC_SIGN_IN,
                                            int button) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.google_client_id))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        final GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((FragmentActivity)context, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e("Connected Failed", "There was a problem connecting to internet.");
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.

        SignInButton signInButton = (SignInButton) context.findViewById(button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                context.startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        setGooglePlusButtonText(signInButton, "Sign In With Google");
        return mGoogleApiClient;
    }

    private static void setGooglePlusButtonText(SignInButton signInButton,
                                           String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(17);
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setText(buttonText);
                tv.setPadding(0,0,20,0);
                return;
            }
        }
    }
}
