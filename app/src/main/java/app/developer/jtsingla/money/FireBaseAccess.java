package app.developer.jtsingla.money;

/**
 * Created by jssingla on 11/26/16.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class FireBaseAccess {

    public static FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    private static String TAG = "Firebase Access";
/*

    public static FirebaseAuth.AuthStateListener createAuthStateListener(final boolean sign_in) {
        FirebaseAuth.AuthStateListener listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (sign_in) {
                    // trying to log in
                    if (user != null) {
                        // User is signed in
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                        if (user.isEmailVerified()) {
                            Log.d(TAG, "email is verified");
                        } else {
                            user.sendEmailVerification();
                            Log.d(TAG, "email is not verified");
                        }
                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                } else {
                    // trying to sign up
                    // send verification email, if the user is not already verified.
                    if (!user.isEmailVerified()) {
                        user.sendEmailVerification();
                    }
                }
                // ...
            }
        };
        return listener;
    }

    public static OnCompleteListener<AuthResult> createOnCompleteListener(final Context context,
                                                                          final boolean sign_in) {
        return new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!sign_in) {
                    // sign up
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithEmail", task.getException());
                        // sign out any user, if sign up attempt fails
                        mAuth.signOut();
                        if (task.getException().toString().contains("The email address is" +
                                " already in use by another account.")) {
                            // this email address is already in use.
                            Toast.makeText(context, "The email address is already " +
                                    "in use by another account. Please try to log in.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "We regret that your sign up attempt failed. " +
                                            "Please try again.",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // if successful sign up.
                        Toast.makeText(context, "We have sent you a verification email. Please " +
                                "verify that and then log in",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    // sign _in
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithEmail", task.getException());
                        // sign out any user if log in attempt fails.
                        mAuth.signOut();
                        if (task.getException().toString().contains("There is no user record " +
                                "corresponding to this identifier." +
                                " The user may have been deleted.")) {
                            Toast.makeText(context, "This email address is not registered with us." +
                                    " Please sign up first.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Authentication failed. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                // ...
            }
        };
    }
*/

    FireBaseAccess(FirebaseAuth.AuthStateListener listener) {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = listener;
    }

    public void addListenerToTask() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void removeListenerFromTask() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /*public boolean addUser(String email, String password, Context context) {
        Task<AuthResult> task = mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(createOnCompleteListener(context, false));
        return task.isSuccessful();
    }

    public boolean signInUser(String email, String password, final Context context) {
        Task<AuthResult> task = mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(createOnCompleteListener(context, true));
        return task.isSuccessful();
    }*/

    public void sendVerificationEmail(String email) {
        Log.i(TAG, "verification email was sent");
        mAuth.getCurrentUser().sendEmailVerification();
    }

    public boolean isEmailVerified() {
        return mAuth.getCurrentUser().isEmailVerified();
    }

    public boolean isUserSignedIn() {
        return (mAuth.getCurrentUser() == null) ? false : true;
    }

    public static void logOutManual() {
        mAuth.signOut();
    }
}
