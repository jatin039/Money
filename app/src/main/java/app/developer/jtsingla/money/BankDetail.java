package app.developer.jtsingla.money;

import java.util.HashMap;

/**
 * Created by jssingla on 11/19/16.
 */

public class BankDetail {
    private boolean isValid;
    private String bankAccountNo;
    private String bankAccountHolderName;
    private String bankCountry;
    private String IFSC;         //only valid for India, will need to integrate other methods for other countries

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public void setBankAccountNo(String bankAccountNo) {
        this.bankAccountNo = bankAccountNo;
    }

    public void setBankAccountHolderName(String bankAccountHolderName) {
        this.bankAccountHolderName = bankAccountHolderName;
    }

    public void setBankCountry(String bankCountry) {
        this.bankCountry = bankCountry;
    }

    public void setIFSC(String ifsc) {
        this.IFSC = ifsc;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getBankAccountNo() {
        return bankAccountNo;
    }

    public String getBankAccountHolderName() {
        return bankAccountHolderName;
    }

    public String getBankCountry() {
        return bankCountry;
    }

    public String getIFSC() {
        return IFSC;
    }
}
