package app.developer.jtsingla.money;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;
import static app.developer.jtsingla.money.EnterActivity.ISLOGGEDIN;
import static app.developer.jtsingla.money.EnterActivity.LOGINFO;
import static app.developer.jtsingla.money.EnterActivity.loggedInUser;
import static app.developer.jtsingla.money.FacebookLogin.globalFacebookLogin;
import static app.developer.jtsingla.money.FacebookLogin.setFacebookData;
import static app.developer.jtsingla.money.FireBaseAccess.getLoggedInUserDb;
import static app.developer.jtsingla.money.GoogleLogin.globalGoogleLogin;
import static app.developer.jtsingla.money.getUserInfo.startAdActivity;
import static app.developer.jtsingla.money.getUserInfo.storeData;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private int RC_SIGN_IN = 300;

    private static FireBaseAccess fireBaseAccess;

    private FacebookLogin facebookLogin;
    private GoogleLogin googleLogin;
    private FirebaseAuth mAuthManual;
    private FirebaseAuth.AuthStateListener mAuthListenerManual;
    private FirebaseUser userForEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        Instantiate sign in via Facebook.
        */
        facebookLogin = new FacebookLogin();
        facebookLogin.setCallbackManager(createCallBackManager(getApplicationContext()));
        setContentView(R.layout.activity_login);
         /*
        Instantiate Sign in via Google.
         */
        googleLogin = new GoogleLogin();
        googleLogin.setClient(googleLogin.createClient(this, RC_SIGN_IN, R.id.google_sign_in_button_log_in));

        AppEventsLogger.activateApp(this);
        try {
            setupActionBar();
        } catch (NullPointerException e) {
            return;
        }
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        /* add Firebase Listener */
        mAuthManual = FirebaseAuth.getInstance();

        mAuthListenerManual = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userForEmail = user;
                    if (!user.isEmailVerified()) {
                        Log.i("Firebase Listener", "Email is not verified.");
                    } else {
                        Log.i("Firebase Listener", "Email is verified.");
                    }
                    // Open Log In Activity for login.
                    List<String> providers = user.getProviders();

                    if (providers.contains("google.com")) {
                        Log.i("Firebase Listener", "Provider is google");
                        if (globalGoogleLogin.getResult() != null) {
                            loggedInUser = user;
                            startAdActivity(getApplicationContext(), getUserInfo.logInMethod.Google,
                                    globalGoogleLogin.getResult());
                            return;
                        }
                    }

                    if (providers.contains("facebook.com")) {
                        Log.i("Firebase Listener", "Provider is facebook");
                        if (globalFacebookLogin.getLoginResult() != null) {
                            loggedInUser = user;
                            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                                    Arrays.asList("public_profile", "email"));
                            setFacebookData(getApplicationContext(), globalFacebookLogin.getPrefs(), globalFacebookLogin.getLoginResult());
                            return;
                        }
                        //
                    }

                    if (providers.contains("password")) {
                        Log.i("Firebase Listener", "Provider is firebase/manual");
                        // send verification email TODO -- resend verification email for sending email
                        if (user.isEmailVerified()) {
                            loggedInUser = user;

                            // get data from loggedInUserInDb
                            UserDb userdb = getLoggedInUserDb(user);
                            // store data in shared prefs.
                            if (userdb == null) {
                                /* this will happen when listener is invoked while attaching */
                                return;
                            }
                            storeData(getSharedPreferences(LOGINFO, MODE_PRIVATE),
                                    userdb.getUserId(), userdb.getUserName(), true, getUserInfo.logInMethod.Manual.getMethod());
                            startAdActivity(LoginActivity.this, getUserInfo.logInMethod.Manual, null);
                        } else {
                            showProgress(false);
                            userForEmail = user;
                            Toast.makeText(LoginActivity.this, "Please verify your email first from the " +
                                    "email sent to you", Toast.LENGTH_SHORT).show();
                        }

                    }
                    Log.d("Firebase Listener", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("FireBase Listener", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuthManual.addAuthStateListener(mAuthListenerManual);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuthManual.removeAuthStateListener(mAuthListenerManual);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() throws NullPointerException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            try {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } catch (NullPointerException e) {
                Log.e("setUpActionBar", e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (password.isEmpty()) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            // perform sign in
            mAuthManual.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("log in", "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w("log in", "signInWithEmail", task.getException());

                                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    // email does not exist
                                    Toast.makeText(LoginActivity.this, "This email Id does not exist." +
                                            " Please register first.",
                                            Toast.LENGTH_SHORT).show();
                                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    // wrong password
                                    Toast.makeText(LoginActivity.this, "Wrong password. Please try again.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Authentication failed. Please try again.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                showProgress(false);
                            }

                            // ...
                        }
                    });
        }
    }

    private boolean isEmailValid (String email) {
        //TODO: Replace this with your own logic
        // check if email exists in db.
        // this will be taken care by firebase, we can just return true from here.
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    public void log_in_log_in(View v) {
        attemptLogin();
    }

    public void sign_up_log_in(View v) {
        // if not logged in, open signUpActivity
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Sign in", "Trying sign in");
        showProgress(true);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            googleLogin.setResult(result);
            globalGoogleLogin.setResult(result);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.e("Sign in error", "Google sign in was not successful");
            }
        } else {
            facebookLogin.getCallbackManager().onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
                Log.e("Sign in error", "Facebook sign in was successful");
            } else {
                Log.e("Sign in error", "Facebook sign in was not successful");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("google sign in", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        //googleLogin.getmAuthGoogle().signInWithCredential(credential)
        mAuthManual.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("google sign in", "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("facebook sign in", "signInWithCredential", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(LoginActivity.this, "This account has been disabled.",
                                        Toast.LENGTH_SHORT).show();
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this, "This account has expired.",
                                        Toast.LENGTH_SHORT).show();
                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(LoginActivity.this, "This email address is being used" +
                                                " by some other account.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            showProgress(false);
                        }
                        // ...
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("facebook sign in", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
       // facebookLogin.getmAuthFacebook().signInWithCredential(credential)
        mAuthManual.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("facebook sign in", "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("facebook sign in", "signInWithCredential", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(LoginActivity.this, "This account has been disabled.",
                                        Toast.LENGTH_SHORT).show();
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this, "This account has expired.",
                                        Toast.LENGTH_SHORT).show();
                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(LoginActivity.this, "This email address is being used" +
                                        " by some other account.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            LoginManager.getInstance().logOut();
                            showProgress(false);
                        }

                        // ...
                    }
                });
    }

    public CallbackManager createCallBackManager(final Context context) {
        FacebookSdk.sdkInitialize(context);
        final CallbackManager callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.i("facebook", "success");
                globalFacebookLogin.setLoginResult(loginResult);
                globalFacebookLogin.setPrefs(getSharedPreferences(EnterActivity.LOGINFO, MODE_PRIVATE));
                handleFacebookAccessToken(loginResult.getAccessToken());
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

    public void log_in_forgot_password(View v) {
        mEmailView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            /* do we need to have userForEmail here ?*/
            mAuthManual.sendPasswordResetEmail(email);
            Toast.makeText(LoginActivity.this, "Please check your inbox. " +
                    "Follow the link to reset the password.", Toast.LENGTH_SHORT).show();
        }
    }

    public void log_in_resend_email(View v) {
        mEmailView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (userForEmail != null) {
                userForEmail.sendEmailVerification();
                Toast.makeText(LoginActivity.this, "Email has been sent to you, " +
                        "Please open the email and click on the link.", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("email resend", "Some error occured while sending email");
                Toast.makeText(LoginActivity.this, "Some error occured. Please try logging in again. " +
                        "Sorry for the inconvenience. Your email may already be verified." +
                        " Try logging in with correct credentials.", Toast.LENGTH_LONG /* big message */).show();
            }
            // send verification email.
        }
    }
}

