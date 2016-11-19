package app.developer.jtsingla.money;

import com.facebook.login.LoginManager;

import java.util.HashMap;

/**
 * Created by jssingla on 11/19/16.
 */

public class UserDb {

    private final static long FACTOR = 1000000000;

    private String userName;   // first name + last name
    private String userId; // email or other log in id mentioned by user
    private String email;  // email - could be same as user id, could not be available if he has not provided
    private String logInVia;  // log in medium -- google, facebook and manual as of now
    private BankDetail bankDetail;
    private Long totalWatched;

    public void setUserName(String fullName) {
        this.userName = fullName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLogInVia(String method) {
        this.logInVia = method;
    }

    public void setTotalWatched(Long totalWatched) {
        this.totalWatched = totalWatched;
    }

    public void setBankDetail(BankDetail bankDetail) {
        this.bankDetail = bankDetail;
    }

    public Long getTotalWatched() {
        return this.totalWatched;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getEmail() {
        return this.email;
    }

    public String getLogInVia() {
        return this.logInVia;
    }

    public BankDetail getBankDetail() {
        return this.bankDetail;
    }
}
