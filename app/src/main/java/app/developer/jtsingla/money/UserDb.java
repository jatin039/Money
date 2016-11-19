package app.developer.jtsingla.money;

import com.facebook.login.LoginManager;

import java.util.HashMap;

/**
 * Created by jssingla on 11/19/16.
 */

public class UserDb {
    private String userName;   // first name + last name
    private String userId; // email or other log in id mentioned by user
    private String email;  // email - could be same as user id, could not be available if he has not provided
    private String logInVia;  // log in medium -- google, facebook and manual as of now
    private BankDetail bankDetail;

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

    public void setBankDetail(Boolean isValid, String accountNumber,
                              String accountHolderName, String country, String ifsc) {
        bankDetail.setValid(isValid);
        bankDetail.setBankAccountNo(accountNumber);
        bankDetail.setBankAccountHolderName(accountHolderName);
        bankDetail.setBankCountry(country);
        bankDetail.setIFSC(ifsc);
    }
}
