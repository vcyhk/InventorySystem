package transaction.transactionImport;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ImportData {

    //Transactions
    private final StringProperty TID;
    private final StringProperty CID;
    private final StringProperty location;
    private final StringProperty transactionDate;
    private final StringProperty transactionAmount;
    private final StringProperty discountAmount;
    private final StringProperty netAmount;

    public ImportData(String TID, String CID, String location, String transactionDate,
                      String transactionAmount, String discountAmount, String netAmount) {
        this.TID = new SimpleStringProperty(TID);
        this.CID = new SimpleStringProperty(CID);
        this.location = new SimpleStringProperty(location);
        this.transactionDate = new SimpleStringProperty(transactionDate);
        this.transactionAmount = new SimpleStringProperty(transactionAmount);
        this.discountAmount = new SimpleStringProperty(discountAmount);
        this.netAmount = new SimpleStringProperty(netAmount);
    }

    public String getTID() {
        return TID.get();
    }

    public StringProperty TIDProperty() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID.set(TID);
    }

    public String getCID() {
        return CID.get();
    }

    public StringProperty CIDProperty() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID.set(CID);
    }

    public String getLocation() {
        return location.get();
    }

    public StringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public String getTransactionDate() {
        return transactionDate.get();
    }

    public StringProperty transactionDateProperty() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate.set(transactionDate);
    }

    public String getTransactionAmount() {
        return transactionAmount.get();
    }

    public StringProperty transactionAmountProperty() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount.set(transactionAmount);
    }

    public String getDiscountAmount() {
        return discountAmount.get();
    }

    public StringProperty discountAmountProperty() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount.set(discountAmount);
    }

    public String getNetAmount() {
        return netAmount.get();
    }

    public StringProperty netAmountProperty() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount.set(netAmount);
    }

    public String toString(){
        return TID+" "+CID+" "+location+" "+transactionDate+" "+transactionAmount+" "+discountAmount+" "+netAmount;
    }


}
