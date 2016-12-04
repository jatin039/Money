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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static app.developer.jtsingla.money.EnterActivity.loggedInUser;


public class FireBaseAccess {

    public static FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    private static String TAG = "Firebase Access";
    private static UserDb loggedInUserDb;

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

    public static UserDb getLoggedInUserDb(FirebaseUser user) {
        if (user == null) {
            return null;
        }
        DatabaseReference ref = getDbRef(user.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loggedInUserDb = dataSnapshot.getValue(UserDb.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
        return loggedInUserDb;
    }

    private static DatabaseReference getDbRef(String id) {
        return FirebaseDatabase.getInstance().getReference().child("users").child(id);
    }

    public static void updateUserNameInDb(String id, String name) {
        DatabaseReference ref = getDbRef(id);
        ref.child("userName").setValue(name);
    }

    public static void updateEmailInDb(String id, String email) {
        DatabaseReference ref = getDbRef(id);
        ref.child("email").setValue(email);
    }

    public static void updateUserIdInDb(String id, String userId) {
        DatabaseReference ref = getDbRef(id);
        ref.child("userId").setValue(userId);
    }

    public static void updateLogInViaInDb(String id, String logInVia) {
        DatabaseReference ref = getDbRef(id);
        ref.child("logInVia").setValue(logInVia);
    }

    public static void updateBankDetailsInDb(String id, BankDetail bankDetail) {
        DatabaseReference ref = getDbRef(id);
        ref.child("bankDetail").setValue(bankDetail);
    }

    public static void updateTotalWatchedInDb(String id, Long totalWatched) {
        DatabaseReference ref = getDbRef(id);
        ref.child("totalWatched").setValue(totalWatched);
    }

    public static void updateFeedbackInDb(String id, String feedback) {
        DatabaseReference ref = getDbRef(id);
        List<String> feedbacks = loggedInUserDb.getFeedback();
        feedbacks.add(feedback);
        ref.child("feedback").setValue(feedbacks);
    }

    public static void saveUserToDb(String id, UserDb userDb) {
        // this will be called only for new users
        DatabaseReference ref = getDbRef(id);
        ref.setValue(userDb);
    }
}
