package app.developer.jtsingla.money;

import java.util.HashMap;

/**
 * Created by jssingla on 11/19/16.
 */

public class BankDetail {
    private boolean isValid;
    private String bankName;
    private String bankAccountNo;
    private String bankAccountHolderName;
    private String bankCountry;
    private String IFSC;         //only valid for India, will need to integrate other methods for other countries

    BankDetail() {
        this.isValid = new Boolean(false);
        this.bankAccountNo = new String();
        this.bankAccountHolderName = new String();
        this.bankCountry = new String();
        this.IFSC = new String();
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public void setBankName(String name) {
        this.bankName = name;
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

    public String getBankName() {
        return bankName;
    }
}
